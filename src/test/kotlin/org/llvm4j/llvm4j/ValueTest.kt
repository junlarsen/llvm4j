package org.llvm4j.llvm4j

import org.junit.jupiter.api.Test
import org.llvm4j.llvm4j.testing.assertIsErr
import org.llvm4j.llvm4j.testing.assertIsNone
import org.llvm4j.llvm4j.testing.assertIsOk
import org.llvm4j.llvm4j.testing.assertIsSome
import org.llvm4j.llvm4j.util.None
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ConstantIntTest {
    @Test fun `Test ConstantInt properties`() {
        val ctx = Context()
        val i32 = ctx.getInt32Type()
        val subject1 = i32.getConstant(100)
        val subject2 = i32.getConstantNull()
        val subject3 = i32.getConstantPointerNull()
        val subject4 = i32.getConstantUndef()
        val subject5 = i32.getConstant(-100)

        assertEquals(i32.ref, subject1.getType().ref)
        assertEquals(ValueKind.ConstantInt, subject1.getValueKind())
        assertEquals("i32 100", subject1.getAsString())
        assertEquals(100, subject1.getSignExtendedValue())
        assertEquals(100, subject1.getZeroExtendedValue())
        assertTrue { subject1.isConstant() }
        assertFalse { subject1.isUndef() }
        assertFalse { subject1.isNull() }

        assertTrue { subject2.isNull() }
        assertTrue { subject3.isNull() }
        assertTrue { subject4.isUndef() }
        assertEquals(2 * (Int.MAX_VALUE.toLong() + 1) - 100, subject5.getZeroExtendedValue())
        assertEquals(-100, subject5.getSignExtendedValue())
}

@Test fun `Test get all ones`() {
    val ctx = Context()
    val i1 = ctx.getInt1Type()
    val i8 = ctx.getInt8Type()
    val i16 = ctx.getInt16Type()
        val i32 = ctx.getInt32Type()
        val i64 = ctx.getInt64Type()
        val i128 = ctx.getInt128Type()

        for (type in listOf(i1, i8, i16, i32, i64, i128)) {
            val subject = type.getAllOnes()

            assertEquals(-1, subject.getSignExtendedValue())
        }
    }
}

class ConstantFloatTest {
    @Test fun `Test ConstantFloat properties`() {
        val ctx = Context()
        val float = ctx.getFloatType()
        val subject1 = float.getConstant(100.0)
        val subject2 = float.getConstantNull()
        val subject3 = float.getConstantPointerNull()
        val subject4 = float.getConstantUndef()

        assertEquals(float.ref, subject1.getType().ref)
        assertEquals(ValueKind.ConstantFP, subject1.getValueKind())
        assertEquals("float 1.000000e+02", subject1.getAsString())
        assertEquals(Pair(100.0, false), subject1.getValue())
        assertTrue { subject1.isConstant() }
        assertFalse { subject1.isUndef() }
        assertFalse { subject1.isNull() }

        assertTrue { subject2.isNull() }
        assertTrue { subject3.isNull() }
        assertTrue { subject4.isUndef() }
    }

    @Test fun `Test get all ones`() {
        val ctx = Context()
        val float = ctx.getFloatType()
        val bfloat = ctx.getBFloatType()
        val double = ctx.getDoubleType()
        val x86fp80 = ctx.getX86FP80Type()
        val fp128 = ctx.getFP128Type()
        val ppcfp128 = ctx.getPPCFP128Type()
        val expected = mapOf(
            float to Pair(Double.NaN, false),
            bfloat to Pair(Double.NaN, false),
            double to Pair(Double.NaN, false),
            x86fp80 to Pair(Double.NaN, true),
            fp128 to Pair(Double.NaN, true),
            ppcfp128 to Pair(Double.NaN, false)
        )

        for ((type, result) in expected) {
            val (value, lossy) = result
            val subject = type.getAllOnes().getValue()

            assertEquals(value, subject.first)
            assertEquals(lossy, subject.second)
        }
    }
}

class FunctionTest {
    @Test fun `Test Function properties`() {
        val ctx = Context()
        val i32 = ctx.getInt32Type()
        val void = ctx.getVoidType()
        val fnTy = ctx.getFunctionType(void, i32, i32)
        val mod = ctx.createModule("test_module")
        val subject1 = mod.addFunction("test_fn", fnTy)

        assertEquals(TypeKind.Pointer, subject1.getType().getTypeKind())
        assertEquals(ValueKind.Function, subject1.getValueKind())
        assertEquals("declare void @test_fn(i32, i32)\n", subject1.getAsString())
        assertEquals("test_fn", subject1.getName())
        assertTrue { subject1.isConstant() }
        assertFalse { subject1.isUndef() }
        assertFalse { subject1.isNull() }

        assertFalse { subject1.hasPersonalityFunction() }
        assertIsNone(subject1.getPersonalityFunction())
        assertEquals(CallConvention.C, subject1.getCallConvention())
        assertEquals(2, subject1.getParameterCount())
        assertIsNone(subject1.getGC())

        subject1.setName("test_main")
        subject1.setGC("shadow-stack")
        subject1.setCallConvention(CallConvention.Fast)

        assertIsSome(subject1.getGC())
        assertIsSome(mod.getFunction("test_main"))
        assertEquals("test_main", subject1.getName())
        assertEquals("shadow-stack", subject1.getGC().get())
        assertEquals(CallConvention.Fast, subject1.getCallConvention())
    }

    @Test fun `Test function attributes`() {
        val ctx = Context()
        val i32 = ctx.getInt32Type()
        val void = ctx.getVoidType()
        val fnTy = ctx.getFunctionType(void, i32, i32)
        val mod = ctx.createModule("test_module")
        val subject1 = mod.addFunction("test_fn", fnTy)

        subject1.addTargetDependentAttribute("k", "v")

        assertEquals(1, subject1.getAttributeCount(AttributeIndex.Function))
    }

    @Test fun `Test function parameters`() {
        val ctx = Context()
        val i8 = ctx.getInt32Type()
        val void = ctx.getVoidType()
        val fnTy = ctx.getFunctionType(void, i8, i8)
        val mod = ctx.createModule("test_module")
        val subject1 = mod.addFunction("test_fn", fnTy)
        val (subject2, subject3) = subject1.getParameters()

        subject2.setAlignment(8)
        subject2.setName("a")

        assertEquals("a", subject2.getName())
        assertEquals(2, subject1.getParameterCount())
        assertEquals(subject2.ref, subject1.getParameter(0).get().ref)
        assertEquals(subject3.ref, subject1.getParameter(1).get().ref)

        assertEquals(subject1.ref, subject2.getParent().ref)
    }

    @Test fun `Test personality functions`() {
        val ctx = Context()
        val mod = ctx.createModule("test_module")
        val i32 = ctx.getInt32Type()
        val i8 = ctx.getInt8Type()
        val personalityFnTy = ctx.getFunctionType(i32, isVariadic = true)
        val primaryFnTy = ctx.getFunctionType(i8, i32)
        val subject1 = mod.addFunction("test_main", primaryFnTy)
        val subject2 = mod.addFunction("personality", personalityFnTy)

        assertFalse { subject1.hasPersonalityFunction() }
        subject1.setPersonalityFunction(subject2)

        assertTrue { subject1.hasPersonalityFunction() }
        assertEquals(subject2.ref, subject1.getPersonalityFunction().get().ref)
    }

    @Test fun `Test deleting function from module`() {
        val ctx = Context()
        val i32 = ctx.getInt32Type()
        val void = ctx.getVoidType()
        val fnTy = ctx.getFunctionType(void, i32, i32)
        val mod = ctx.createModule("test_module")
        val subject1 = mod.addFunction("test_fn", fnTy)

        assertIsSome(mod.getFunction("test_fn"))
        assertEquals(subject1.ref, mod.getFunction("test_fn").get().ref)

        subject1.delete()

        assertIsNone(mod.getFunction("test_fn"))
    }
}

class GlobalAliasTest {
    @Test fun `Test GlobalAlias properties`() {
        val ctx = Context()
        val mod = ctx.createModule("test_module")
        val i32 = ctx.getInt32Type()
        val i32ptr = ctx.getPointerType(i32)
        val value1 = i32.getConstantPointerNull()
        val value2 = i32ptr.get().getConstantUndef()
        val subject = mod.addGlobalAlias("global_alias", i32ptr.get(), value1)

        assertEquals(TypeKind.Pointer, subject.getType().getTypeKind())
        assertEquals(ValueKind.GlobalAlias, subject.getValueKind())
        assertEquals("@global_alias = alias i32, i32 null\n", subject.getAsString())
        assertEquals("global_alias", subject.getName())
        assertTrue { subject.isConstant() }
        assertFalse { subject.isNull() }
        assertFalse { subject.isUndef() }
        assertEquals(value1.ref, subject.getValue().ref)

        subject.setName("global_alias1")
        subject.setValue(value2)

        assertEquals("global_alias1", subject.getName())
        assertEquals(value2.ref, subject.getValue().ref)
    }
}

class GlobalValueTest {
    @Test fun `Test GlobalValue properties`() {
        val ctx = Context()
        val mod = ctx.createModule("test_module")
        val i32 = ctx.getInt32Type()
        val fnTy = ctx.getFunctionType(i32, i32)
        val subject = mod.addFunction("test_main", fnTy)

        assertIsNone(subject.getSection())
        assertEquals(mod.ref, subject.getModule().ref)
        assertEquals(0, subject.getPreferredAlignment())
        assertEquals(Linkage.External, subject.getLinkage())
        assertEquals(Visibility.Default, subject.getVisibility())
        assertEquals(DLLStorageClass.Default, subject.getStorageClass())
        assertEquals(UnnamedAddress.None, subject.getUnnamedAddress())
        assertEquals(TypeKind.Function, subject.getValueType().getTypeKind())

        // these do not make sense, but llvm allows it
        subject.setSection(".bss")
        subject.setPreferredAlignment(16)
        subject.setLinkage(Linkage.Appending)

        assertIsSome(subject.getSection())
        assertEquals(".bss", subject.getSection().get())
        assertEquals(16, subject.getPreferredAlignment())
        assertEquals(Linkage.Appending, subject.getLinkage())

        for (value in Visibility.entries) {
            subject.setVisibility(value)
            assertEquals(value, subject.getVisibility())
        }
        for (value in DLLStorageClass.entries) {
            subject.setStorageClass(value)
            assertEquals(value, subject.getStorageClass())
        }
        for (value in UnnamedAddress.entries) {
            subject.setUnnamedAddress(value)
            assertEquals(value, subject.getUnnamedAddress())
        }
    }
}

class GlobalVariableTest {
    @Test fun `Test GlobalVariable properties`() {
        val ctx = Context()
        val mod = ctx.createModule("test_module")
        val void = ctx.getVoidType()
        val i32 = ctx.getInt32Type()
        val res1 = mod.addGlobalVariable("test_var1", i32, None)
        val res2 = mod.addGlobalVariable("test_var2", void, None)
        val value = i32.getConstant(1L)

        assertIsOk(res1)
        assertIsErr(res2)

        val subject1 = res1.get()

        assertEquals(TypeKind.Pointer, subject1.getType().getTypeKind())
        assertEquals(ValueKind.GlobalVariable, subject1.getValueKind())
        assertEquals("@test_var1 = external global i32", subject1.getAsString())
        assertEquals("test_var1", subject1.getName())
        assertTrue { subject1.isConstant() }
        assertFalse { subject1.isNull() }
        assertFalse { subject1.isUndef() }

        assertFalse { subject1.isExternallyInitialized() }
        assertFalse { subject1.isThreadLocal() }
        assertFalse { subject1.isImmutable() }
        assertIsNone(subject1.getInitializer())
        assertEquals(ThreadLocalMode.NotThreadLocal, subject1.getThreadLocalMode())

        subject1.setExternallyInitialized(true)
        subject1.setThreadLocal(true)
        subject1.setImmutable(true)
        subject1.setInitializer(value)
        subject1.setThreadLocalMode(ThreadLocalMode.LocalDynamic)

        assertTrue { subject1.isExternallyInitialized() }
        assertTrue { subject1.isThreadLocal() }
        assertTrue { subject1.isImmutable() }
        assertIsSome(subject1.getInitializer())
        assertEquals(value.ref, subject1.getInitializer().get().ref)
        assertEquals(ThreadLocalMode.LocalDynamic, subject1.getThreadLocalMode())
    }

    @Test fun `Test deletion from parent module`() {
        val ctx = Context()
        val mod = ctx.createModule("test_module")
        val i32 = ctx.getInt32Type()
        val subject = mod.addGlobalVariable("test_var1", i32, None).get()

        assertIsSome(mod.getGlobalVariable("test_var1"))

        subject.delete()

        assertIsNone(mod.getGlobalVariable("test_var1"))
    }
}
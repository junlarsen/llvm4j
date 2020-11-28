package io.vexelabs.bitbuilder.llvm.unit.ir

import io.vexelabs.bitbuilder.llvm.ir.Context
import io.vexelabs.bitbuilder.llvm.ir.Module
import io.vexelabs.bitbuilder.llvm.ir.ValueKind
import io.vexelabs.bitbuilder.llvm.ir.types.FunctionType
import io.vexelabs.bitbuilder.llvm.ir.types.IntType
import io.vexelabs.bitbuilder.llvm.ir.types.VoidType
import io.vexelabs.bitbuilder.llvm.ir.values.constants.ConstantInt
import io.vexelabs.bitbuilder.llvm.setup
import org.spekframework.spek2.Spek
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal object ValueTest : Spek({
    setup()

    val context: Context by memoized()
    val module: Module by memoized()

    group("assigning a name to the value") {
        test("getting a manually set name") {
            assertFalse { context.isDiscardingValueNames() }

            val void = context.getVoidType()
            val fnTy = context.getFunctionType(void, variadic = false)
            val value = module.createFunction("NotTrue", fnTy).apply {
                setName("True")
            }

            assertEquals("True", value.getName())
        }

        test("default name is empty string") {
            val i1 = context.getIntType(1)
            val value = ConstantInt(i1, 0)

            assertEquals("", value.getName())
        }
    }

    test("finding the type of the value") {
        val type = context.getIntType(32)
        val value = ConstantInt(type, 100)
        val subject = value.getType()

        assertEquals(type.ref, subject.ref)
    }

    group("usage of singleton values") {
        test("constant undefined") {
            val undef = context.getIntType(1).getConstantUndef()

            assertTrue { undef.isConstant() }
            assertTrue { undef.isUndef() }
        }

        test("constant null pointer") {
            val value = context.getIntType(32).getConstantNullPointer()

            assertEquals(ValueKind.ConstantPointerNull, value.getValueKind())
            assertTrue { value.isConstant() }
            assertTrue { value.isNull() }
        }

        test("constant null") {
            val value = context.getIntType(32).getConstantNull()

            assertTrue { value.isConstant() }
            assertTrue { value.isNull() }
        }
    }

    test("usage of ConstAllOne") {
        val value = context.getIntType(1).getConstantAllOnes()
        val subject = value.getUnsignedValue()

        assertTrue { value.isConstant() }
        assertEquals(1, subject)
    }

    test("context is equal to its type's context") {
        val type = context.getIntType(1)
        val value = ConstantInt(type, 1)
        val subject = value.getContext()

        assertEquals(type.getContext().ref, subject.ref)
    }

    test("pulling a value in textual format") {
        val i32 = context.getIntType(32)
        val value = ConstantInt(i32, 100)
        val ir = "i32 100"

        assertEquals(ir, value.getIR().toString())
    }
})

package io.vexelabs.bitbuilder.llvm.unit.ir.values

import io.vexelabs.bitbuilder.llvm.ir.Context
import io.vexelabs.bitbuilder.llvm.ir.Module
import io.vexelabs.bitbuilder.llvm.ir.TypeKind
import io.vexelabs.bitbuilder.llvm.ir.types.IntType
import io.vexelabs.bitbuilder.llvm.ir.types.VectorType
import io.vexelabs.bitbuilder.llvm.ir.values.IntrinsicFunction
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import org.spekframework.spek2.Spek

internal class IntrinsicFunctionTest : Spek({
    test("performing lookup for intrinsic function") {
        val intrinsic = IntrinsicFunction("llvm.va_start")

        assertTrue { intrinsic.exists() }
    }

    test("looking up invalid intrinsic name fails") {
        assertFailsWith<IllegalArgumentException> {
            IntrinsicFunction("not.a.valid.intrinsic")
        }
    }

    test("overloaded intrinsics are marked overloaded") {
        val intrinsic = IntrinsicFunction("llvm.ctpop")

        assertTrue { intrinsic.isOverloaded() }
    }

    test("finding fully qualified name by overloaded arguments") {
        val ty = VectorType(IntType(8), 4)
        val intrinsic = IntrinsicFunction("llvm.ctpop")

        val overloaded = intrinsic.getOverloadedName(listOf(ty))

        assertEquals("llvm.ctpop.v4i8", overloaded)
    }

    test("the function name matches") {
        val intrinsic = IntrinsicFunction("llvm.va_start")

        assertEquals("llvm.va_start", intrinsic.getName())
    }

    test("the function declaration can be retrieved from the intrinsic") {
        val ty = VectorType(IntType(8), 4)
        val intrinsic = IntrinsicFunction("llvm.ctpop")
        val mod = Module("utils.ll")
        val fn = intrinsic.getDeclaration(mod, listOf(ty))

        assertTrue { fn.getIntrinsicId() == intrinsic.id }
    }

    test("the function type can be retrieved from intrinsic") {
        val intrinsic = IntrinsicFunction("llvm.va_start")
        val args = listOf(IntType(8).toPointerType())
        val types = intrinsic.getType(Context.getGlobalContext(), args)

        assertEquals(1, types.getParameterCount())
        assertEquals(
            TypeKind.Pointer, types.getParameterTypes().first().getTypeKind()
        )
    }
})

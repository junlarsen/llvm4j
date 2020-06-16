package dev.supergrecko.vexe.llvm.unit.ir.values

import dev.supergrecko.vexe.llvm.ir.Context
import dev.supergrecko.vexe.llvm.ir.Module
import dev.supergrecko.vexe.llvm.ir.TypeKind
import dev.supergrecko.vexe.llvm.ir.types.IntType
import dev.supergrecko.vexe.llvm.ir.types.VectorType
import dev.supergrecko.vexe.llvm.ir.values.IntrinsicFunction
import dev.supergrecko.vexe.test.TestSuite
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

internal class IntrinsicFunctionTest : TestSuite({
    describe("Search for intrinsic function") {
        val intrinsic = IntrinsicFunction("llvm.va_start")

        assertTrue { intrinsic.exists() }
    }

    describe("Invalid intrinsic name fails") {
        assertFailsWith<IllegalArgumentException> {
            IntrinsicFunction("not.a.valid.intrinsic")
        }
    }

    describe("Search for overloaded intrinsic") {
        val intrinsic = IntrinsicFunction("llvm.ctpop")

        assertTrue { intrinsic.isOverloaded() }
    }

    describe("Get name by overloaded intrinsic's arguments") {
        val ty = VectorType(IntType(8), 4)
        val intrinsic = IntrinsicFunction("llvm.ctpop")

        val overloaded = intrinsic.getOverloadedName(listOf(ty))

        assertEquals("llvm.ctpop.v4i8", overloaded)
    }

    describe("Intrinsic name matches getter") {
        val intrinsic = IntrinsicFunction("llvm.va_start")

        assertEquals("llvm.va_start", intrinsic.getName())
    }

    describe("Function declaration can be retrieved from intrinsic") {
        val ty = VectorType(IntType(8), 4)
        val intrinsic = IntrinsicFunction("llvm.ctpop")
        val mod = Module("utils.ll")
        val fn = intrinsic.getDeclaration(mod, listOf(ty))

        assertTrue { fn.getIntrinsicId() == intrinsic.id }
    }

    describe("Function type can be retrieved from intrinsic") {
        val intrinsic = IntrinsicFunction("llvm.va_start")
        val args = listOf(IntType(8).toPointerType())
        val types = intrinsic.getType(Context.getGlobalContext(), args)

        assertEquals(1, types.getParameterCount())
        assertEquals(
            TypeKind.Pointer, types.getParameterTypes().first().getTypeKind()
        )
    }
})

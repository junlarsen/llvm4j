package dev.supergrecko.kllvm.unit.ir.values

import dev.supergrecko.kllvm.unit.ir.Context
import dev.supergrecko.kllvm.unit.ir.Module
import dev.supergrecko.kllvm.unit.ir.TypeKind
import dev.supergrecko.kllvm.unit.ir.types.IntType
import dev.supergrecko.kllvm.unit.ir.types.VectorType
import dev.supergrecko.kllvm.utils.KLLVMTestCase
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test

internal class IntrinsicFunctionTest : KLLVMTestCase() {
    @Test
    fun `Search for intrinsic function`() {
        val intrinsic = IntrinsicFunction("llvm.va_start")

        assertTrue { intrinsic.exists() }
    }

    @Test
    fun `Invalid intrinsic name fails`() {
        assertFailsWith<IllegalArgumentException> {
            IntrinsicFunction("not.a.valid.intrinsic")
        }
    }

    @Test
    fun `Search for overloaded intrinsic`() {
        val intrinsic = IntrinsicFunction("llvm.ctpop")

        assertTrue { intrinsic.isOverloaded() }
    }

    @Test
    fun `Get name by overloaded intrinsic's arguments`() {
        val ty = VectorType(IntType(8), 4)
        val intrinsic = IntrinsicFunction("llvm.ctpop")

        val overloaded = intrinsic.getOverloadedName(listOf(ty))

        assertEquals("llvm.ctpop.v4i8", overloaded)
    }

    @Test
    fun `Intrinsic name matches getter`() {
        val intrinsic = IntrinsicFunction("llvm.va_start")

        assertEquals("llvm.va_start", intrinsic.getName())
    }

    @Test
    fun `Function declaration can be retrieved from intrinsic`() {
        val ty = VectorType(IntType(8), 4)
        val intrinsic = IntrinsicFunction("llvm.ctpop")
        val mod = Module("utils.ll")
        val fn = intrinsic.getDeclaration(mod, listOf(ty))

        assertTrue { fn.getIntrinsicId() == intrinsic.id }
    }

    @Test
    fun `Function type can be retrieved from intrinsic`() {
        val intrinsic = IntrinsicFunction("llvm.va_start")
        val args = listOf(IntType(8).toPointerType())
        val types = intrinsic.getType(Context.getGlobalContext(), args)

        assertEquals(1, types.getParameterCount())
        assertEquals(
            TypeKind.Pointer, types.getParameterTypes().first().getTypeKind()
        )
    }
}

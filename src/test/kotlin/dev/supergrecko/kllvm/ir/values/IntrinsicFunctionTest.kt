package dev.supergrecko.kllvm.ir.values

import dev.supergrecko.kllvm.ir.Context
import dev.supergrecko.kllvm.ir.Module
import dev.supergrecko.kllvm.ir.TypeKind
import dev.supergrecko.kllvm.ir.types.IntType
import dev.supergrecko.kllvm.ir.types.VectorType
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test

class IntrinsicFunctionTest {
    @Test
    fun `searching for an intrinsic`() {
        val intrinsic = IntrinsicFunction("llvm.va_start")

        assertTrue { intrinsic.exists() }
    }

    @Test
    fun `searching for unknown intrinsic`() {
        assertFailsWith<IllegalArgumentException> {
            IntrinsicFunction("not.a.valid.intrinsic")
        }
    }

    @Test
    fun `finding overloaded intrinsics`() {
        val intrinsic = IntrinsicFunction("llvm.ctpop")

        assertTrue { intrinsic.isOverloaded() }
    }

    @Test
    fun `finding overloaded name`() {
        val ty = VectorType(IntType(8), 4)
        val intrinsic = IntrinsicFunction("llvm.ctpop")

        val overloaded = intrinsic.getOverloadedName(listOf(ty))

        assertEquals("llvm.ctpop.v4i8", overloaded)
    }

    @Test
    fun `intrinsic names match`() {
        val intrinsic = IntrinsicFunction("llvm.va_start")

        assertEquals("llvm.va_start", intrinsic.getName())
    }

    @Test
    fun `fetching function declaration`() {
        val ty = VectorType(IntType(8), 4)
        val intrinsic = IntrinsicFunction("llvm.ctpop")
        val mod = Module("test.ll")
        val fn = intrinsic.getDeclaration(mod, listOf(ty))

        assertTrue { fn.getIntrinsicId() == intrinsic.id }
    }

    @Test
    fun `fetching type for intrinsic`() {
        val intrinsic = IntrinsicFunction("llvm.va_start")
        val args = listOf(IntType(8).toPointerType())
        val types = intrinsic.getType(Context.getGlobalContext(), args)

        assertEquals(1, types.getParameterCount())
        assertEquals(
            TypeKind.Pointer, types.getParameterTypes().first().getTypeKind()
        )
    }
}

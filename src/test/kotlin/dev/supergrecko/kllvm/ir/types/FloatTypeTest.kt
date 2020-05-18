package dev.supergrecko.kllvm.ir.types

import dev.supergrecko.kllvm.ir.TypeKind
import dev.supergrecko.kllvm.test.runAll
import java.lang.IllegalArgumentException
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import org.junit.jupiter.api.Test

class FloatTypeTest {
    @Test
    fun `Creation from user-land constructor`() {
        runAll(*FloatType.kinds.toTypedArray()) { it, _ ->
            val type = FloatType(it)

            assertEquals(it, type.getTypeKind())
        }
    }

    @Test
    fun `Creation via LLVM reference`() {
        val ref = FloatType(TypeKind.Float)
        val second = FloatType(ref.ref)

        assertEquals(TypeKind.Float, second.getTypeKind())
    }

    @Test
    fun `Attempting to use reference constructor with wrong type fails`() {
        val ref = IntType(32)

        assertFailsWith<IllegalArgumentException> {
            FloatType(ref.ref)
        }
    }
}

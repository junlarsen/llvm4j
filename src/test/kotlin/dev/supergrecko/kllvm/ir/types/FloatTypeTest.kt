package dev.supergrecko.kllvm.ir.types

import dev.supergrecko.kllvm.ir.TypeKind
import dev.supergrecko.kllvm.test.runAll
import org.junit.jupiter.api.Test
import java.lang.IllegalArgumentException
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class FloatTypeTest {
    @Test
    fun `user land creation of type`() {
        runAll(*FloatType.kinds.toTypedArray()) { it, _ ->
            val type = FloatType(it)

            assertEquals(it, type.getTypeKind())
        }
    }

    @Test
    fun `ref creation of type`() {
        val ref = FloatType(TypeKind.Float)
        val second = FloatType(ref.ref)

        assertEquals(TypeKind.Float, second.getTypeKind())
    }

    @Test
    fun `ref creation with wrong type`() {
        val ref = IntType(32)

        assertFailsWith<IllegalArgumentException> {
            FloatType(ref.ref)
        }
    }
}
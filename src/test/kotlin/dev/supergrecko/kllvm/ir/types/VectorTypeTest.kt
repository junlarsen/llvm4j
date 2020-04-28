package dev.supergrecko.kllvm.ir.types

import dev.supergrecko.kllvm.ir.TypeKind
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import org.junit.jupiter.api.Test

class VectorTypeTest {
    @Test
    fun `user land creation of type`() {
        val type = IntType(32)
        val vec = type.toVectorType(1000)

        assertEquals(TypeKind.Vector, vec.getTypeKind())
        assertEquals(1000, vec.getElementCount())
    }

    @Test
    fun `ref creation of type`() {
        val type = IntType(16).toVectorType(10)
        val second = VectorType(type.ref)

        assertEquals(TypeKind.Vector, second.getTypeKind())
    }

    @Test
    fun `underlying type matches`() {
        val type = IntType(32)
        val vec = VectorType(type, 10)

        assertEquals(10, vec.getElementCount())
        assertEquals(type.ref, vec.getElementType().ref)
    }

    @Test
    fun `subtypes match`() {
        val type = IntType(32)
        val vec = VectorType(type, 10)

        assertEquals(10, vec.getSubtypes().size)
        assertEquals(type.ref, vec.getSubtypes().first().ref)
    }

    @Test
    fun `negative size is illegal`() {
        val type = FloatType(TypeKind.Float)

        assertFailsWith<IllegalArgumentException> {
            type.toVectorType(-100)
        }

        assertFailsWith<IllegalArgumentException> {
            VectorType(type, -100)
        }
    }
}

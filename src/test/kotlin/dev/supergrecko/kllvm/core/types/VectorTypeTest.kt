package dev.supergrecko.kllvm.core.types

import dev.supergrecko.kllvm.core.enumerations.TypeKind
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class VectorTypeTest {
    @Test
    fun `underlying type matches`() {
        val type = IntType.new(32)
        val vec = VectorType.new(type, 10)

        assertEquals(10, vec.getElementCount())
        assertEquals(type.ref, vec.getElementType().ref)
    }

    @Test
    fun `subtypes match`() {
        val type = IntType.new(32)
        val vec = VectorType.new(type, 10)

        assertEquals(10, vec.getSubtypes().size)
        assertEquals(type.ref, vec.getSubtypes().first().ref)
    }

    @Test
    fun `negative size is illegal`() {
        val type = FloatType.new(TypeKind.Float)

        assertFailsWith<IllegalArgumentException> {
            type.toVectorType(-100)
        }

        assertFailsWith<IllegalArgumentException> {
            VectorType.new(type, -100)
        }
    }
}
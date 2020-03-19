package dev.supergrecko.kllvm.core.types

import dev.supergrecko.kllvm.core.enumerations.LLVMTypeKind
import dev.supergrecko.kllvm.factories.TypeFactory
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class LLVMVectorTypeTest {
    @Test
    fun `underlying type matches`() {
        val type = TypeFactory.integer(32)
        val vec = TypeFactory.vector(type, 10)

        assertEquals(10, vec.getElementSize())
        assertEquals(type.llvmType, vec.getElementType().llvmType)
    }

    @Test
    fun `negative size is illegal`() {
        val type = TypeFactory.float(LLVMTypeKind.Float)

        assertFailsWith<IllegalArgumentException> {
            type.toVector(-100)
        }

        assertFailsWith<IllegalArgumentException> {
            TypeFactory.vector(-100) {
                this.type = type
            }
        }
    }
}
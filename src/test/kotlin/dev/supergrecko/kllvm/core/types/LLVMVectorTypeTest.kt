package dev.supergrecko.kllvm.core.types

import dev.supergrecko.kllvm.core.LLVMType
import dev.supergrecko.kllvm.core.enumerations.LLVMTypeKind
import org.junit.jupiter.api.Test
import java.lang.IllegalArgumentException
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class LLVMVectorTypeTest {
    @Test
    fun `underlying type matches`() {
        val type = LLVMType.createInteger(32)
        val vec = LLVMType.createVector(type, 10)

        assertEquals(10, vec.getElementSize())
        assertEquals(type.llvmType, vec.getElementType().llvmType)
    }

    @Test
    fun `negative size is illegal`() {
        val type = LLVMType.create(LLVMTypeKind.Float)

        assertFailsWith<IllegalArgumentException> {
            type.toVector(-100)
        }
    }
}
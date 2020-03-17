package dev.supergrecko.kllvm.core.types

import dev.supergrecko.kllvm.core.LLVMType
import dev.supergrecko.kllvm.core.enumerations.LLVMTypeKind
import org.junit.jupiter.api.Test
import java.lang.IllegalArgumentException
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class LLVMArrayTypeTest {
    @Test
    fun `underlying type matches`() {
        val type = LLVMType.createInteger(32)
        val arr = LLVMType.createArray(type, 10)

        assertEquals(10, arr.getElementSize())
        assertEquals(type.llvmType, arr.getElementType().llvmType)
    }

    @Test
    fun `negative size is illegal`() {
        val type = LLVMType.create(LLVMTypeKind.Float)

        assertFailsWith<IllegalArgumentException> {
            type.toArray(-100)
        }
    }
}
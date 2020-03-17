package dev.supergrecko.kllvm.core.types

import dev.supergrecko.kllvm.core.LLVMType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class LLVMArrayTypeTest {
    @Test
    fun `underlying type matches`() {
        val type = LLVMType.createInteger(32)
        val arr = LLVMType.createArray(type, 10)

        assertEquals(10, arr.getElementSize())
        assertEquals(type.llvmType, arr.getElementType().llvmType)
    }
}
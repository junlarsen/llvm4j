package dev.supergrecko.kllvm.core.types

import dev.supergrecko.kllvm.core.LLVMType
import dev.supergrecko.kllvm.core.enumerations.LLVMTypeKind
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class LLVMArrayTypeTest {
    @Test
    fun `underlying type matches`() {
        val type = LLVMType.makeInteger(32)
        val arr = LLVMType.makeArray(type, 10)

        assertEquals(10, arr.getLength())
        assertEquals(type.llvmType, arr.getElementType().llvmType)
    }
}
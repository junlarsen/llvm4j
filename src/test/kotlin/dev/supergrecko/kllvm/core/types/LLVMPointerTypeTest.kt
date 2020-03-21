package dev.supergrecko.kllvm.core.types

import dev.supergrecko.kllvm.factories.TypeFactory
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class LLVMPointerTypeTest {
    @Test
    fun `underlying type matches`() {
        val type = TypeFactory.integer(32)
        val ptr = type.toPointerType()

        assertEquals(type.llvmType, ptr.getSequentialElementType().llvmType)
    }

    @Test
    fun `address space matches`() {
        val type = TypeFactory.integer(32)
        val ptr = type.toPointerType(100)

        assertEquals(100, ptr.getPointerAddressSpace())
    }
}
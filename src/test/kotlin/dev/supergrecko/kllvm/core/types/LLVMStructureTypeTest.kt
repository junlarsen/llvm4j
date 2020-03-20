package dev.supergrecko.kllvm.core.types

import dev.supergrecko.kllvm.factories.TypeFactory
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class LLVMStructureTypeTest {
    @Test
    fun `test element spec matches`() {
        val elements = listOf(TypeFactory.integer(32))
        val struct = TypeFactory.struct(elements, false)

        assertEquals(false, struct.isStructPacked())
        assertEquals(1, struct.getSequentialElementSize())
        assertEquals(true, struct.isStructLiteral())
        assertEquals(false, struct.isStructOpaque())

        val (first) = struct.getStructElementTypes()
        assertEquals(elements.first().llvmType, first.llvmType)

        val type = struct.getStructElementTypeAt(0)
        assertEquals(type.llvmType, elements.first().llvmType)
    }

    @Test
    fun `test opaque struct`() {
        val struct = TypeFactory.opaque("test_struct")

        assertEquals(true, struct.isStructOpaque())

        val elements = listOf(TypeFactory.integer(32))
        struct.setStructBody(elements, false)

        val (first) = struct.getStructElementTypes()
        assertEquals(elements.first().llvmType, first.llvmType)
        assertEquals(false, struct.isStructOpaque())
    }
}

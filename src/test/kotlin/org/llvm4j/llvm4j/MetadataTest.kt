package org.llvm4j.llvm4j

import org.junit.jupiter.api.Test
import org.llvm4j.llvm4j.testing.assertIsSome
import kotlin.test.assertEquals

class MetadataTest

class NamedMetadataNodeTest {
    @Test fun `Test NamedMetadataNode properties`() {
        val ctx = Context()
        val mod = ctx.createModule("test_module")
        val subject = mod.getOrCreateNamedMetadata("meta")

        assertIsSome(mod.getNamedMetadata("meta"))
        assertEquals(0, subject.getOperandCount())
        assertEquals("meta", subject.getName())
        assertEquals(0, subject.getOperands(ctx).size)
    }

    @Test fun `Test adding operands to the list`() {
        val ctx = Context()
        val mod = ctx.createModule("test_module")
        val subject = mod.getOrCreateNamedMetadata("meta")
        val md1 = ctx.getMetadataString("lol")
        val md2 = ctx.getMetadataNode(md1).toValue(ctx)

        assertEquals(0, subject.getOperandCount())

        subject.addOperand(md2)

        assertEquals(1, subject.getOperandCount())
    }
}

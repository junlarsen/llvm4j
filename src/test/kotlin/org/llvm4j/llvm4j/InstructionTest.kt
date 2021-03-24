package org.llvm4j.llvm4j

import org.junit.jupiter.api.Test
import org.llvm4j.optional.None
import org.llvm4j.optional.Some
import org.llvm4j.optional.testing.assertNone
import kotlin.test.assertEquals

class InstructionTest {
    @Test fun `Test cloned instruction properties`() {
        val ctx = Context()
        val i32 = ctx.getInt32Type()
        val mod = ctx.newModule("test")
        val bb = ctx.newBasicBlock("bb")
        val func = mod.addFunction("test", ctx.getFunctionType(i32, i32, i32))
        val lhs = func.getParameter(0).unwrap()
        val rhs = func.getParameter(1).unwrap()
        val builder = ctx.newIRBuilder()

        builder.positionAfter(bb)
        val inst = cast<BinaryOperatorInstruction>(builder.buildIntAdd(lhs, rhs, WrapSemantics.Unspecified, None))
        inst.setName("hello")
        assertEquals("hello", inst.getName())
        val clone = inst.clone()

        assertEquals(bb.ref, inst.getBasicBlock().unwrap().ref)
        assertNone(clone.getBasicBlock())
        assertEquals("", clone.getName())

        clone.insert(builder, Some("name"))
        assertEquals(bb.ref, clone.getBasicBlock().unwrap().ref)
        assertEquals("name", clone.getName())
    }
}

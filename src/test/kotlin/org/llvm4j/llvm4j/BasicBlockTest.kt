package org.llvm4j.llvm4j

import org.junit.jupiter.api.Test
import org.llvm4j.llvm4j.testing.assertIsNone
import kotlin.test.assertEquals

class BasicBlockTest {
    @Test fun `Test BasicBlock properties`() {
        val ctx = Context()
        val bb = ctx.createBasicBlock("bb")
        val subject1 = bb.asValue()
        val subject2 = subject1.getBlock()
        val subject3 = subject1.asBasicBlock()

        assertIsNone(bb.getFunction())
        assertEquals("bb", bb.getName())
        assertEquals(ValueKind.BasicBlock, subject1.getValueKind())
        assertEquals("bb", subject2.getName())
        assertEquals("bb", subject3.getName())
    }

    @Test fun `Test basic blocks are erased properly`() {
        val ctx = Context()
        val i8 = ctx.getInt8Type()
        val fnTy = ctx.getFunctionType(i8)
        val mod = ctx.createModule("test_module")
        val fn = mod.addFunction("test_fn", fnTy)
        val bb = ctx.createBasicBlock("bb")

        fn.addBasicBlock(bb)
        assertEquals(1, fn.getBasicBlockCount())

        bb.erase()
        assertEquals(0, fn.getBasicBlockCount())

        fn.addBasicBlock(bb)
        assertEquals(1, fn.getBasicBlockCount())

        bb.delete()
        assertEquals(0, fn.getBasicBlockCount())
    }

    @Test fun `Test re-arranging basic blocks`() {
        val ctx = Context()
        val i8 = ctx.getInt8Type()
        val fnTy = ctx.getFunctionType(i8)
        val mod = ctx.createModule("test_module")
        val fn = mod.addFunction("test_fn", fnTy)
        val bb1 = ctx.createBasicBlock("bb1")
        val bb2 = ctx.createBasicBlock("bb2")

        fn.addBasicBlock(bb1)
        fn.addBasicBlock(bb2)

        assertEquals(bb1.ref, fn.getEntryBasicBlock().ref)

        bb1.moveAfter(bb2)
        assertEquals(bb2.ref, fn.getEntryBasicBlock().ref)

        bb1.moveBefore(bb2)
        assertEquals(bb1.ref, fn.getEntryBasicBlock().ref)
    }
}

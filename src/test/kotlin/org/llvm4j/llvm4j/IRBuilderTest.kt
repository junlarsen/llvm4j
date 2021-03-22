package org.llvm4j.llvm4j

import org.junit.jupiter.api.Test
import org.llvm4j.optional.None
import org.llvm4j.optional.Some
import org.llvm4j.optional.testing.assertNone
import org.llvm4j.optional.testing.assertSome
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class IRBuilderTest {
    @Test fun `Test ir builder properties`() {
        val ctx = Context()
        val builder = ctx.newIRBuilder()
        val bb1 = ctx.newBasicBlock("bb1")

        assertNone(builder.getInsertionBlock())
        builder.positionAfter(bb1)
        assertSome(builder.getInsertionBlock())
        assertEquals(bb1.ref, builder.getInsertionBlock().unwrap().ref)
        builder.clear()
        assertNone(builder.getInsertionBlock())

        assertNone(builder.getDefaultFPMathTag())
        assertNone(builder.getDebugLocation())
    }

    @Test fun `Test return instructions`() {
        val ctx = Context()
        val i32 = ctx.getInt32Type()
        val i32V = i32.getConstant(1)
        val builder = ctx.newIRBuilder()
        val bb1 = ctx.newBasicBlock("bb1")
        val bb2 = ctx.newBasicBlock("bb2")

        builder.positionAfter(bb1)
        val ret1 = builder.buildReturn(None)
        assertEquals("  ret void", ret1.getAsString())
        assertEquals(0, ret1.getSuccessorCount())

        builder.positionAfter(bb2)
        val ret2 = builder.buildReturn(Some(i32V))
        assertEquals("  ret i32 1", ret2.getAsString())
    }

    @Test fun `Test branch instructions`() {
        val ctx = Context()
        val i1 = ctx.getInt1Type()
        val builder = ctx.newIRBuilder()
        val bb1 = ctx.newBasicBlock("bb1")
        val bb2 = ctx.newBasicBlock("bb2")
        val bb3 = ctx.newBasicBlock("bb3")

        builder.positionAfter(bb1)
        val br1 = builder.buildBranch(bb2)
        builder.positionAfter(bb2)
        builder.buildReturn(None)

        assertEquals(1, br1.getSuccessorCount())
        assertEquals(bb2.ref, br1.getSuccessor(0).unwrap().ref)
        assertFalse { br1.isConditional() }

        val cond = i1.getConstant(0)
        val replace = i1.getConstant(1)
        builder.positionAfter(bb3)
        val br2 = builder.buildConditionalBranch(cond, bb1, bb2)

        assertEquals(2, br2.getSuccessorCount())
        assertTrue { br2.isConditional() }
        assertEquals(cond.ref, br2.getCondition().unwrap().ref)

        br2.setCondition(replace)
        assertEquals(replace.ref, br2.getCondition().unwrap().ref)
    }
}

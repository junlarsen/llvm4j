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

    @Test fun `Test ret instructions`() {
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

    @Test fun `Test br instructions`() {
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

    @Test fun `Test switch instructions`() {
        val ctx = Context()
        val i32 = ctx.getInt32Type()
        val builder = ctx.newIRBuilder()
        val entry = ctx.newBasicBlock("entry")
        val default = ctx.newBasicBlock("default")
        val bb1 = ctx.newBasicBlock("bb1")
        val cond = i32.getConstant(14)

        builder.positionAfter(entry)
        val switch = builder.buildSwitch(cond, default, 1)

        assertEquals(1, switch.getSuccessorCount())
        assertEquals(default.ref, switch.getDefaultDestination().ref)

        switch.addCase(i32.getConstant(1), bb1)
        assertEquals(2, switch.getSuccessorCount())
    }

    @Test fun `Test indirectbr instructions`() {
        val ctx = Context()
        val void = ctx.getVoidType()
        val mod = ctx.newModule("test")
        val func = mod.addFunction("test", ctx.getFunctionType(void))
        val bb1 = ctx.newBasicBlock("bb1")
        val bb2 = ctx.newBasicBlock("bb2")
        val bb3 = ctx.newBasicBlock("bb3")
        val builder = ctx.newIRBuilder()

        func.addBasicBlock(bb1)
        func.addBasicBlock(bb2)
        func.addBasicBlock(bb3)
        builder.positionAfter(bb1)
        val addr = func.getBlockAddress(bb2).unwrap()
        val indirect = builder.buildIndirectBranch(addr, 2)

        assertEquals(0, indirect.getSuccessorCount())
        indirect.addCase(bb2)
        indirect.addCase(bb3)
        assertEquals(2, indirect.getSuccessorCount())
    }

    @Test fun `Test unreachable instructions`() {
        val ctx = Context()
        val bb1 = ctx.newBasicBlock("bb1")
        val builder = ctx.newIRBuilder()

        builder.positionAfter(bb1)
        val unreachable = builder.buildUnreachable()

        assertEquals(0, unreachable.getSuccessorCount())
    }

    @Test fun `Test fneg instructions`() {
        val ctx = Context()
        val mod = ctx.newModule("test")
        val float = ctx.getFloatType()
        val builder = ctx.newIRBuilder()

        val function = mod.addFunction("test", ctx.getFunctionType(float, float))
        val block = ctx.newBasicBlock("entry")

        builder.positionAfter(block)
        val lhs = function.getParameter(0).unwrap()
        val res = builder.buildFloatNeg(lhs, None)
        builder.buildReturn(Some(res))
        function.addBasicBlock(block)

        assertTrue { isa<UnaryOperatorInstruction>(res) }
    }

    @Test fun `Test integer binary instructions`() {
        val ctx = Context()
        val mod = ctx.newModule("test")
        val i32 = ctx.getInt32Type()
        val builder = ctx.newIRBuilder()
        val fn = ctx.getFunctionType(i32, i32, i32)

        // for each binary op we are generating
        // define i32 name (i32 %0, i32 %1) {
        //   %2 = op i32 %0, %1
        //   ret %2
        // }
        for ((index, semantic) in WrapSemantics.values().withIndex()) {
            val addFunction = mod.addFunction("test_add_$index", fn)
            val addBlock = ctx.newBasicBlock("entry")
            builder.positionAfter(addBlock)
            val addLhs = addFunction.getParameter(0).unwrap()
            val addRhs = addFunction.getParameter(1).unwrap()
            val addRes = builder.buildIntAdd(addLhs, addRhs, semantic, None)
            builder.buildReturn(Some(addRes))
            addFunction.addBasicBlock(addBlock)
            assertTrue { isa<BinaryOperatorInstruction>(addRes) }

            val subFunction = mod.addFunction("test_sub_$index", fn)
            val subBlock = ctx.newBasicBlock("entry")
            builder.positionAfter(subBlock)
            val subLhs = subFunction.getParameter(0).unwrap()
            val subRhs = subFunction.getParameter(1).unwrap()
            val subRes = builder.buildIntSub(subLhs, subRhs, semantic, None)
            builder.buildReturn(Some(subRes))
            subFunction.addBasicBlock(subBlock)
            assertTrue { isa<BinaryOperatorInstruction>(subRes) }

            val mulFunction = mod.addFunction("test_mul_$index", fn)
            val mulBlock = ctx.newBasicBlock("entry")
            builder.positionAfter(mulBlock)
            val mulLhs = mulFunction.getParameter(0).unwrap()
            val mulRhs = mulFunction.getParameter(1).unwrap()
            val mulRes = builder.buildIntMul(mulLhs, mulRhs, semantic, None)
            builder.buildReturn(Some(mulRes))
            mulFunction.addBasicBlock(mulBlock)
            assertTrue { isa<BinaryOperatorInstruction>(mulRes) }
        }

        for ((index, exact) in listOf(true, false).withIndex()) {
            val sdivFunction = mod.addFunction("test_sdiv_$index", fn)
            val sdivBlock = ctx.newBasicBlock("entry")
            builder.positionAfter(sdivBlock)
            val sdivLhs = sdivFunction.getParameter(0).unwrap()
            val sdivRhs = sdivFunction.getParameter(1).unwrap()
            val sdivRes = builder.buildSignedDiv(sdivLhs, sdivRhs, exact, None)
            builder.buildReturn(Some(sdivRes))
            sdivFunction.addBasicBlock(sdivBlock)
            assertTrue { isa<BinaryOperatorInstruction>(sdivRes) }

            val udivFunction = mod.addFunction("test_udiv_$index", fn)
            val udivBlock = ctx.newBasicBlock("entry")
            builder.positionAfter(udivBlock)
            val udivLhs = udivFunction.getParameter(0).unwrap()
            val udivRhs = udivFunction.getParameter(1).unwrap()
            val udivRes = builder.buildSignedDiv(udivLhs, udivRhs, exact, None)
            builder.buildReturn(Some(udivRes))
            udivFunction.addBasicBlock(udivBlock)
            assertTrue { isa<BinaryOperatorInstruction>(udivRes) }
        }

        val sremFunction = mod.addFunction("test_srem", fn)
        val sremBlock = ctx.newBasicBlock("entry")
        builder.positionAfter(sremBlock)
        val sremLhs = sremFunction.getParameter(0).unwrap()
        val sremRhs = sremFunction.getParameter(1).unwrap()
        val sremRes = builder.buildSignedRem(sremLhs, sremRhs, None)
        builder.buildReturn(Some(sremRes))
        sremFunction.addBasicBlock(sremBlock)
        assertTrue { isa<BinaryOperatorInstruction>(sremRes) }

        val uremFunction = mod.addFunction("test_urem", fn)
        val uremBlock = ctx.newBasicBlock("entry")
        builder.positionAfter(uremBlock)
        val uremLhs = uremFunction.getParameter(0).unwrap()
        val uremRhs = uremFunction.getParameter(1).unwrap()
        val uremRes = builder.buildUnsignedRem(uremLhs, uremRhs, None)
        builder.buildReturn(Some(uremRes))
        uremFunction.addBasicBlock(uremBlock)
        assertTrue { isa<BinaryOperatorInstruction>(uremRes) }

        val shlFunction = mod.addFunction("test_shl", fn)
        val shlBlock = ctx.newBasicBlock("entry")
        builder.positionAfter(shlBlock)
        val shlLhs = shlFunction.getParameter(0).unwrap()
        val shlRhs = shlFunction.getParameter(1).unwrap()
        val shlRes = builder.buildLeftShift(shlLhs, shlRhs, None)
        builder.buildReturn(Some(shlRes))
        shlFunction.addBasicBlock(shlBlock)
        assertTrue { isa<BinaryOperatorInstruction>(shlRes) }

        val lshrFunction = mod.addFunction("test_lshr", fn)
        val lshrBlock = ctx.newBasicBlock("entry")
        builder.positionAfter(lshrBlock)
        val lshrLhs = lshrFunction.getParameter(0).unwrap()
        val lshrRhs = lshrFunction.getParameter(1).unwrap()
        val lshrRes = builder.buildLogicalShiftRight(lshrLhs, lshrRhs, None)
        builder.buildReturn(Some(lshrRes))
        lshrFunction.addBasicBlock(lshrBlock)
        assertTrue { isa<BinaryOperatorInstruction>(lshrRes) }

        val ashrFunction = mod.addFunction("test_ashr", fn)
        val ashrBlock = ctx.newBasicBlock("entry")
        builder.positionAfter(ashrBlock)
        val ashrLhs = ashrFunction.getParameter(0).unwrap()
        val ashrRhs = ashrFunction.getParameter(1).unwrap()
        val ashrRes = builder.buildArithmeticShiftRight(ashrLhs, ashrRhs, None)
        builder.buildReturn(Some(ashrRes))
        ashrFunction.addBasicBlock(ashrBlock)
        assertTrue { isa<BinaryOperatorInstruction>(ashrRes) }

        val andFunction = mod.addFunction("test_and", fn)
        val andBlock = ctx.newBasicBlock("entry")
        builder.positionAfter(andBlock)
        val andLhs = andFunction.getParameter(0).unwrap()
        val andRhs = andFunction.getParameter(1).unwrap()
        val andRes = builder.buildLogicalAnd(andLhs, andRhs, None)
        builder.buildReturn(Some(andRes))
        andFunction.addBasicBlock(andBlock)
        assertTrue { isa<BinaryOperatorInstruction>(andRes) }

        val orFunction = mod.addFunction("test_or", fn)
        val orBlock = ctx.newBasicBlock("entry")
        builder.positionAfter(orBlock)
        val orLhs = orFunction.getParameter(0).unwrap()
        val orRhs = orFunction.getParameter(1).unwrap()
        val orRes = builder.buildLogicalOr(orLhs, orRhs, None)
        builder.buildReturn(Some(orRes))
        orFunction.addBasicBlock(orBlock)
        assertTrue { isa<BinaryOperatorInstruction>(orRes) }

        val xorFunction = mod.addFunction("test_xor", fn)
        val xorBlock = ctx.newBasicBlock("entry")
        builder.positionAfter(xorBlock)
        val xorLhs = xorFunction.getParameter(0).unwrap()
        val xorRhs = xorFunction.getParameter(1).unwrap()
        val xorRes = builder.buildLogicalXor(xorLhs, xorRhs, None)
        builder.buildReturn(Some(xorRes))
        xorFunction.addBasicBlock(xorBlock)
        assertTrue { isa<BinaryOperatorInstruction>(xorRes) }
    }
}

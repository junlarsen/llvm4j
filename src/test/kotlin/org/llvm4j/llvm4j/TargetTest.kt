package org.llvm4j.llvm4j

import org.junit.jupiter.api.Test
import org.llvm4j.optional.None
import org.llvm4j.optional.Some
import org.llvm4j.optional.testing.assertOk
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TargetTest {
    @Test fun `Test retrieving x86_64 target values`() {
        LLVMSystem.initializeAllTargets()
        val target = Target.fromName("x86-64").unwrap()
        assertTrue { target.hasJIT() }
        assertTrue { target.hasTargetMachine() }
        assertTrue { target.hasAsmBackend() }
        assertTrue { target.getDescription().contains("X86") }
        val alternative = Target.fromTriple("x86_64-unknown-linux-gnu")
        assertOk(alternative)
    }

    @Test fun `Test x86_64 targetmachine properties`() {
        LLVMSystem.initializeAllTargets()
        val target = Target.fromName("x86-64").unwrap()
        val machine = TargetMachine.of(
            target, "x86_64-unknown-linux-gnu", "generic", "+sse",
            CodeGenOptimizationLevel.None, CodeModel.Default, RelocMode.Default
        )

        assertEquals(target.ref, machine.getTarget().ref)
        assertEquals("x86_64-unknown-linux-gnu", machine.getTargetTriple())
        assertEquals("generic", machine.getProcessorName())
        assertEquals("+sse", machine.getProcessorFeatures())
        val data = machine.getTargetLayout()
        val ctx = Context()
        val i32 = ctx.getInt32Type()
        val struct = ctx.getStructType(i32, i32)

        assertEquals(8, data.getPointerSize(None))
        assertEquals(8, data.getPointerSize(Some(AddressSpace.from(1))))
        assertEquals(ByteOrdering.LittleEndian, data.getByteOrder())
        assertEquals(32, data.getBitSize(i32))
        assertEquals(4, data.getStorageSize(i32))
        assertEquals(4, data.getABISize(i32))
        assertEquals(4, data.getCallFrameAlignment(i32))
        assertEquals(4, data.getPreferredAlignment(i32))

        val element = data.getElementAtOffset(struct, 4)
        assertEquals(4, data.getOffsetOfElement(struct, element))

        val layoutString = data.getTargetLayout()
        val other = TargetData.of(layoutString)
        assertEquals(data.getTargetLayout(), other.getTargetLayout())
    }
}
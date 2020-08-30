package io.vexelabs.bitbuilder.llvm.unit.ir

import io.vexelabs.bitbuilder.llvm.ir.MetadataString
import io.vexelabs.bitbuilder.llvm.ir.Module
import io.vexelabs.bitbuilder.llvm.setup
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.spekframework.spek2.Spek

internal object NamedMetadataTest : Spek({
    setup()

    val module: Module by memoized()

    test("the name of a node matches") {
        val metadata = module.getOrCreateNamedMetadata("test")

        assertEquals("test", metadata.name)
    }

    group("fetching and creating operands for a node") {
        test("a fresh node does not have any operands") {
            val metadata = module.getOrCreateNamedMetadata("test")

            assertEquals(0, metadata.getOperandCount())
            assertTrue { metadata.getOperands().isEmpty() }
        }

        test("adding an operand to a node") {
            val metadata = MetadataString("never")
            val node = module.getOrCreateNamedMetadata("test").also {
                it.addOperand(metadata)
            }
            val operands = node.getOperands()

            assertEquals(1, node.getOperandCount())
            assertEquals(1, operands.size)
        }
    }
})

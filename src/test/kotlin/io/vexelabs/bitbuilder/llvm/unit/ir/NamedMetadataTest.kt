package io.vexelabs.bitbuilder.llvm.unit.ir

import io.vexelabs.bitbuilder.llvm.ir.Context
import io.vexelabs.bitbuilder.llvm.ir.MetadataString
import io.vexelabs.bitbuilder.llvm.ir.Module
import io.vexelabs.bitbuilder.llvm.setup
import org.spekframework.spek2.Spek
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal object NamedMetadataTest : Spek({
    setup()

    val module: Module by memoized()
    val context: Context by memoized()

    test("the name of a node matches") {
        val metadata = module.getOrCreateNamedMetadata("test")

        assertEquals("test", metadata.name)
    }

    group("fetching and creating operands for a node") {
        test("a fresh node does not have any operands") {
            val metadata = module.getOrCreateNamedMetadata("test")

            assertEquals(0, metadata.getOperandCount(module))
            assertTrue { metadata.getOperands(module).isEmpty() }
        }

        test("adding an operand to a node") {
            val metadata = context.createMetadataString("never")
            val node = module.getOrCreateNamedMetadata("test").also {
                it.addOperand(metadata, module)
            }
            val operands = node.getOperands(module)

            assertEquals(1, node.getOperandCount(module))
            assertEquals(1, operands.size)
        }
    }
})

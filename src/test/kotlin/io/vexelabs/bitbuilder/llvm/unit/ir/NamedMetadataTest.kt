package io.vexelabs.bitbuilder.llvm.unit.ir

import io.vexelabs.bitbuilder.llvm.ir.Metadata
import io.vexelabs.bitbuilder.llvm.ir.Module
import io.vexelabs.bitbuilder.llvm.setup
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.spekframework.spek2.Spek

internal object NamedMetadataTest : Spek({
    setup()

    val module: Module by memoized()

    group("getting metadata from a module") {
        test("a module without metadata does not have a first or last") {
            val iter = module.getNamedMetadataIterator()

            assertNull(iter)
        }

        test("a module with metadata can use the iterator") {
            module.getOrCreateNamedMetadata("one")
            module.getOrCreateNamedMetadata("two")

            val iter = module.getNamedMetadataIterator()

            assertNotNull(iter)
            assertTrue { iter.hasNext() }

            val first = iter.next()

            assertTrue { iter.hasNext() }

            val second = iter.next()

            assertEquals("one", first.getName())
            assertEquals("two", second.getName())
        }

        test("finding a metadata node by name") {
            module.getOrCreateNamedMetadata("metadata")

            val subject = module.getNamedMetadata("metadata")

            assertNotNull(subject)
            assertEquals("metadata", subject.getName())
        }
    }

    group("assigning values to the metadata nodes") {
        test("a node without any values has a size of 0") {
            module.getOrCreateNamedMetadata("metadata")

            assertEquals(0, module.getNamedMetadataOperandCount("metadata"))
        }

        test("retrieving the operands from the metadata") {
            val operand = Metadata("meta").asValue()

            module.getOrCreateNamedMetadata("metadata")
            module.addNamedMetadataOperand("metadata", operand)

            assertEquals(1, module.getNamedMetadataOperandCount("metadata"))

            val subject = module.getNamedMetadataOperands("metadata")

            assertNotNull(subject)
        }
    }
})

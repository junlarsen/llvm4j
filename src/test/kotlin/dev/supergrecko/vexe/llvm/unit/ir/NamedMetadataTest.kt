package dev.supergrecko.vexe.llvm.unit.ir

import dev.supergrecko.vexe.llvm.ir.Metadata
import dev.supergrecko.vexe.llvm.ir.Module
import dev.supergrecko.vexe.llvm.ir.types.IntType
import dev.supergrecko.vexe.llvm.ir.values.constants.ConstantInt
import dev.supergrecko.vexe.llvm.setup
import org.spekframework.spek2.Spek
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

internal object NamedMetadataTest : Spek({
    setup()

    val module: Module by memoized()

    group("getting metadata from a module") {
        test("a module without metadata does not have a first or last") {
            val first = module.getFirstNamedMetadata()
            val last = module.getLastNamedMetadata()

            assertNull(first)
            assertNull(last)
        }

        test("a module with metadata can use the iterator") {
            module.getOrCreateNamedMetadata("one")
            module.getOrCreateNamedMetadata("two")

            val first = module.getFirstNamedMetadata()
            val second = first?.getNextNamedMetadata()

            assertEquals("one", first?.getName())
            assertEquals("two", second?.getName())
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
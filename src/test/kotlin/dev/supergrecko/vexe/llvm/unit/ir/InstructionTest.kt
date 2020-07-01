package dev.supergrecko.vexe.llvm.unit.ir

import dev.supergrecko.vexe.llvm.ir.Builder
import dev.supergrecko.vexe.llvm.ir.Context
import dev.supergrecko.vexe.llvm.ir.Module
import dev.supergrecko.vexe.llvm.ir.Metadata
import dev.supergrecko.vexe.llvm.ir.types.FunctionType
import dev.supergrecko.vexe.llvm.ir.types.VoidType
import dev.supergrecko.vexe.llvm.setup
import org.spekframework.spek2.Spek
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal object InstructionTest : Spek({
    setup()

    val builder: Builder by memoized()
    val context: Context by memoized()
    val module: Module by memoized()

    group("instruction metadata") {
        test("fresh instruction does not have metadata") {
            val inst = builder.build().createRetVoid()

            assertFalse { inst.hasMetadata() }
        }

        test("retrieving metadata from an instruction") {
            val inst = builder.build().createRetVoid().apply {
                setMetadata(
                    "range",
                    Metadata("yes").asValue(context)
                )
            }
            val subject = inst.getMetadata("range")

            assertTrue { inst.hasMetadata() }
            assertNotNull(subject)
        }

        test("metadata bucket contains our metadata") {
            val inst = builder.build().createRetVoid().apply {
                setMetadata(
                    "range",
                    Metadata("yes").asValue(context)
                )
            }
            val bucket = inst.getAllMetadataExceptDebugLocations()
            val subject = bucket.getKind(0)
            val id = context.getMetadataKindId("range")

            assertEquals(1, bucket.size())
            assertEquals(subject, id)
        }
    }

    group("residing basic blocks") {
        test("loose instruction has no parent") {
            val inst = builder.build().createRetVoid()

            assertNull(inst.getInstructionBlock())
        }

        test("retrieving the residing block") {
            val function = module.addFunction("test", FunctionType(
                VoidType(), listOf(), false
            )
            )
            val block = function.createBlock("entry")

            builder.setPositionAtEnd(block)
            val inst = builder.build().createRetVoid()
            val subject = inst.getInstructionBlock()

            assertNotNull(subject)
            assertEquals(block.ref, subject.ref)
        }

        test("iterating over instructions in a block") {
            val function = module.addFunction("testfn", FunctionType(
                VoidType(), listOf(), false
            ))
            val block = function.createBlock("entry")

            builder.setPositionAtEnd(block)
            builder.build().createBr(block)
            builder.build().createRetVoid()

            val first = block.getFirstInstruction()
            val second = first?.getNextInstruction()
            val subject = second?.getPreviousInstruction()

            assertNotNull(first)
            assertNotNull(second)
            assertNotNull(subject)
            assertEquals(first.ref, subject.ref)
        }
    }
})

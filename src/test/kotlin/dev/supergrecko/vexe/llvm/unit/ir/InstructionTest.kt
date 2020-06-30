package dev.supergrecko.vexe.llvm.unit.ir

import dev.supergrecko.vexe.llvm.ir.Builder
import dev.supergrecko.vexe.llvm.ir.Context
import dev.supergrecko.vexe.llvm.ir.Metadata
import dev.supergrecko.vexe.llvm.ir.Module
import dev.supergrecko.vexe.llvm.ir.types.FunctionType
import dev.supergrecko.vexe.llvm.ir.types.IntType
import dev.supergrecko.vexe.llvm.ir.types.VoidType
import dev.supergrecko.vexe.llvm.ir.values.constants.ConstantInt
import dev.supergrecko.vexe.llvm.setup
import org.junit.jupiter.api.assertThrows
import org.spekframework.spek2.Spek
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class InstructionTest : Spek({
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
            ))
            val block = function.createBlock("entry")

            builder.setPositionAtEnd(block)
            val inst = builder.build().createRetVoid()
            val subject = inst.getInstructionBlock()

            assertNotNull(subject)
            assertEquals(block.ref, subject.ref)
        }
    }

    group("successors of terminating basic blocks") {
        test("a loose terminator does not have a successor") {
            val inst = builder.build().createRetVoid()

            assertThrows<IllegalArgumentException> {
                inst.getSuccessor(0)
            }
        }

        test("non terminators do not have a successor") {
            val inst = builder.build().createAnd(
                ConstantInt(IntType(32), 1),
                ConstantInt(IntType(32), 0),
                "and"
            )

            assertThrows<IllegalArgumentException> {
                assertEquals(0, inst.getSuccessorCount())
            }
        }

        test("non terminators may not have a successor assigned") {
            val function = module.addFunction("test", FunctionType(
                VoidType(), listOf(), false
            ))
            val block = function.createBlock("entry")
            val inst = builder.build().createAnd(
                ConstantInt(IntType(32), 1),
                ConstantInt(IntType(32), 0),
                "and"
            )

            assertThrows<IllegalArgumentException> {
                inst.setSuccessor(0, block)
            }
        }
    }
})

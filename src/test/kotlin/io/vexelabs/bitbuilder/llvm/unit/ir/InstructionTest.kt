package io.vexelabs.bitbuilder.llvm.unit.ir

import io.vexelabs.bitbuilder.llvm.ir.Builder
import io.vexelabs.bitbuilder.llvm.ir.Context
import io.vexelabs.bitbuilder.llvm.ir.MetadataString
import io.vexelabs.bitbuilder.llvm.ir.Module
import io.vexelabs.bitbuilder.llvm.ir.Opcode
import io.vexelabs.bitbuilder.llvm.ir.types.FunctionType
import io.vexelabs.bitbuilder.llvm.ir.types.VoidType
import io.vexelabs.bitbuilder.llvm.setup
import org.spekframework.spek2.Spek
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
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
            val inst = builder.createRetVoid()

            assertFalse { inst.hasMetadata() }
        }

        test("retrieving metadata from an instruction") {
            val inst = builder.createRetVoid().apply {
                setMetadata(
                    "range",
                    MetadataString("yes").toValue(context)
                )
            }
            val subject = inst.getMetadata("range")

            assertTrue { inst.hasMetadata() }
            assertNotNull(subject)
        }

        test("metadata bucket contains our metadata") {
            val inst = builder.createRetVoid().apply {
                setMetadata(
                    "range",
                    MetadataString("yes").toValue(context)
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
            val inst = builder.createRetVoid()

            assertNull(inst.getInstructionBlock())
        }

        test("retrieving the residing block") {
            val function = module.createFunction(
                "test",
                FunctionType(
                    VoidType(),
                    listOf(),
                    false
                )
            )
            val block = function.createBlock("entry")

            builder.setPositionAtEnd(block)
            val inst = builder.createRetVoid()
            val subject = inst.getInstructionBlock()

            assertNotNull(subject)
            assertEquals(block.ref, subject.ref)
        }
    }

    group("removal and deletion of nodes") {
        test("removal of loose instruction fails due to missing parent") {
            val inst = builder.createRetVoid()

            assertFailsWith<IllegalArgumentException> {
                inst.remove()
            }
        }

        test("deletion of loose instruction fails due to missing parent") {
            val inst = builder.createRetVoid()

            assertFailsWith<IllegalArgumentException> {
                inst.delete()
            }
        }
    }

    test("the opcode of an instruction matches") {
        val inst = builder.createRetVoid()

        assertEquals(Opcode.Ret, inst.getOpcode())
    }

    group("cloning an instruction") {
        test("cloning creates an instruction without a parent") {
            val function = module.createFunction(
                "test",
                FunctionType(
                    VoidType(),
                    listOf(),
                    false
                )
            )
            val block = function.createBlock("entry")

            builder.setPositionAtEnd(block)
            val inst = builder.createRetVoid()

            assertNotNull(inst.getInstructionBlock())

            val subject = inst.clone()

            assertNull(subject.getInstructionBlock())
        }
    }
})

package io.vexelabs.bitbuilder.llvm.unit.ir

import io.vexelabs.bitbuilder.llvm.ir.Context
import io.vexelabs.bitbuilder.llvm.ir.Metadata
import io.vexelabs.bitbuilder.llvm.ir.MetadataNode
import io.vexelabs.bitbuilder.llvm.ir.MetadataString
import io.vexelabs.bitbuilder.llvm.ir.types.IntType
import io.vexelabs.bitbuilder.llvm.ir.values.constants.ConstantInt
import io.vexelabs.bitbuilder.llvm.setup
import org.spekframework.spek2.Spek
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal object MetadataTest : Spek({
    setup()

    val context: Context by memoized()

    group("casting values to and from metadata") {
        test("any value may be used as metadata") {
            val i32 = context.getIntType(32)
            val value = ConstantInt(i32, 1000)
            val metadata = Metadata.fromValue(value)

            assertTrue { metadata.isNode(context) }
        }

        test("metadata may be cast to values") {
            val str = context.createMetadataString("hello world")
            val nodeChild = context.createMetadataString("hello")
            val node = context.createMetadataNode(listOf(nodeChild))

            val strValue = str.toValue(context)
            val strNode = node.toValue(context)

            assertTrue { strValue.isMetadataString() }
            assertTrue { strNode.isMetadataNode() }
        }
    }

    test("retrieving the string from a metadatastring") {
        val metadata = context.createMetadataString("hello world")

        assertEquals("hello world", metadata.getString(context))
    }

    test("a fresh node has zero operands") {
        val metadata = context.createMetadataNode(listOf())

        assertEquals(0, metadata.getOperandCount(context))
        assertTrue { metadata.getOperands(context).isEmpty() }
    }

    test("fetching operands from a metadata node") {
        val child = context.createMetadataString("hello")
        val node = context.createMetadataNode(listOf(child))
        val operands = node.getOperands(context)

        assertEquals(1, node.getOperandCount(context))
        assertEquals(1, operands.size)

        val first = operands.first()

        assertTrue { first.isMetadataString() }

        val metadata = Metadata.fromValue(first)

        assertTrue { metadata.isString(context) }

        val string = MetadataString(metadata.ref)

        assertEquals("hello", string.getString(context))
    }
})

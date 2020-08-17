package io.vexelabs.bitbuilder.llvm.unit.ir

import io.vexelabs.bitbuilder.llvm.ir.Metadata
import io.vexelabs.bitbuilder.llvm.ir.MetadataNode
import io.vexelabs.bitbuilder.llvm.ir.MetadataString
import io.vexelabs.bitbuilder.llvm.ir.types.IntType
import io.vexelabs.bitbuilder.llvm.ir.values.constants.ConstantInt
import org.spekframework.spek2.Spek
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal object MetadataTest : Spek({
    group("casting values to and from metadata") {
        test("any value may be used as metadata") {
            val value = ConstantInt(IntType(32), 1000)
            val metadata = Metadata.fromValue(value)

            assertTrue { metadata.isNode() }
        }

        test("metadata may be cast to values") {
            val str = MetadataString("hello world")
            val node = MetadataNode(listOf(MetadataString("hello")))
            val strValue = str.toValue()
            val strNode = node.toValue()

            assertTrue { strValue.isMetadataString() }
            assertTrue { strNode.isMetadataNode() }
        }
    }

    test("retrieving the string from a metadatastring") {
        val metadata = MetadataString("hello world")

        assertEquals("hello world", metadata.getString())
    }

    test("a fresh node has zero operands") {
        val metadata = MetadataNode(listOf())

        assertEquals(0, metadata.getOperandCount())
        assertTrue { metadata.getOperands().isEmpty() }
    }

    test("fetching operands from a metadata node") {
        val child = MetadataString("hello")
        val node = MetadataNode(listOf(child))
        val operands = node.getOperands()

        assertEquals(1, node.getOperandCount())
        assertEquals(1, operands.size)

        val first = operands.first()

        assertTrue { first.isMetadataString() }

        val metadata = Metadata.fromValue(first)

        assertTrue { metadata.isString() }

        val string = MetadataString(metadata.ref)

        assertEquals("hello", string.getString())
    }
})
package io.vexelabs.bitbuilder.llvm.unit.ir.types

import io.vexelabs.bitbuilder.llvm.ir.TypeKind
import io.vexelabs.bitbuilder.llvm.ir.types.FloatType
import io.vexelabs.bitbuilder.llvm.ir.types.IntType
import io.vexelabs.bitbuilder.llvm.ir.types.StructType
import org.spekframework.spek2.Spek
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class StructureTypeTest : Spek({
    test("create an empty structure type") {
        val type = StructType(listOf(), false)

        assertEquals(TypeKind.Struct, type.getTypeKind())
    }

    test("all structures are sized") {
        val arg = FloatType(TypeKind.Float)
        val type = StructType(listOf(arg), false)

        assertTrue { type.isSized() }

        // Yes, even structs without a defined body
        val struct = StructType(listOf(), false)

        assertTrue { struct.isSized() }
    }

    test("the element size matches") {
        val struct1 = StructType(listOf(), false)
        val struct2 = StructType(listOf(IntType(32)), false)

        assertEquals(0, struct1.getElementCount())
        assertEquals(1, struct2.getElementCount())
    }

    test("retrieved element types match") {
        val elements = listOf(IntType(32))
        val struct = StructType(elements, false)

        val (first) = struct.getElementTypes()
        assertEquals(elements.first().ref, first.ref)
    }

    test("structures may be flagged packed") {
        val struct = StructType(listOf(), true)

        assertTrue { struct.isPacked() }
    }

    group("literal structs") {
        test("an empty named struct is not literal") {
            val struct = StructType("test")

            assertFalse { struct.isLiteral() }
        }

        test("the structure name matches") {
            val struct = StructType("test_this")

            assertEquals("test_this", struct.getName())
        }

        test("unnamed structs are opaque") {
            val struct = StructType("test")

            assertTrue { struct.isOpaque() }
        }

        test("An opaque struct is no longer opaque after body is set") {
            val struct = StructType("test")

            assertEquals(true, struct.isOpaque())

            val elements = listOf(IntType(32))
            struct.setBody(elements, false)

            assertEquals(false, struct.isOpaque())
        }
    }
})

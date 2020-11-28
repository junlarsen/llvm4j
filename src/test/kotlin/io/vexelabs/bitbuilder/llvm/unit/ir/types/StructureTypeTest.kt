package io.vexelabs.bitbuilder.llvm.unit.ir.types

import io.vexelabs.bitbuilder.llvm.ir.Context
import io.vexelabs.bitbuilder.llvm.ir.TypeKind
import io.vexelabs.bitbuilder.llvm.ir.types.FloatType
import io.vexelabs.bitbuilder.llvm.ir.types.IntType
import io.vexelabs.bitbuilder.llvm.ir.types.StructType
import io.vexelabs.bitbuilder.llvm.setup
import org.spekframework.spek2.Spek
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class StructureTypeTest : Spek({
    setup()

    val context: Context by memoized()

    test("create an empty structure type") {
        val type = context.getStructType(packed = false)

        assertEquals(TypeKind.Struct, type.getTypeKind())
    }

    test("all structures are sized") {
        val arg = context.getFloatType(TypeKind.Float)
        val type = context.getStructType(arg, packed = false)

        assertTrue { type.isSized() }

        // Yes, even structs without a defined body
        val struct = context.getStructType(packed = false)

        assertTrue { struct.isSized() }
    }

    test("the element size matches") {
        val i32 = context.getIntType(32)
        val struct1 = context.getStructType(packed = false)
        val struct2 = context.getStructType(i32, packed = false)

        assertEquals(0, struct1.getElementCount())
        assertEquals(1, struct2.getElementCount())
    }

    test("retrieved element types match") {
        val i32 = context.getIntType(32)
        val struct = context.getStructType(i32, packed = false)

        val (first) = struct.getElementTypes()
        assertEquals(i32.ref, first.ref)
    }

    test("structures may be flagged packed") {
        val struct = context.getStructType(packed = true)

        assertTrue { struct.isPacked() }
    }

    group("literal structs") {
        test("an empty named struct is not literal") {
            val struct = context.getOpaqueStructType("Test")

            assertFalse { struct.isLiteral() }
        }

        test("the structure name matches") {
            val struct = context.getOpaqueStructType("test_this")

            assertEquals("test_this", struct.getName())
        }

        test("unnamed structs are opaque") {
            val struct = context.getOpaqueStructType("test")

            assertTrue { struct.isOpaque() }
        }

        test("An opaque struct is no longer opaque after body is set") {
            val i32 = context.getIntType(32)
            val struct = context.getOpaqueStructType("test")

            assertEquals(true, struct.isOpaque())

            val elements = listOf(i32)
            struct.setBody(elements, false)

            assertEquals(false, struct.isOpaque())
        }
    }
})

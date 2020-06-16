package dev.supergrecko.vexe.llvm.unit.ir.types

import dev.supergrecko.vexe.llvm.ir.TypeKind
import dev.supergrecko.vexe.llvm.ir.types.FloatType
import dev.supergrecko.vexe.llvm.ir.types.IntType
import dev.supergrecko.vexe.llvm.ir.types.StructType
import dev.supergrecko.vexe.test.TestSuite
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class StructureTypeTest : TestSuite({
    describe("Creation from user-land constructor") {
        val type = StructType(listOf(), false)

        assertEquals(TypeKind.Struct, type.getTypeKind())
    }

    describe("Creation via LLVM reference") {
        val type = StructType(listOf(IntType(16)), false)
        val second = StructType(type.ref)

        assertEquals(TypeKind.Struct, second.getTypeKind())
    }

    describe("All structures are sized") {
        val arg = FloatType(TypeKind.Float)
        val type = StructType(listOf(arg), false)

        assertTrue { type.isSized() }

        val struct = StructType(listOf(), false)

        assertTrue { struct.isSized() }
    }

    describe("Struct element size matches") {
        val struct1 = StructType(listOf(), false)
        val struct2 = StructType(listOf(IntType(32)), false)

        assertEquals(0, struct1.getElementCount())
        assertEquals(1, struct2.getElementCount())
    }

    describe("Element types match") {
        val elements = listOf(IntType(32))
        val struct = StructType(elements, false)

        val (first) = struct.getElementTypes()
        assertEquals(elements.first().ref, first.ref)
    }

    describe("A packed struct is packed") {
        val struct = StructType(listOf(), true)

        assertTrue { struct.isPacked() }
    }

    describe("An unnamed struct is literal") {
        val struct = StructType(listOf(), true)

        assertTrue { struct.isLiteral() }
    }

    describe("Giving a structure a name matches") {
        val struct = StructType("StructureName")

        assertEquals("StructureName", struct.getName())

        assertTrue { struct.isOpaque() }
    }

    describe("An opaque struct is no longer opaque after body is set") {
        val struct = StructType("test_struct")

        assertEquals(true, struct.isOpaque())

        val elements = listOf(IntType(32))
        struct.setBody(elements, false)

        assertEquals(false, struct.isOpaque())
    }
})

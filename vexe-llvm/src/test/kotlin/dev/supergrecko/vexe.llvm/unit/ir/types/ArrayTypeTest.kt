package dev.supergrecko.vexe.llvm.unit.ir.types

import dev.supergrecko.vexe.llvm.ir.TypeKind
import dev.supergrecko.vexe.llvm.ir.types.ArrayType
import dev.supergrecko.vexe.llvm.ir.types.IntType
import dev.supergrecko.vexe.test.TestSuite
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import org.junit.jupiter.api.Test

internal class ArrayTypeTest : TestSuite({
    describe("Creation from user-land constructor") {
        val type = IntType(64)
        val arr = type.toArrayType(10)

        assertEquals(TypeKind.Array, arr.getTypeKind())
        assertEquals(10, arr.getElementCount())
    }

    describe("Creation via LLVM reference") {
        val type = IntType(1).toArrayType(10)
        val second = ArrayType(type.ref)

        assertEquals(TypeKind.Array, second.getTypeKind())
    }

    describe("The LLVMType references match each other") {
        val type = IntType(32)
        val arr = ArrayType(type, 10)

        assertEquals(10, arr.getElementCount())
        assertEquals(type.ref, arr.getElementType().ref)
    }

    describe("The Subtype trait refers to the same LLVMType") {
        val type = IntType(32)
        val arr = ArrayType(type, 10)

        val children = arr.getSubtypes()

        assertEquals(10, children.size)
        assertEquals(type.ref, children.first().ref)
    }

    describe("Declaring type of negative size fails") {
        val type = IntType(32)

        assertFailsWith<IllegalArgumentException> {
            type.toArrayType(-100)
        }
    }
})
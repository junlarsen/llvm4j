package dev.supergrecko.vexe.llvm.unit.ir.types

import dev.supergrecko.vexe.llvm.ir.TypeKind
import dev.supergrecko.vexe.llvm.ir.types.IntType
import dev.supergrecko.vexe.llvm.ir.types.PointerType
import dev.supergrecko.vexe.test.TestSuite
import kotlin.test.assertEquals

internal class PointerTypeTest : TestSuite({
    describe("Creation from user-land constructor") {
        val type = IntType(64)
        val ptr = type.toPointerType()

        assertEquals(TypeKind.Pointer, ptr.getTypeKind())
    }

    describe("Creation via LLVM reference") {
        val type = IntType(1).toPointerType(10)
        val second = PointerType(type.ref)

        assertEquals(TypeKind.Pointer, second.getTypeKind())
    }

    describe("The underlying type matches") {
        val type = IntType(32)
        val ptr = type.toPointerType()

        assertEquals(type.ref, ptr.getElementType().ref)
    }

    describe("The element subtype matches") {
        val type = IntType(32)
        val ptr = type.toPointerType()

        assertEquals(type.getTypeKind(), ptr.getSubtypes().first().getTypeKind())
    }

    describe("The element count is 1 for pointers") {
        val type = IntType(32).toPointerType()

        assertEquals(1, type.getElementCount())
    }

    describe("A given address space matches") {
        val type = IntType(32)
        val ptr = type.toPointerType(100)

        assertEquals(100, ptr.getAddressSpace())
    }
})

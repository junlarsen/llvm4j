package dev.supergrecko.vexe.llvm.unit.ir.types

import dev.supergrecko.vexe.llvm.ir.TypeKind
import dev.supergrecko.vexe.llvm.ir.types.IntType
import kotlin.test.assertEquals
import org.spekframework.spek2.Spek

internal class PointerTypeTest : Spek({
    test("create pointer to integer type") {
        val type = IntType(64)
        val ptr = type.toPointerType()

        assertEquals(TypeKind.Pointer, ptr.getTypeKind())
    }

    test("the type we are pointing to matches") {
        val type = IntType(32)
        val ptr = type.toPointerType()

        assertEquals(type.ref, ptr.getElementType().ref)
    }

    test("the element subtype is equal to the pointee") {
        val type = IntType(32)
        val ptr = type.toPointerType()

        assertEquals(type.getTypeKind(), ptr.getSubtypes().first().getTypeKind())
    }

    test("a created pointer type has a size of 1") {
        val type = IntType(32).toPointerType()

        assertEquals(1, type.getElementCount())
    }

    test("a pointer may be assigned a address space") {
        val type = IntType(32)
        val ptr = type.toPointerType(100)

        assertEquals(100, ptr.getAddressSpace())
    }
})

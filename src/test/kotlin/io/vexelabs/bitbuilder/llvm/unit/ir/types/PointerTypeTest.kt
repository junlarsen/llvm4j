package io.vexelabs.bitbuilder.llvm.unit.ir.types

import io.vexelabs.bitbuilder.llvm.ir.Context
import io.vexelabs.bitbuilder.llvm.ir.TypeKind
import io.vexelabs.bitbuilder.llvm.ir.types.IntType
import io.vexelabs.bitbuilder.llvm.setup
import org.spekframework.spek2.Spek
import kotlin.test.assertEquals

internal class PointerTypeTest : Spek({
    setup()

    val context: Context by memoized()

    test("create pointer to integer type") {
        val type = context.getIntType(32)
        val ptr = type.intoPointerType()

        assertEquals(TypeKind.Pointer, ptr.getTypeKind())
    }

    test("the type we are pointing to matches") {
        val type = context.getIntType(32)
        val ptr = type.intoPointerType()

        assertEquals(type.ref, ptr.getElementType().ref)
    }

    test("the element subtype is equal to the pointee") {
        val type = context.getIntType(32)
        val ptr = type.intoPointerType()

        assertEquals(type.getTypeKind(), ptr.getSubtypes().first().getTypeKind())
    }

    test("a created pointer type has a size of 1") {
        val type = context.getIntType(32)
        val ptr = type.intoPointerType()

        assertEquals(1, ptr.getElementCount())
    }

    test("a pointer may be assigned a address space") {
        val type = context.getIntType(32)
        val ptr = type.intoPointerType(withAddressSpace = 100)

        assertEquals(100, ptr.getAddressSpace())
    }
})

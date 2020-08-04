package dev.supergrecko.vexe.llvm.unit.ir.types

import dev.supergrecko.vexe.llvm.ir.TypeKind
import dev.supergrecko.vexe.llvm.ir.types.ArrayType
import dev.supergrecko.vexe.llvm.ir.types.IntType
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import org.spekframework.spek2.Spek

internal class ArrayTypeTest : Spek({
    test("create an array type with 10 elements") {
        val type = IntType(64)
        val arr = type.toArrayType(10)

        assertEquals(TypeKind.Array, arr.getTypeKind())
        assertEquals(10, arr.getElementCount())
    }

    test("the element type of an array matches the original type") {
        val type = IntType(32)
        val arr = ArrayType(type, 10)

        assertEquals(10, arr.getElementCount())
        assertEquals(type.ref, arr.getElementType().ref)
    }

    test("the subtype matches the original type") {
        val type = IntType(32)
        val arr = ArrayType(type, 10)

        val children = arr.getSubtypes()

        assertEquals(10, children.size)
        assertEquals(type.ref, children.first().ref)
    }

    test("an array may not have a negative size") {
        val type = IntType(32)

        assertFailsWith<IllegalArgumentException> {
            type.toArrayType(-100)
        }
    }
})

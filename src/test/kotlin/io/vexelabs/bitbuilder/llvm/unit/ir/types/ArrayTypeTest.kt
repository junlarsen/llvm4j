package io.vexelabs.bitbuilder.llvm.unit.ir.types

import io.vexelabs.bitbuilder.llvm.ir.Context
import io.vexelabs.bitbuilder.llvm.ir.TypeKind
import io.vexelabs.bitbuilder.llvm.ir.types.ArrayType
import io.vexelabs.bitbuilder.llvm.ir.types.IntType
import io.vexelabs.bitbuilder.llvm.setup
import org.spekframework.spek2.Spek
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class ArrayTypeTest : Spek({
    setup()

    val context: Context by memoized()

    test("create an array type with 10 elements") {
        val arr = context.getIntType(32).intoArrayType(10)

        assertEquals(TypeKind.Array, arr.getTypeKind())
        assertEquals(10, arr.getElementCount())
    }

    test("the element type of an array matches the original type") {
        val type = context.getIntType(32)
        val arr = type.intoArrayType(10)

        assertEquals(10, arr.getElementCount())
        assertEquals(type.ref, arr.getElementType().ref)
    }

    test("the subtype matches the original type") {
        val type = context.getIntType(32)
        val arr = type.intoArrayType(10)

        val children = arr.getSubtypes()

        assertEquals(10, children.size)
        assertEquals(type.ref, children.first().ref)
    }

    test("an array may not have a negative size") {
        val type = context.getIntType(32)

        assertFailsWith<IllegalArgumentException> {
            type.intoArrayType(-100)
        }
    }
})

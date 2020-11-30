package io.vexelabs.bitbuilder.llvm.unit.ir.types

import io.vexelabs.bitbuilder.llvm.ir.Context
import io.vexelabs.bitbuilder.llvm.ir.TypeKind
import io.vexelabs.bitbuilder.llvm.setup
import org.spekframework.spek2.Spek
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class VectorTypeTest : Spek({
    setup()

    val context: Context by memoized()

    test("create a vector type of 1000 integers") {
        val type = context.getIntType(32)
        val vec = type.getVectorType(1000)

        assertEquals(TypeKind.Vector, vec.getTypeKind())
        assertEquals(1000, vec.getElementCount())
    }

    test("the element type matches the original type") {
        val type = context.getIntType(32)
        val vec = type.getVectorType(10)

        assertEquals(10, vec.getElementCount())
        assertEquals(type.ref, vec.getElementType().ref)
    }

    test("the subtype matches the original type") {
        val type = context.getIntType(32)
        val vec = type.getVectorType(10)

        assertEquals(10, vec.getSubtypes().size)
        assertEquals(type.ref, vec.getSubtypes().first().ref)
    }

    test("creating a vector of negative size fails") {
        val type = context.getFloatType(TypeKind.Float)

        assertFailsWith<IllegalArgumentException> {
            type.getVectorType(-100)
        }
    }
})

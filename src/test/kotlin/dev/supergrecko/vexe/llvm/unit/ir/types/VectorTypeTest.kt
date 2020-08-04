package dev.supergrecko.vexe.llvm.unit.ir.types

import dev.supergrecko.vexe.llvm.ir.TypeKind
import dev.supergrecko.vexe.llvm.ir.types.FloatType
import dev.supergrecko.vexe.llvm.ir.types.IntType
import dev.supergrecko.vexe.llvm.ir.types.VectorType
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import org.spekframework.spek2.Spek

internal class VectorTypeTest : Spek({
    test("create a vector type of 1000 integers") {
        val type = IntType(32)
        val vec = type.toVectorType(1000)

        assertEquals(TypeKind.Vector, vec.getTypeKind())
        assertEquals(1000, vec.getElementCount())
    }

    test("the element type matches the original type") {
        val type = IntType(32)
        val vec = VectorType(type, 10)

        assertEquals(10, vec.getElementCount())
        assertEquals(type.ref, vec.getElementType().ref)
    }

    test("the subtype matches the original type") {
        val type = IntType(32)
        val vec = VectorType(type, 10)

        assertEquals(10, vec.getSubtypes().size)
        assertEquals(type.ref, vec.getSubtypes().first().ref)
    }

    test("creating a vector of negative size fails") {
        val type = FloatType(TypeKind.Float)

        assertFailsWith<IllegalArgumentException> {
            type.toVectorType(-100)
        }

        assertFailsWith<IllegalArgumentException> {
            VectorType(type, -100)
        }
    }
})

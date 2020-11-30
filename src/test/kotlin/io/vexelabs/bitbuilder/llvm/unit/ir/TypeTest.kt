package io.vexelabs.bitbuilder.llvm.unit.ir

import io.vexelabs.bitbuilder.llvm.ir.Context
import io.vexelabs.bitbuilder.llvm.ir.TypeKind
import io.vexelabs.bitbuilder.llvm.setup
import org.spekframework.spek2.Spek
import kotlin.test.assertEquals

internal object TypeTest : Spek({
    setup()

    val context: Context by memoized()

    group("construction of types from existing types") {
        test("creating a pointer type") {
            val ptr = context.getIntType(32).getPointerType()

            assertEquals(TypeKind.Pointer, ptr.getTypeKind())
        }

        test("creating a vector type") {
            val vec = context.getIntType(32).getVectorType(100)

            assertEquals(TypeKind.Vector, vec.getTypeKind())
        }

        test("creating an array type") {
            val arr = context.getIntType(32).getArrayType(100)

            assertEquals(TypeKind.Array, arr.getTypeKind())
        }
    }

    test("getting the context from a type") {
        val type = context.getIntType(32)
        val subject = type.getContext()

        assertEquals(context.ref, subject.ref)
    }

    group("printing the string representation of a type") {
        test("integer types are prefixed with i") {
            val type = context.getIntType(32)
            val ir = "i32"

            assertEquals(ir, type.getIR().toString())
        }

        test("structures print their body") {
            val i32 = context.getIntType(32)
            val struct = context.getStructType(i32, packed = true)
            val ir = "<{ i32 }>"

            assertEquals(ir, struct.getIR().toString())
        }
    }
})

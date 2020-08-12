package io.vexelabs.bitbuilder.llvm.unit.ir

import io.vexelabs.bitbuilder.llvm.ir.Context
import io.vexelabs.bitbuilder.llvm.ir.TypeKind
import io.vexelabs.bitbuilder.llvm.ir.types.IntType
import io.vexelabs.bitbuilder.llvm.ir.types.StructType
import io.vexelabs.bitbuilder.llvm.setup
import kotlin.test.assertEquals
import org.spekframework.spek2.Spek

internal object TypeTest : Spek({
    setup()

    val context: Context by memoized()

    group("construction of types from existing types") {
        test("creating a pointer type") {
            val type = IntType(32)
            val ptr = type.toPointerType()

            assertEquals(TypeKind.Pointer, ptr.getTypeKind())
        }

        test("creating a vector type") {
            val type = IntType(32)
            val vec = type.toVectorType(100)

            assertEquals(TypeKind.Vector, vec.getTypeKind())
        }

        test("creating an array type") {
            val type = IntType(32)
            val arr = type.toArrayType(1)

            assertEquals(TypeKind.Array, arr.getTypeKind())
        }
    }

    test("getting the context from a type") {
        val type = IntType(1, context)
        val subject = type.getContext()

        assertEquals(context.ref, subject.ref)
    }

    group("printing the string representation of a type") {
        test("integer types are prefixed with i") {
            val type = IntType(32)
            val ir = "i32"

            assertEquals(ir, type.getIR().toString())
        }

        test("structures print their body") {
            val struct = StructType(
                listOf(IntType(32)),
                true,
                context
            )
            val ir = "<{ i32 }>"

            assertEquals(ir, struct.getIR().toString())
        }
    }
})

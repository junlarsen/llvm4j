package dev.supergrecko.vexe.llvm.unit.ir

import dev.supergrecko.vexe.llvm.ir.Context
import dev.supergrecko.vexe.llvm.ir.TypeKind
import dev.supergrecko.vexe.llvm.ir.types.IntType
import dev.supergrecko.vexe.llvm.ir.types.StructType
import dev.supergrecko.vexe.llvm.setup
import org.spekframework.spek2.Spek
import kotlin.test.assertEquals

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
        test("integer type ...") {
            val type = IntType(32)
        }

        test("structures print their body") {
            val struct = StructType(
                listOf(IntType(32)),
                true,
                context
            )

            println(struct.getStringRepresentation().getString())
        }
    }
})

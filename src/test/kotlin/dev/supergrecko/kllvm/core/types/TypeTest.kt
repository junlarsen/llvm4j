package dev.supergrecko.kllvm.core.types

import dev.supergrecko.kllvm.core.typedefs.Context
import dev.supergrecko.kllvm.core.enumerations.TypeKind
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class TypeTest {
    @Test
    fun `test creation of pointer type`() {
        val type = IntType(64)
        val ptr = type.toPointerType()

        assertEquals(TypeKind.Pointer, ptr.getTypeKind())
    }

    @Test
    fun `test creation of array type`() {
        val type = IntType(64)
        val arr = type.toArrayType(10)

        assertEquals(TypeKind.Array, arr.getTypeKind())
        assertEquals(10, arr.getElementCount())
    }

    @Test
    fun `test creation of vector type`() {
        val type = IntType(32)
        val vec = type.toVectorType(1000)

        assertEquals(TypeKind.Vector, vec.getTypeKind())
        assertEquals(1000, vec.getElementCount())
    }

    @Test
    fun `casting into other type works when expected to`() {
        val type = IntType(32)
        val ptr = type.toPointerType()
        val underlying = ptr.getElementType()

        assertEquals(type.ref, IntType(underlying).ref)
    }

    @Test
    fun `casting won't fail when the underlying type is different`() {
        // it is impossible to guarantee that the underlying types is valid or invalid
        val type = IntType(32)
        val ptr = type.toPointerType()
        val underlying = ptr.getElementType()

        assertEquals(type.ref, FunctionType(underlying).ref)
    }

    @Test
    fun `getting a type works properly`() {
        val type = FloatType(TypeKind.Float)

        assertEquals(TypeKind.Float, type.getTypeKind())
    }

    @Test
    fun `negative size is illegal`() {
        assertFailsWith<IllegalArgumentException> {
            IntType(-1)
        }
    }

    @Test
    fun `too huge size is illegal`() {
        assertFailsWith<IllegalArgumentException> {
            IntType(1238234672)
        }
    }

    @Test
    fun `is sized works for integer`() {
        val type = IntType(192)

        assertEquals(true, type.isSized())
    }

    @Test
    fun `is sized works for struct`() {
        val arg = FloatType(TypeKind.Float)
        val type = StructType(listOf(arg), false)

        assertEquals(true, type.isSized())
    }

    @Test
    fun `retrieving context works`() {
        val ctx = Context()
        val type = IntType(32, ctx)

        val typeCtx = type.getContext()

        assertEquals(ctx.ref, typeCtx.ref)
    }

    @Test
    fun `getting a name representation works`() {
        val type = IntType(32)

        val msg = type.getStringRepresentation()

        // LLVM does apparently not retain bit size for integer types here
        assertEquals("i", msg.getString())

        msg.dispose()
    }
}

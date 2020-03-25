package dev.supergrecko.kllvm.core.types

import dev.supergrecko.kllvm.core.typedefs.LLVMContext
import dev.supergrecko.kllvm.core.enumerations.LLVMTypeKind
import dev.supergrecko.kllvm.factories.TypeFactory
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class LLVMTypeTest {
    @Test
    fun `test creation of pointer type`() {
        val type = TypeFactory.integer(64)
        val ptr = type.toPointerType()

        assertEquals(LLVMTypeKind.Pointer, ptr.getTypeKind())
    }

    @Test
    fun `test creation of array type`() {
        val type = TypeFactory.integer(64)
        val arr = type.toArrayType(10)

        assertEquals(LLVMTypeKind.Array, arr.getTypeKind())
        assertEquals(10, arr.getElementCount())
    }

    @Test
    fun `test creation of vector type`() {
        val type = TypeFactory.integer(32)
        val vec = type.toVectorType(1000)

        assertEquals(LLVMTypeKind.Vector, vec.getTypeKind())
        assertEquals(1000, vec.getElementCount())
    }

    @Test
    fun `casting into other type works when expected to`() {
        val type = TypeFactory.integer(32)
        val ptr = type.toPointerType()
        val underlying = ptr.getElementType()

        assertEquals(type.llvmType, underlying.cast<LLVMIntType>().llvmType)
    }

    @Test
    fun `casting won't fail when the underlying type is different`() {
        // This behavior is documented at LLVMType. There is no way
        // to guarantee that the underlying types is valid or invalid
        val type = TypeFactory.integer(32)
        val ptr = type.toPointerType()
        val underlying = ptr.getElementType()

        assertEquals(type.llvmType, underlying.cast<LLVMFunctionType>().llvmType)
    }

    @Test
    fun `getting a type works properly`() {
        val type = TypeFactory.float(LLVMTypeKind.Float)

        assertEquals(LLVMTypeKind.Float, type.getTypeKind())
    }

    @Test
    fun `negative size is illegal`() {
        assertFailsWith<IllegalArgumentException> {
            TypeFactory.integer(-1)
        }
    }

    @Test
    fun `too huge size is illegal`() {
        assertFailsWith<IllegalArgumentException> {
            TypeFactory.integer(1238234672)
        }
    }

    @Test
    fun `is sized works for integer`() {
        val type = TypeFactory.integer(192)

        assertEquals(true, type.isSized())
    }

    @Test
    fun `is sized works for struct`() {
        val arg = TypeFactory.float(LLVMTypeKind.Float)
        val type = TypeFactory.struct(listOf(arg), false)

        assertEquals(true, type.isSized())
    }

    @Test
    fun `retrieving context works`() {
        val ctx = LLVMContext.create()
        val type = TypeFactory.integer(32, ctx)

        val typeCtx = type.getContext()

        assertEquals(ctx.llvmCtx, typeCtx.llvmCtx)
    }

    @Test
    fun `getting a name representation works`() {
        val type = TypeFactory.integer(32)

        val msg = type.getStringRepresentation()

        // LLVM does apparently not retain bit size for integer types here
        assertEquals("i", msg.getString())

        msg.dispose()
    }
}

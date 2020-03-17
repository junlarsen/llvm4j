package dev.supergrecko.kllvm.core.types

import dev.supergrecko.kllvm.core.LLVMType
import dev.supergrecko.kllvm.core.enumerations.LLVMTypeKind
import org.junit.jupiter.api.Test
import java.lang.IllegalArgumentException
import kotlin.test.*

class LLVMTypeTest {
    @Test
    fun `test creation of pointer type`() {
        val type = LLVMType.createInteger(64)
        val ptr = type.toPointer()

        assertEquals(LLVMTypeKind.Pointer, ptr.getTypeKind())
    }

    @Test
    fun `test creation of array type`() {
        val type = LLVMType.createInteger(64)
        val arr = type.toArray(10)

        assertEquals(LLVMTypeKind.Array, arr.getTypeKind())
        assertEquals(10, arr.getElementSize())
    }

    @Test
    fun `test creation of vector type`() {
        val type = LLVMType.createInteger(32)
        val vec = type.toVector(1000)

        assertEquals(LLVMTypeKind.Vector, vec.getTypeKind())
        assertEquals(1000, vec.getElementSize())
    }

    @Test
    fun `casting into other type works when expected to`() {
        val type = LLVMType.createInteger(32)
        val ptr = type.toPointer()
        val underlying = ptr.getElementType()

        assertEquals(type.llvmType, underlying.cast(LLVMTypeKind.Integer).llvmType)
    }

    @Test
    fun `casting won't fail when the underlying type is different`() {
        // This behavior is documented at LLVMType. There is no way
        // to guarantee that the underlying types is valid or invalid
        val type = LLVMType.createInteger(32)
        val ptr = type.toPointer()
        val underlying = ptr.getElementType()

        assertEquals(type.llvmType, underlying.cast(LLVMTypeKind.Function).llvmType)
    }

    @Test
    fun `getting a type works properly`() {
        val type = LLVMType.create(LLVMTypeKind.Float)

        assertEquals(LLVMTypeKind.Float, type.getTypeKind())
    }

    @Test
    fun `calling function with different type fails`() {
        val type = LLVMType.create(LLVMTypeKind.Float)

        assertFailsWith<IllegalArgumentException> {
            type.getElementSize()
        }
    }

    @Test
    fun `negative size is illegal`() {
        assertFailsWith<IllegalArgumentException> {
            LLVMType.createInteger(-1)
        }
    }

    @Test
    fun `too huge size is illegal`() {
        assertFailsWith<IllegalArgumentException> {
            LLVMType.createInteger(1000123012)
        }
    }
}

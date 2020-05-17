package dev.supergrecko.kllvm.ir.values.constants

import dev.supergrecko.kllvm.ir.types.IntType
import dev.supergrecko.kllvm.ir.values.constants.ConstantArray
import dev.supergrecko.kllvm.test.constIntPairOf
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ConstantArrayTest {
    @Test
    fun `Creation via LLVM reference`() {
        val str = ConstantArray("Hello")
        val borrow = ConstantArray(str.ref)

        assertEquals(str.ref, borrow.ref)
    }

    @Test
    fun `Creation of string (i8 array) from user-land`() {
        val str = ConstantArray("Hello")

        assertTrue { str.isConstantString() }

        // LLVM strips the null-termination
        assertEquals("Hello", str.getAsString())
    }

    @Test
    fun `Creation from user-land constructor`() {
        val ty = IntType(8)
        val (one, two) = constIntPairOf(1, 2)
        val arr = ConstantArray(ty, listOf(one, two))

        val first = arr.getElementAsConstant(0)

        assertEquals(one.getSignedValue(), first.asIntValue().getSignedValue())
    }
}

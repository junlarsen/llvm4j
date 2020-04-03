package dev.supergrecko.kllvm.core.values

import dev.supergrecko.kllvm.core.types.IntType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class IntValueTest {
    @Test
    fun `test negation of value`() {
        val ty = IntType(32)
        val v = IntValue(ty, 100L, true)

        val neg = v.neg()

        assertEquals(-100L, neg.getSignedValue())
    }

    @Test
    fun `test inversion of value`() {
        val ty = IntType(32)
        val v = IntValue(ty, 100L, true)

        val not = v.not()

        assertEquals(-101, not.getSignedValue())
    }

    @Test
    fun `test addition of values`() {
        val ty = IntType(32)

        val v1 = IntValue(ty, 100L, true)
        val v2 = IntValue(ty, 300L, true)

        val sum = v1.add(v2)

        assertEquals(400, sum.getSignedValue())
        assertEquals(400, sum.getUnsignedValue())
    }

    @Test
    fun `test subtraction of values`() {
        val ty = IntType(32)

        val v1 = IntValue(ty, 400L, true)
        val v2 = IntValue(ty, 200L, true)

        val diff = v1.sub(v2)

        assertEquals(200, diff.getSignedValue())
        assertEquals(200, diff.getUnsignedValue())
    }

    @Test
    fun `test multiplication of values`() {
        val ty = IntType(32)

        val v1 = IntValue(ty, 100L, true)
        val v2 = IntValue(ty, 10L, true)

        val product = v1.mul(v2)

        assertEquals(1000, product.getSignedValue())
        assertEquals(1000, product.getUnsignedValue())
    }

    @Test
    fun `test unsigned division`() {
        val ty = IntType(32)

        val v1 = IntValue(ty, 100L, false)
        val v2 = IntValue(ty, 10L, false)

        val quotient1 = v1.udiv(v2, true)
        val quotient2 = v1.udiv(v2, false)

        assertEquals(10, quotient1.getSignedValue())
        assertEquals(10, quotient2.getSignedValue())
    }

    @Test
    fun `test signed division`() {
        val ty = IntType(32)

        val v1 = IntValue(ty, 100L, true)
        val v2 = IntValue(ty, 10L, true)

        val quotient1 = v1.udiv(v2, true)
        val quotient2 = v1.udiv(v2, false)

        assertEquals(10, quotient1.getSignedValue())
        assertEquals(10, quotient2.getSignedValue())
    }

    @Test
    fun `test fp division`() {
        val ty = IntType(32)

        // 10 div 3 is not an even number
        val v1 = IntValue(ty, 10L, false)
        val v2 = IntValue(ty, 3L, false)

        val quotient1 = v1.udiv(v2, true)
        val quotient2 = v1.udiv(v2, false)

        assertEquals(3, quotient1.getSignedValue())
        assertEquals(3, quotient2.getSignedValue())
    }
}
package dev.supergrecko.kllvm.values

import dev.supergrecko.kllvm.types.IntType
import dev.supergrecko.kllvm.values.constants.ConstantInt
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ConstantIntTest {
    @Test
    fun `test negation of value`() {
        val ty = IntType(32)
        val v = ConstantInt(ty, 100L, true)

        val neg = v.neg()

        assertEquals(-100L, neg.getSignedValue())
    }

    @Test
    fun `test inversion of value`() {
        val ty = IntType(32)
        val v = ConstantInt(ty, 100L, true)

        val not = v.not()

        assertEquals(-101, not.getSignedValue())
    }

    @Test
    fun `test addition of values`() {
        val ty =
            IntType(32)

        val v1 = ConstantInt(ty, 100L, true)
        val v2 = ConstantInt(ty, 300L, true)

        val sum = v1.add(v2)

        assertEquals(400, sum.getSignedValue())
        assertEquals(400, sum.getUnsignedValue())
    }

    @Test
    fun `test subtraction of values`() {
        val ty = IntType(32)

        val v1 = ConstantInt(ty, 400L, true)
        val v2 = ConstantInt(ty, 200L, true)

        val diff = v1.sub(v2)

        assertEquals(200, diff.getSignedValue())
        assertEquals(200, diff.getUnsignedValue())
    }

    @Test
    fun `test multiplication of values`() {
        val ty = IntType(32)

        val v1 = ConstantInt(ty, 100L, true)
        val v2 = ConstantInt(ty, 10L, true)

        val product = v1.mul(v2)

        assertEquals(1000, product.getSignedValue())
        assertEquals(1000, product.getUnsignedValue())
    }

    @Test
    fun `test division`() {
        val ty = IntType(32)

        val v1 = ConstantInt(ty, 100L, false)
        val v2 = ConstantInt(ty, 10L, false)

        val quotient1 = v1.div(v2, exact = true, unsigned = false)
        val quotient2 = v1.div(v2, exact = false, unsigned = false)
        val quotient3 = v1.div(v2, exact = true, unsigned = true)
        val quotient4 = v1.div(v2, exact = false, unsigned = true)

        assertEquals(10, quotient1.getSignedValue())
        assertEquals(10, quotient2.getSignedValue())
        assertEquals(10, quotient3.getSignedValue())
        assertEquals(10, quotient4.getSignedValue())
    }

    @Test
    fun `test fp division`() {
        val ty =
            IntType(32)

        // 10 div 3 is not an even number
        val v1 = ConstantInt(ty, 10L, false)
        val v2 = ConstantInt(ty, 3L, false)

        val quotient1 = v1.div(v2, exact = true, unsigned = false)
        val quotient2 = v1.div(v2, exact = false, unsigned = false)
        val quotient3 = v1.div(v2, exact = true, unsigned = true)
        val quotient4 = v1.div(v2, exact = false, unsigned = true)

        assertEquals(3, quotient1.getSignedValue())
        assertEquals(3, quotient2.getSignedValue())
        assertEquals(3, quotient3.getSignedValue())
        assertEquals(3, quotient4.getSignedValue())
    }

    @Test
    fun `test remainder`() {
        val ty = IntType(32)

        val v1 = ConstantInt(ty, 10L, false)
        val v2 = ConstantInt(ty, 3L, false)

        val rem1 = v1.rem(v2, true)
        val rem2 = v1.rem(v2, false)

        assertEquals(1, rem1.getUnsignedValue())
        assertEquals(1, rem1.getSignedValue())

        assertEquals(1, rem2.getUnsignedValue())
        assertEquals(1, rem2.getSignedValue())
    }
}
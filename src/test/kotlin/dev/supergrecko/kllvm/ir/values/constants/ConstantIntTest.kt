package dev.supergrecko.kllvm.ir.values.constants

import dev.supergrecko.kllvm.ir.TypeKind
import dev.supergrecko.kllvm.test.runAll
import dev.supergrecko.kllvm.ir.instructions.IntPredicate
import dev.supergrecko.kllvm.ir.types.FloatType
import dev.supergrecko.kllvm.ir.types.IntType
import dev.supergrecko.kllvm.ir.types.PointerType
import dev.supergrecko.kllvm.test.constIntPairOf
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ConstantIntTest {
    @Test
    fun `construction with words`() {
        val ty = IntType(32)
        val value = ConstantInt(ty, listOf(100L, 20L))

        assertEquals(100, value.getSignedValue())
    }

    @Test
    fun `negation of value`() {
        val ty = IntType(32)
        val v = ConstantInt(ty, 100)

        val neg = v.neg()

        assertEquals(-100L, neg.getSignedValue())
    }

    @Test
    fun `inversion of value`() {
        val ty = IntType(32)
        val v = ConstantInt(ty, 100)

        val not = v.not()

        assertEquals(-101, not.getSignedValue())
    }

    @Test
    fun `addition of values`() {
        val ty = IntType(32)

        val v1 = ConstantInt(ty, 100)
        val v2 = ConstantInt(ty, 300)

        val sum = v1.add(v2)

        assertEquals(400, sum.getSignedValue())
        assertEquals(400, sum.getUnsignedValue())
    }

    @Test
    fun `subtraction of values`() {
        val ty = IntType(32)

        val v1 = ConstantInt(ty, 400)
        val v2 = ConstantInt(ty, 200)

        val diff = v1.sub(v2)

        assertEquals(200, diff.getSignedValue())
        assertEquals(200, diff.getUnsignedValue())
    }

    @Test
    fun `multiplication of values`() {
        val ty = IntType(32)

        val v1 = ConstantInt(ty, 100)
        val v2 = ConstantInt(ty, 10)

        val product = v1.mul(v2)

        assertEquals(1000, product.getSignedValue())
        assertEquals(1000, product.getUnsignedValue())
    }

    @Test
    fun `division of values`() {
        val ty = IntType(32)

        val v1 = ConstantInt(ty, 100, false)
        val v2 = ConstantInt(ty, 10, false)

        val quotient1 = v1.sdiv(v2, exact = true)
        val quotient2 = v1.sdiv(v2, exact = false)
        val quotient3 = v1.udiv(v2, exact = true)
        val quotient4 = v1.udiv(v2, exact = false)

        assertEquals(10, quotient1.getSignedValue())
        assertEquals(10, quotient2.getSignedValue())
        assertEquals(10, quotient3.getSignedValue())
        assertEquals(10, quotient4.getSignedValue())
    }

    @Test
    fun `floating point division`() {
        val ty = IntType(32)

        // 10 div 3 is not an even number
        val v1 = ConstantInt(ty, 10, false)
        val v2 = ConstantInt(ty, 3, false)

        val quotient1 = v1.sdiv(v2, exact = true)
        val quotient2 = v1.sdiv(v2, exact = false)
        val quotient3 = v1.udiv(v2, exact = true)
        val quotient4 = v1.udiv(v2, exact = false)

        assertEquals(3, quotient1.getSignedValue())
        assertEquals(3, quotient2.getSignedValue())
        assertEquals(3, quotient3.getSignedValue())
        assertEquals(3, quotient4.getSignedValue())
    }

    @Test
    fun `remainder of values`() {
        val ty = IntType(32)

        val v1 = ConstantInt(ty, 10, false)
        val v2 = ConstantInt(ty, 3, false)

        val rem1 = v1.urem(v2)
        val rem2 = v1.srem(v2)

        assertEquals(1, rem1.getUnsignedValue())
        assertEquals(1, rem1.getSignedValue())
        assertEquals(1, rem2.getUnsignedValue())
        assertEquals(1, rem2.getSignedValue())
    }

    @Test
    fun `logical and`() {
        val (lhs, rhs) = constIntPairOf(2, 6)

        val res = lhs.and(rhs).getSignedValue()

        assertEquals(2 and 6, res)
    }

    @Test
    fun `logical or`() {
        val (lhs, rhs) = constIntPairOf(16, 92)

        val res = lhs.or(rhs).getSignedValue()

        assertEquals(16 or 92, res)
    }

    @Test
    fun `logical xor`() {
        val (lhs, rhs) = constIntPairOf(100, 200)

        val res = lhs.xor(rhs).getSignedValue()

        assertEquals(100 xor 200, res)
    }

    @Test
    fun `less and greater comparison`() {
        val (lhs, rhs) = constIntPairOf(10, 20)

        val expected = arrayOf<Long>(
            0, 1, // eq, ne
            0, 0, // ugt, uge
            1, 1, // ult ule
            0, 0, // sgt sge
            1, 1 // slt sle
        )

        runAll(*IntPredicate.values()) { it, idx ->
            val res = lhs.cmp(it, rhs).getUnsignedValue()

            val expect = expected[idx]

            assertEquals(expect, res)
        }
    }

    @Test
    fun `left shift`() {
        val (lhs, rhs) = constIntPairOf(10, 20)

        val res = lhs.shl(rhs).getSignedValue()

        assertEquals(10 shl 20, res)
    }

    @Test
    fun `right shift`() {
        val (lhs, rhs) = constIntPairOf(10, 20)

        val res = lhs.lshr(rhs).getSignedValue()

        assertEquals(10 shr 20, res)
    }

    @Test
    fun `arithmetic right shift`() {
        val (lhs, rhs) = constIntPairOf(10, 20)

        val res = lhs.ashr(rhs).getSignedValue()

        assertEquals(10 shr 20, res)
    }

    @Test
    fun `type truncation`() {
        val lhs = ConstantInt(IntType(8), 64)

        val trunc = lhs.trunc(IntType(1))

        assertEquals(0, trunc.getUnsignedValue())
    }

    @Test
    fun `type extension`() {
        val lhs = ConstantInt(IntType(8), 64)

        val sext = lhs.sext(IntType(16))
        val zext = lhs.zext(IntType(16))

        assertEquals(64, sext.getSignedValue())
        assertEquals(64, zext.getUnsignedValue())
    }

    @Test
    fun `convert to float`() {
        val lhs = ConstantInt(IntType(64), 64)

        val si = lhs.sitofp(FloatType(TypeKind.Float))
        val ui = lhs.uitofp(FloatType(TypeKind.Double))

        assertEquals(64.0, si.getDouble())
        assertEquals(64.0, ui.getDouble())
        assertFalse { si.getDoubleLosesPrecision() }
        assertFalse { si.getDoubleLosesPrecision() }
    }

    @Test
    fun `convert to pointer`() {
        val ty = IntType(64)
        val lhs = ConstantInt(ty, 100)
        val ptr = lhs.ptrcast(PointerType(ty))

        assertTrue { ptr.isConstant() }

        val num = ptr.intcast(ty)

        assertEquals(lhs.getSignedValue(), num.getSignedValue())
    }

    @Test
    fun `convert to other int type`() {
        val targetTy = IntType(128)
        val lhs = ConstantInt(IntType(32), 100000)

        val second = lhs.intcast(targetTy, true)

        assertEquals(lhs.getSignedValue(), second.getSignedValue())
    }

    @Test
    fun `select instruction`() {
        // true
        val cond = ConstantInt(IntType(1), 1)

        val (lhs, rhs) = constIntPairOf(10, 20)

        val res = cond.select(lhs, rhs)

        assertEquals(10, res.asIntValue().getSignedValue())
    }
}

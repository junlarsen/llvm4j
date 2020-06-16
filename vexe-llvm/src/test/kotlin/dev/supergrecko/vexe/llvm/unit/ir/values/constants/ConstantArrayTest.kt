package dev.supergrecko.vexe.llvm.unit.ir.values.constants

import dev.supergrecko.vexe.llvm.ir.types.IntType
import dev.supergrecko.vexe.llvm.ir.values.constants.ConstantArray
import dev.supergrecko.vexe.llvm.utils.constIntPairOf
import dev.supergrecko.vexe.test.TestSuite
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class ConstantArrayTest : TestSuite({
    describe("Creation via LLVM reference") {
        val str = ConstantArray("Hello")
        val borrow = ConstantArray(str.ref)

        assertEquals(str.ref, borrow.ref)
    }

    describe("Creation of string (i8 array) from user-land") {
        val str = ConstantArray("Hello")

        assertTrue { str.isConstantString() }

        // LLVM strips the null-termination
        assertEquals("Hello", str.getAsString())
    }

    describe("Creation from user-land constructor") {
        val ty = IntType(8)
        val (one, two) = constIntPairOf(1, 2)
        val arr = ConstantArray(ty, listOf(one, two))

        val first = arr.getElementAsConstant(0)

        assertEquals(one.getSignedValue(), first.asIntValue().getSignedValue())
    }
})

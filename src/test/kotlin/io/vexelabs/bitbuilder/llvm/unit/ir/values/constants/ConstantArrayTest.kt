package io.vexelabs.bitbuilder.llvm.unit.ir.values.constants

import io.vexelabs.bitbuilder.internal.cast
import io.vexelabs.bitbuilder.llvm.ir.Context
import io.vexelabs.bitbuilder.llvm.ir.values.constants.ConstantInt
import io.vexelabs.bitbuilder.llvm.setup
import org.spekframework.spek2.Spek
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class ConstantArrayTest : Spek({
    setup()

    val context: Context by memoized()

    test("create string as constant i8 array") {
        val str = context.createConstantArrayFromString("Hello", true)

        assertTrue { str.isConstantString() }

        // LLVM strips the null-termination
        assertEquals("Hello", str.getAsString())
    }

    test("create a constant array") {
        val i8 = context.getIntType(8)
        val i32 = context.getIntType(32)
        val lhs = i32.getConstant(1)
        val rhs = i32.getConstant(2)
        val arr = i8.getConstantArray(i32, lhs, rhs)

        val first = arr.getElementAsConstant(0)

        assertEquals(
            lhs.getSignedValue(),
            cast<ConstantInt>(first).getSignedValue()
        )
    }
})

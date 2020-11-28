package io.vexelabs.bitbuilder.llvm.unit.ir.values.constants

import io.vexelabs.bitbuilder.internal.cast
import io.vexelabs.bitbuilder.llvm.ir.Context
import io.vexelabs.bitbuilder.llvm.ir.values.constants.ConstantArray
import io.vexelabs.bitbuilder.llvm.ir.values.constants.ConstantInt
import io.vexelabs.bitbuilder.llvm.setup
import org.spekframework.spek2.Spek
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class ConstantArrayTest : Spek({
    setup()

    val context: Context by memoized()

    test("create string as constant i8 array") {
        val str = ConstantArray("Hello")

        assertTrue { str.isConstantString() }

        // LLVM strips the null-termination
        assertEquals("Hello", str.getAsString())
    }

    test("create a constant array") {
        val i8 = context.getIntType(8)
        val i32 = context.getIntType(32)
        val lhs = ConstantInt(i32, 1)
        val rhs = ConstantInt(i32, 2)
        val arr = ConstantArray(i8, listOf(lhs, rhs))

        val first = arr.getElementAsConstant(0)

        assertEquals(
            lhs.getSignedValue(),
            cast<ConstantInt>(first).getSignedValue()
        )
    }
})

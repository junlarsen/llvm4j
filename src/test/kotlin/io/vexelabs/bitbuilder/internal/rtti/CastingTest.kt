package io.vexelabs.bitbuilder.internal.rtti

import io.vexelabs.bitbuilder.internal.cast
import io.vexelabs.bitbuilder.llvm.ir.Context
import io.vexelabs.bitbuilder.llvm.ir.Use
import io.vexelabs.bitbuilder.llvm.ir.Value
import io.vexelabs.bitbuilder.llvm.ir.values.constants.ConstantInt
import io.vexelabs.bitbuilder.llvm.setup
import org.spekframework.spek2.Spek
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal object CastingTest : Spek({
    setup()

    val context: Context by memoized()

    test("casting between valid paths") {
        // Path: Value -> ConstantInt : LLVMValueRef
        val i32 = context.getIntType(32)
        val value: Value = i32.getConstant(1)
        val int = cast<ConstantInt>(value)

        assertEquals(1, int.getSignedValue())
    }

    test("invalid paths will fail") {
        // Use has a constructor from LLVMValueRef, but is not a valid path
        // Path: Value -> Use : LLVMValueRef
        val i32 = context.getIntType(32)
        val value: Value = i32.getConstant(1)

        assertFailsWith<IllegalArgumentException> {
            cast<Use>(value)
        }
    }
})

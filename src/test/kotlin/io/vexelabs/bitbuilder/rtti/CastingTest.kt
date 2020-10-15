package io.vexelabs.bitbuilder.rtti

import io.vexelabs.bitbuilder.llvm.ir.Use
import io.vexelabs.bitbuilder.llvm.ir.Value
import io.vexelabs.bitbuilder.llvm.ir.types.IntType
import io.vexelabs.bitbuilder.llvm.ir.values.constants.ConstantInt
import org.spekframework.spek2.Spek
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal object CastingTest : Spek({
    test("casting between valid paths") {
        // Path: Value -> ConstantInt : LLVMValueRef
        val value: Value = ConstantInt(IntType(32), 1)
        val int = cast<ConstantInt>(value)

        assertEquals(1, int.getSignedValue())
    }

    test("invalid paths will fail") {
        // Use has a constructor from LLVMValueRef, but is not a valid path
        // Path: Value -> Use : LLVMValueRef
        val value: Value = ConstantInt(IntType(32), 1)

        assertFailsWith<IllegalArgumentException> {
            cast<Use>(value)
        }
    }
})

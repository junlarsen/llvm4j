package io.vexelabs.bitbuilder.rtti

import io.vexelabs.bitbuilder.llvm.ir.Value
import io.vexelabs.bitbuilder.llvm.ir.types.IntType
import io.vexelabs.bitbuilder.llvm.ir.values.constants.ConstantInt
import org.spekframework.spek2.Spek
import kotlin.test.assertEquals

internal object CastingTest : Spek({
    test("casting between valid paths") {
        // Path: Value -> ConstantInt : LLVMValueRef
        val value: Value = ConstantInt(IntType(32), 1)
        val int = cast<ConstantInt>(value)

        assertEquals(1, int.getSignedValue())
    }
})
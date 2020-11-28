package io.vexelabs.bitbuilder.llvm.unit.ir.values

import io.vexelabs.bitbuilder.llvm.ir.Context
import io.vexelabs.bitbuilder.llvm.ir.types.IntType
import io.vexelabs.bitbuilder.llvm.ir.types.PointerType
import io.vexelabs.bitbuilder.llvm.ir.values.constants.ConstantInt
import io.vexelabs.bitbuilder.llvm.setup
import org.spekframework.spek2.Spek
import kotlin.test.assertEquals

internal class ConstantTest : Spek({
    setup()

    val context: Context by memoized()

    // TODO: what on earth is this doing here?
    //  Move to ConstantIntTest
    test("casting a constant into a pointer") {
        val type = context.getIntType(32)
        val ptrTy = type.intoPointerType()
        val value = ConstantInt(type, 1L, true)

        val ptr = value.getIntToPtr(ptrTy)
        val res = ptr.getPointerCast(type)

        assertEquals(1L, ConstantInt(res.ref).getSignedValue())
    }
})

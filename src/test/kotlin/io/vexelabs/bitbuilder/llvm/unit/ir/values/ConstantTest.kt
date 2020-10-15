package io.vexelabs.bitbuilder.llvm.unit.ir.values

import io.vexelabs.bitbuilder.llvm.ir.types.IntType
import io.vexelabs.bitbuilder.llvm.ir.types.PointerType
import io.vexelabs.bitbuilder.llvm.ir.values.constants.ConstantInt
import org.spekframework.spek2.Spek
import kotlin.test.assertEquals

internal class ConstantTest : Spek({
    // TODO: what on earth is this doing here?
    //  Move to ConstantIntTest
    test("casting a constant into a pointer") {
        val type = IntType(32)
        val ptrTy = PointerType(type)
        val value = ConstantInt(type, 1L, true)

        val ptr = value.getIntToPtr(ptrTy.toPointerType())
        val res = ptr.getPointerCast(type)

        assertEquals(1L, ConstantInt(res.ref).getSignedValue())
    }
})

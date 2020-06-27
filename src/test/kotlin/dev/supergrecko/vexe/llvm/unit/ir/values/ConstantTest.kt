package dev.supergrecko.vexe.llvm.unit.ir.values

import dev.supergrecko.vexe.llvm.ir.types.IntType
import dev.supergrecko.vexe.llvm.ir.types.PointerType
import dev.supergrecko.vexe.llvm.ir.values.constants.ConstantInt
import dev.supergrecko.vexe.test.TestSuite
import kotlin.test.assertEquals

internal class ConstantTest : TestSuite({
    describe("Casting a constant into a pointer") {
        val type = IntType(32)
        val ptrTy = PointerType(type)
        val value = ConstantInt(type, 1L, true)

        val ptr = value.getIntToPtr(ptrTy.toPointerType())
        val res = ptr.getPointerCast(type)

        assertEquals(1L, ConstantInt(res.ref).getSignedValue())
    }
}
)

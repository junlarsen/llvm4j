package dev.supergrecko.vexe.llvm.unit.ir.values

import dev.supergrecko.vexe.llvm.ir.types.IntType
import dev.supergrecko.vexe.llvm.ir.types.PointerType
import dev.supergrecko.vexe.llvm.ir.values.constants.ConstantInt
import dev.supergrecko.vexe.llvm.utils.TestSuite
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test

internal class ConstantTest : TestSuite() {
    @Test
    fun `Casting a constant into a pointer`() {
        val type = IntType(32)
        val ptrTy = PointerType(type)
        val value = ConstantInt(type, 1L, true)

        val ptr = value.ptrcast(ptrTy.toPointerType())
        val res = ptr.cast(type)

        assertEquals(1L, res.asIntValue().getSignedValue())
    }
}

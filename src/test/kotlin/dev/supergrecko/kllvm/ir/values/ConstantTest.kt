package dev.supergrecko.kllvm.ir.values

import dev.supergrecko.kllvm.ir.types.IntType
import dev.supergrecko.kllvm.ir.types.PointerType
import dev.supergrecko.kllvm.ir.values.constants.ConstantInt
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test

class ConstantTest {
    @Test
    fun `casting to pointer`() {
        val type = IntType(32)
        val ptrTy = PointerType(type)
        val value = ConstantInt(type, 1L, true)

        val ptr = value.ptrcast(ptrTy.toPointerType())
        val res = ptr.cast(type)

        assertEquals(1L, res.asIntValue().getSignedValue())
    }
}

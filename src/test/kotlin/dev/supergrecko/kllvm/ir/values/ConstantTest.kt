package dev.supergrecko.kllvm.ir.values

import dev.supergrecko.kllvm.ir.types.IntType
import dev.supergrecko.kllvm.ir.types.PointerType
import dev.supergrecko.kllvm.ir.values.constants.ConstantInt
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ConstantTest {
    @Test
    fun `casting to pointer`() {
        val type = IntType(32)
        val ptrTy = PointerType(type)
        val value = ConstantInt(type, 1L, true)

        /* TODO: Fix jvm crash
        val ptr = value.toConstPointer(ptrTy)

        println()

        val int = ptr.intcast(IntType(32))

        assertEquals(1L, int.getSignedValue())
         */
    }
}
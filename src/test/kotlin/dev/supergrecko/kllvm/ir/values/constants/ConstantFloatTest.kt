package dev.supergrecko.kllvm.ir.values.constants

import dev.supergrecko.kllvm.ir.TypeKind
import dev.supergrecko.kllvm.ir.types.FloatType
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test

class ConstantFloatTest {
    @Test
    fun `Creation via LLVM reference`() {
        val float = ConstantFloat(FloatType(TypeKind.Float), 1.0)
        val borrow = ConstantFloat(float.ref)

        assertEquals(float.ref, borrow.ref)
    }
}

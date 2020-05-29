package dev.supergrecko.kllvm.unit.ir.values.constants

import dev.supergrecko.kllvm.unit.ir.TypeKind
import dev.supergrecko.kllvm.unit.ir.types.FloatType
import dev.supergrecko.kllvm.utils.KLLVMTestCase
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test

internal class ConstantFloatTest : KLLVMTestCase() {
    @Test
    fun `Creation via LLVM reference`() {
        val float = ConstantFloat(FloatType(TypeKind.Float), 1.0)
        val borrow = ConstantFloat(float.ref)

        assertEquals(float.ref, borrow.ref)
    }
}

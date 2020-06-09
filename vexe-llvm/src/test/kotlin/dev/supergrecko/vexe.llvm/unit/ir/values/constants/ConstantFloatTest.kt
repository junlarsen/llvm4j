package dev.supergrecko.vexe.llvm.unit.ir.values.constants

import dev.supergrecko.vexe.llvm.ir.TypeKind
import dev.supergrecko.vexe.llvm.ir.types.FloatType
import dev.supergrecko.vexe.llvm.ir.values.constants.ConstantFloat
import dev.supergrecko.vexe.llvm.utils.KLLVMTestCase
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

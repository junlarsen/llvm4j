package dev.supergrecko.vexe.llvm.unit.ir.types

import dev.supergrecko.vexe.llvm.ir.TypeKind
import dev.supergrecko.vexe.llvm.ir.types.FloatType
import dev.supergrecko.vexe.llvm.ir.types.IntType
import dev.supergrecko.vexe.llvm.utils.VexeLLVMTestCase
import dev.supergrecko.vexe.llvm.utils.runAll
import java.lang.IllegalArgumentException
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import org.junit.jupiter.api.Test

internal class FloatTypeTest : VexeLLVMTestCase() {
    @Test
    fun `Creation from user-land constructor`() {
        runAll(*FloatType.kinds.toTypedArray()) { it, _ ->
            val type = FloatType(it)

            assertEquals(it, type.getTypeKind())
        }
    }

    @Test
    fun `Creation via LLVM reference`() {
        val ref = FloatType(TypeKind.Float)
        val second = FloatType(ref.ref)

        assertEquals(TypeKind.Float, second.getTypeKind())
    }

    @Test
    fun `Attempting to use reference constructor with wrong type fails`() {
        val ref = IntType(32)

        assertFailsWith<IllegalArgumentException> {
            FloatType(ref.ref)
        }
    }
}

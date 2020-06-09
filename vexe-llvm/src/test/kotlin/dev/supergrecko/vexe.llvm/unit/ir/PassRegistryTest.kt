package dev.supergrecko.vexe.llvm.unit.ir

import dev.supergrecko.vexe.llvm.ir.PassRegistry
import dev.supergrecko.vexe.llvm.utils.VexeLLVMTestCase
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test

internal class PassRegistryTest : VexeLLVMTestCase() {
    @Test
    fun `Pass Registry acts as a singleton`() {
        val p1 = PassRegistry()
        val p2 = PassRegistry()

        assertEquals(p1.ref, p2.ref)
    }
}

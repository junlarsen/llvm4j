package dev.supergrecko.kllvm.unit.ir

import dev.supergrecko.kllvm.utils.KLLVMTestCase
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test

internal class PassRegistryTest : KLLVMTestCase() {
    @Test
    fun `Pass Registry acts as a singleton`() {
        val p1 = PassRegistry()
        val p2 = PassRegistry()

        assertEquals(p1.ref, p2.ref)
    }
}

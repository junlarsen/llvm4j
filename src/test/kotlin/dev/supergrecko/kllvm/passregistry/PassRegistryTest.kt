package dev.supergrecko.kllvm.passregistry

import dev.supergrecko.kllvm.llvm.typedefs.PassRegistry
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class PassRegistryTest {
    @Test
    fun `assert references match`() {
        val p1 = PassRegistry()
        val p2 = PassRegistry()

        assertEquals(p1.ref, p2.ref)
    }
}

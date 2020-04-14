package dev.supergrecko.kllvm.passregistry

import dev.supergrecko.kllvm.llvm.typedefs.PassRegistry
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test

class PassRegistryTest {
    @Test
    fun `assert references match`() {
        val p1 = PassRegistry()
        val p2 = PassRegistry()

        assertEquals(p1.ref, p2.ref)
    }
}

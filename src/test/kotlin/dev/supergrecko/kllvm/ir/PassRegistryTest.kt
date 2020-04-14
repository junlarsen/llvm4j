package dev.supergrecko.kllvm.ir

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

package dev.supergrecko.kllvm.ir

import kotlin.test.assertEquals

import org.junit.jupiter.api.Test

class PassRegistryTest {
    @Test
    fun `pass registry references work as a singleton`() {
        val p1 = PassRegistry()
        val p2 = PassRegistry()

        assertEquals(p1.ref, p2.ref)
    }
}

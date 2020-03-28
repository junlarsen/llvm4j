package dev.supergrecko.kllvm.core.modules

import dev.supergrecko.kllvm.core.typedefs.Module
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ModuleTest {
    @Test
    fun `test that module identifiers match`() {
        val mod = Module.create("test.ll")
        mod.setModuleIdentifier("test")

        assertEquals("test", mod.getModuleIdentifier())
    }

    @Test
    fun `test that module cloning works`() {
        val mod = Module.create("test.ll")
        mod.setModuleIdentifier("test")

        val clone = mod.clone()

        assertEquals(mod.getModuleIdentifier(), clone.getModuleIdentifier())
    }

    @Test
    fun `test that modifying source name works`() {
        val mod = Module.create("test.ll")

        assertEquals("test.ll", mod.getSourceFileName())

        mod.setSourceFileName("test2.ll")

        assertEquals("test2.ll", mod.getSourceFileName())
    }
}
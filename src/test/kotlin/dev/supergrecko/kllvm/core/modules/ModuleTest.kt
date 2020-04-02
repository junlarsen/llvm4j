package dev.supergrecko.kllvm.core.modules

import dev.supergrecko.kllvm.core.typedefs.Context
import dev.supergrecko.kllvm.core.typedefs.Module
import dev.supergrecko.kllvm.core.types.FunctionType
import dev.supergrecko.kllvm.core.types.VoidType
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ModuleTest {
    @Test
    fun `test that module identifiers match`() {
        val mod = Module("test.ll")
        mod.setModuleIdentifier("test")

        assertEquals("test", mod.getModuleIdentifier())
    }

    @Test
    fun `test that module cloning works`() {
        val mod = Module("test.ll")
        mod.setModuleIdentifier("test")

        val clone = mod.clone()

        assertEquals(mod.getModuleIdentifier(), clone.getModuleIdentifier())
    }

    @Test
    fun `test that modifying source name works`() {
        val mod = Module("test.ll")

        assertEquals("test.ll", mod.getSourceFileName())

        mod.setSourceFileName("test2.ll")

        assertEquals("test2.ll", mod.getSourceFileName())
    }

    @Test
    fun `test getFunction returns null when no function added`() {
        val module = Module("test.ll")
        assertNull(module.getFunction("test"))
    }

    @Test
    fun `test getFunction returns function object when function added`() {
        val module = Module("test.ll")
        module.addFunction("test", FunctionType(VoidType(), listOf(), false))
        assertNotNull(module.getFunction("test"))
    }

    @Test
    fun `writing to file works`() {
        val file = File("./out.bc")
        val module = Module("test.ll")

        module.toFile(file)

        assertTrue {
            file.exists()
        }

        file.delete()
        module.dispose()
    }

    @Test
    fun `writing to buffer works`() {
        val context = Context()
        val module = Module("test.ll", context)

        val buf = module.toMemoryBuffer()

        val mod = buf.parse(context)

        assertEquals("test.ll", mod.getSourceFileName())
    }
}
package dev.supergrecko.kllvm.core.modules

import dev.supergrecko.kllvm.ir.types.FunctionType
import dev.supergrecko.kllvm.ir.types.VoidType
import dev.supergrecko.kllvm.llvm.typedefs.Context
import dev.supergrecko.kllvm.llvm.typedefs.Module
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test

class ModuleTest {
    @Test
    fun `test that module identifiers match`() {
        val mod = Module("test.ll")
        mod.setModuleIdentifier("test")

        assertEquals("test", mod.getModuleIdentifier())

        mod.dispose()
    }

    @Test
    fun `test that module cloning works`() {
        val mod = Module("test.ll")
        mod.setModuleIdentifier("test")

        val clone = mod.clone()

        assertEquals(mod.getModuleIdentifier(), clone.getModuleIdentifier())

        mod.dispose()
    }

    @Test
    fun `test that modifying source name works`() {
        val mod = Module("test.ll")

        assertEquals("test.ll", mod.getSourceFileName())

        mod.setSourceFileName("test2.ll")

        assertEquals("test2.ll", mod.getSourceFileName())

        mod.dispose()
    }

    @Test
    fun `test getFunction returns null when no function added`() {
        val module = Module("test.ll")
        assertNull(module.getFunction("test"))

        module.dispose()
    }

    @Test
    fun `test getFunction returns function object when function added`() {
        val module = Module("test.ll")
        module.addFunction("test",
            FunctionType(
                VoidType(),
                listOf(),
                false
            )
        )
        assertNotNull(module.getFunction("test"))

        module.dispose()
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

        module.dispose()
        context.dispose()
    }

    @Test
    fun `getting module from buffer works`() {
        val context = Context()
        val module = Module("test.ll", context)

        val buf = module.toMemoryBuffer()

        val mod = buf.getModule(context)

        assertEquals("test.ll", mod.getSourceFileName())
    }
}

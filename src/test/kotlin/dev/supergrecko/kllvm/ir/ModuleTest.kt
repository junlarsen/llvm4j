package dev.supergrecko.kllvm.ir

import dev.supergrecko.kllvm.ir.types.FunctionType
import dev.supergrecko.kllvm.ir.types.IntType
import dev.supergrecko.kllvm.ir.types.VoidType
import dev.supergrecko.kllvm.support.VerifierFailureAction
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test

class ModuleTest {
    @Test
    fun `Modules can have identifiers`() {
        val mod = Module("test.ll").apply {
            setModuleIdentifier("test")
        }

        assertEquals("test", mod.getModuleIdentifier())

        mod.dispose()
    }

    @Test
    fun `Cloning a module clones the identifier`() {
        val mod = Module("test.ll").apply {
            setModuleIdentifier("test")
        }

        val clone = mod.clone()

        assertEquals(mod.getModuleIdentifier(), clone.getModuleIdentifier())

        mod.dispose()
        clone.dispose()
    }

    @Test
    fun `Modifying the module's source file name`() {
        val mod = Module("test.ll")

        assertEquals("test.ll", mod.getSourceFileName())

        mod.setSourceFileName("test2.ll")

        assertEquals("test2.ll", mod.getSourceFileName())

        mod.dispose()
    }

    @Test
    fun `Fetching a function which does not exist returns null`() {
        val module = Module("test.ll")

        assertNull(module.getFunction("test"))

        module.dispose()
    }

    @Test
    fun `Fetching an existing function returns said function`() {
        val module = Module("test.ll")

        module.addFunction(
            "test",
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
    fun `Write the module byte-code to file`() {
        val file = File("./out.bc")

        val module = Module("test.ll")

        module.toFile(file)

        assertTrue { file.exists() }

        file.delete()
        module.dispose()
    }

    @Test
    fun `Write the module byte-code to file path`() {
        val file = File("./out.bc")
        val module = Module("test.ll")

        module.toFile(file.absolutePath)

        assertTrue { file.exists() }

        file.delete()
        module.dispose()
    }

    @Test
    fun `Writing to MemoryBuffer`() {
        val context = Context()
        val module = Module("test.ll", context)

        val buf = module.toMemoryBuffer()

        assertEquals('B', buf.getStart())

        module.dispose()
        context.dispose()
    }

    @Test
    fun `Get module from MemoryBuffer`() {
        val context = Context()
        val module = Module("test.ll", context)

        val buf = module.toMemoryBuffer()

        val mod = buf.getModule(context)

        assertEquals("test.ll", mod.getSourceFileName())
    }

    @Test
    fun `Verification of a valid module`() {
        val context = Context()
        val module = Module("test.ll", context)

        val res = module.verify(VerifierFailureAction.ReturnStatus)

        assertEquals(true, res)

        module.dispose()
    }

    @Test
    fun `Creation of function inside module`() {
        val fnTy = FunctionType(IntType(32), listOf(), false)
        val module = Module("test.ll")

        val fn = module.addFunction("test", fnTy)

        assertEquals(0, fn.getParameterCount())
    }
}

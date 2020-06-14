package dev.supergrecko.vexe.llvm.unit.support

import dev.supergrecko.vexe.llvm.ir.Context
import dev.supergrecko.vexe.llvm.ir.Module
import dev.supergrecko.vexe.llvm.support.MemoryBuffer
import dev.supergrecko.vexe.llvm.utils.TestSuite
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import org.junit.jupiter.api.Test

internal class MemoryBufferTest : TestSuite() {
    @Test
    fun `The buffer byte-code starts with B for BC`() {
        val context = Context()
        val module = Module("utils.ll", context)

        val buf = module.toMemoryBuffer()

        // module bit code starts with "BC"
        assertEquals('B', buf.getStart())

        cleanup(buf, module, context)
    }

    @Test
    fun `Create MemoryBuffer from byte-code file`() {
        val file = getTemporaryFile("out.ll")
        val mod = Module("utils.ll")

        mod.writeBitCodeToFile(file)

        val buf = MemoryBuffer(file)

        assertNotNull(buf)
        cleanup(mod, buf)
    }

    @Test
    fun `Creation from unknown path fails`() {
        assertFailsWith<IllegalArgumentException> {
            val mod = MemoryBuffer(File("unknown file which does not exist"))

            cleanup(mod)
        }
    }
}

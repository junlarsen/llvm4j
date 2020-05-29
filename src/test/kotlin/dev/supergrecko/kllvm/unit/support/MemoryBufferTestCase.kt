package dev.supergrecko.kllvm.unit.support

import dev.supergrecko.kllvm.unit.ir.Context
import dev.supergrecko.kllvm.unit.ir.Module
import dev.supergrecko.kllvm.utils.KLLVMTestCase
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import org.junit.jupiter.api.Test

internal class MemoryBufferTestCase : KLLVMTestCase() {
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
        val target = File("utils.ll.2")
        val mod = Module("utils.ll")

        mod.writeBitCodeToFile(target)

        val buf = MemoryBuffer(target)

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

package dev.supergrecko.kllvm.support

import dev.supergrecko.kllvm.ir.Context
import dev.supergrecko.kllvm.ir.Module
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class MemoryBufferTest {
    @Test
    fun `getting the buffer size`() {
        val context = Context()
        val module = Module("test.ll", context)

        val buf = module.toMemoryBuffer()

        // module bytecode starts with "BC"
        assertEquals('B', buf.getStart())

        buf.dispose()
        module.dispose()
        context.dispose()
    }

    @Test
    fun `creation from file`() {
        val target = File("test.ll.2")
        val mod = Module("test.ll")
        mod.toFile(target)

        val buf = MemoryBuffer(target)
        assertNotNull(buf)

        buf.dispose()
        mod.dispose()
    }

    @Test
    fun `will fail if file does not exist`() {
        assertFailsWith<IllegalArgumentException> {
            MemoryBuffer(File("unknown file which does not exist"))
        }
    }
}
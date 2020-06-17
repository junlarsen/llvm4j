package dev.supergrecko.vexe.llvm.unit.support

import dev.supergrecko.vexe.llvm.ir.Context
import dev.supergrecko.vexe.llvm.ir.Module
import dev.supergrecko.vexe.llvm.support.MemoryBuffer
import dev.supergrecko.vexe.llvm.utils.cleanup
import dev.supergrecko.vexe.test.TestSuite
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

internal class MemoryBufferTest : TestSuite({
    describe("Memory Buffers") {
        val context = Context()
        val module = Module("Test", context)

        describe("A buffer starts with BC") {
            val buf = module.toMemoryBuffer()

            assertEquals('B', buf.getStart())
        }

        describe("Creating a buffer") {
            val file = getTemporaryFile("out.ll")
            module.writeBitCodeToFile(file)
            val buf = MemoryBuffer(file)

            assertNotNull(buf)

            cleanup(buf)
        }

        cleanup(module, context)
    }

    describe("Creating from invalid paths") {
        assertFailsWith<IllegalArgumentException> {
            val mod = MemoryBuffer(File("this path does not exist"))

            cleanup(mod)
        }
    }
})

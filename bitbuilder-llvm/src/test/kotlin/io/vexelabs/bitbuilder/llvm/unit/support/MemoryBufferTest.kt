package io.vexelabs.bitbuilder.llvm.unit.support

import io.vexelabs.bitbuilder.llvm.TestUtils
import io.vexelabs.bitbuilder.llvm.ir.Module
import io.vexelabs.bitbuilder.llvm.setup
import io.vexelabs.bitbuilder.llvm.support.MemoryBuffer
import org.spekframework.spek2.Spek
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

internal object MemoryBufferTest : Spek({
    setup()

    val module: Module by memoized()
    val utils: TestUtils by memoized()

    test("the buffer starts with BC") {
        val buffer = module.toMemoryBuffer()
        val ptr = buffer.getStart()
        val start = "${ptr.get(0).toChar()}${ptr.get(1).toChar()}"

        assertEquals("BC", start)
    }

    group("storing a memory buffer to file system") {
        test("stores to an existing file") {
            val file = utils.getTemporaryFile()

            module.writeBitCodeToFile(file)
            val buffer = MemoryBuffer(file)

            assertFalse { buffer.ref.isNull }
        }

        test("does not care if the file does not exist") {
            val file = utils.getTemporaryFile()

            file.delete()

            module.writeBitCodeToFile(file)
        }
    }

        test("fails when the path does not exist") {
            assertFailsWith<IllegalArgumentException> {
                MemoryBuffer(File("this file does not exist"))
            }
        }

    group("finding buffer size") {
        test("a buffer with contents has a size") {
            val buff = module.toMemoryBuffer()

            assertTrue { buff.getSize() > 0 }
        }

        test("a buffer from empty file has no size") {
            val file = utils.getTemporaryFile().apply {
                createNewFile()
            }
            val buff = MemoryBuffer(file)

            assertEquals(0, buff.getSize())
        }
    }

    group("parsing into modules") {
        group("retrieving a bit code module") {
            test("with lazy parsing") {
                val buf = module.toMemoryBuffer()
                val subject = buf.getBitCodeModule(parseLazy = true)

                assertNotNull(subject)
                assertEquals("test", subject.getSourceFileName())
            }

            test("with regular parsing") {
                val buf = module.toMemoryBuffer()
                val subject = buf.getBitCodeModule(parseLazy = false)

                assertNotNull(subject)
                assertEquals("test", subject.getSourceFileName())
            }
        }

        test("retrieving an ir module") {
            val fh = utils.getTemporaryFile().apply {
                createNewFile()
            }.also(module.getIR()::writeToFile)
            val buf = MemoryBuffer(fh)
            val subject = buf.getIRModule()

            assertNotNull(subject)
            assertEquals("test", subject.getSourceFileName())
        }
    }
})

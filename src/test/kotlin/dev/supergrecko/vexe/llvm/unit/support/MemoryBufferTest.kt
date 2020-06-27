package dev.supergrecko.vexe.llvm.unit.support

import dev.supergrecko.vexe.llvm.TestUtils
import dev.supergrecko.vexe.llvm.ir.Module
import dev.supergrecko.vexe.llvm.setup
import dev.supergrecko.vexe.llvm.support.MemoryBuffer
import org.spekframework.spek2.Spek
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal object MemoryBufferTest1 : Spek({
    setup()

    val module: Module by memoized()
    val utils: TestUtils by memoized()

    group("using a memory buffer") {
        test("getting the first character") {
            val buffer = module.toMemoryBuffer()
            assertEquals('B', buffer.getStart())
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

        test("creation from file paths fails when the path does not exist") {
            assertFailsWith<IllegalArgumentException> {
                MemoryBuffer(File("this file does not exist"))
            }
        }
    }
})
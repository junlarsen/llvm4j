package io.vexelabs.bitbuilder.llvm.unit.ir

import io.vexelabs.bitbuilder.llvm.TestUtils
import io.vexelabs.bitbuilder.llvm.ir.Context
import io.vexelabs.bitbuilder.llvm.ir.types.IntType
import io.vexelabs.bitbuilder.llvm.setup
import org.spekframework.spek2.Spek
import kotlin.test.assertEquals

internal object IRTest : Spek({
    setup()

    val utils: TestUtils by memoized()
    val context: Context by memoized()

    test("retrieving intermediate representation") {
        val i32 = context.getIntType(32)

        assertEquals("i32", i32.getIR().toString())
    }

    test("storing ir into file") {
        val i128 = context.getIntType(128)
        val file = utils.getTemporaryFile()
        val ir = i128.getIR()

        ir.writeToFile(file)

        val contents = file.readLines().joinToString("\n")

        assertEquals("i128", contents)
    }
})

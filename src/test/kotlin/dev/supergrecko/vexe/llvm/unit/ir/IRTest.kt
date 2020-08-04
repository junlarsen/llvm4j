package dev.supergrecko.vexe.llvm.unit.ir

import dev.supergrecko.vexe.llvm.TestUtils
import dev.supergrecko.vexe.llvm.ir.Context
import dev.supergrecko.vexe.llvm.ir.types.IntType
import dev.supergrecko.vexe.llvm.setup
import kotlin.test.assertEquals
import org.spekframework.spek2.Spek

internal object IRTest : Spek({
    setup()

    val utils: TestUtils by memoized()
    val context: Context by memoized()

    test("retrieving intermediate representation") {
        val ty = IntType(32, context)
        val ir = ty.getIR()

        assertEquals("i32", ir.toString())
    }

    test("equality with other IR") {
        val lhs = IntType(32).getIR()
        val rhs = IntType(32).getIR()

        assertEquals(lhs, rhs)
    }

    test("storing ir into file") {
        val file = utils.getTemporaryFile()
        val ir = IntType(128).getIR()

        ir.writeToFile(file)

        val contents = file.readLines().joinToString("\n")

        assertEquals("i128", contents)
    }
})

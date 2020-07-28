package dev.supergrecko.vexe.llvm.unit.ir.instructions.traits

import dev.supergrecko.vexe.llvm.ir.Builder
import dev.supergrecko.vexe.llvm.setup
import org.junit.jupiter.api.assertThrows
import org.spekframework.spek2.Spek

internal object TerminatorTest : Spek({
    setup()

    val builder: Builder by memoized()

    group("successors of terminating basic blocks") {
        test("a loose terminator does not have a successor") {
            val inst = builder.build().createRetVoid()

            assertThrows<IllegalArgumentException> {
                inst.getSuccessor(0)
            }
        }
    }
})
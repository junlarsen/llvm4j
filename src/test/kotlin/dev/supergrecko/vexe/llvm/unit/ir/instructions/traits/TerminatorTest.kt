package dev.supergrecko.vexe.llvm.unit.ir.instructions.traits

import dev.supergrecko.vexe.llvm.ir.Builder
import dev.supergrecko.vexe.llvm.setup
import org.spekframework.spek2.Spek
import kotlin.test.assertFailsWith

internal object TerminatorTest : Spek({
    setup()

    val builder: Builder by memoized()

    group("successors of terminating basic blocks") {
        test("a loose terminator does not have a successor") {
            val inst = builder.build().createRetVoid()

            assertFailsWith<IllegalArgumentException> {
                inst.getSuccessor(0)
            }
        }
    }
})
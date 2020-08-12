package io.vexelabs.bitbuilder.llvm.unit.ir.instructions.traits

import io.vexelabs.bitbuilder.llvm.ir.Builder
import io.vexelabs.bitbuilder.llvm.setup
import kotlin.test.assertFailsWith
import org.spekframework.spek2.Spek

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

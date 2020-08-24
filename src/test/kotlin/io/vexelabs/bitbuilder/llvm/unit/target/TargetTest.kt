package io.vexelabs.bitbuilder.llvm.unit.target

import io.vexelabs.bitbuilder.llvm.ir.Module
import io.vexelabs.bitbuilder.llvm.setup
import io.vexelabs.bitbuilder.llvm.target.Target
import io.vexelabs.bitbuilder.llvm.target.TargetMachine
import org.spekframework.spek2.Spek
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

internal object TargetTest : Spek({
    setup()

    val module: Module by memoized()

    test("there are registered targets") {
        val machine = module.createExecutionEngine().getTargetMachine()
        val iterator = machine?.getFirstTarget()
        val targets = mutableListOf<Target>()

        assertNotNull(iterator)

        for (i in iterator) {
            targets += i
        }

        assertTrue { targets.isNotEmpty() }
    }

    group("creating targets from name and triples") {
        test("invalid target triple") {
            val triple = "does_not_exist"
            val error = assertFailsWith<IllegalArgumentException> {
                Target.createFromTriple(triple)
            }

            val expected = "No available targets are compatible with triple " +
                    "\"$triple\""

            assertEquals(expected, error.message)
        }

        test("invalid target name") {
            assertFailsWith<IllegalArgumentException> {
                Target.createFromName("this_also_fails")
            }
        }

        // TODO: Test valid target triples once JavaCPP ships binaries with
        //  non-host targets
        //  (see https://github.com/bytedeco/javacpp-presets/issues/932)
    }
})
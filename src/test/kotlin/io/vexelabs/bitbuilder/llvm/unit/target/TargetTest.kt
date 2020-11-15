package io.vexelabs.bitbuilder.llvm.unit.target

import io.vexelabs.bitbuilder.llvm.ir.Module
import io.vexelabs.bitbuilder.llvm.setup
import io.vexelabs.bitbuilder.llvm.target.Target
import org.bytedeco.llvm.global.LLVM
import org.spekframework.spek2.Spek
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

internal object TargetTest : Spek({
    setup()

    val module: Module by memoized()

    test("there are registered targets") {
        val machine = module.createExecutionEngine().getTargetMachine()
        val iterator = machine?.getTargetIterator()
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

            assertTrue {
                error.message
                    ?.contains("No available targets are compatible with")
                    ?: false
            }
        }

        test("invalid target name") {
            assertFailsWith<IllegalArgumentException> {
                Target.createFromName("this_also_fails")
            }
        }

        test("all targets which llvm builds are available") {
            LLVM.LLVMInitializeAllTargets()
            LLVM.LLVMInitializeAllTargetInfos()

            // TODO: Expand this list
            val triples = listOf(
                "x86_64-apple-macos",
                "ppc64le-windows-pc",
                "arm-unknown-unknown"
            )

            for (triple in triples) {
                Target.createFromTriple(triple)
            }
        }
    }
})

package dev.supergrecko.vexe.llvm.unit.ir

import dev.supergrecko.vexe.llvm.ir.PassRegistry
import dev.supergrecko.vexe.test.TestSuite
import org.spekframework.spek2.Spek
import kotlin.test.assertEquals

internal object PassRegistryTest : Spek({
    test("the pass registry is a singleton") {
        val p1 = PassRegistry()
        val p2 = PassRegistry()

        assertEquals(p1.ref, p2.ref)
    }
})

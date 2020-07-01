package dev.supergrecko.vexe.llvm.unit.ir

import dev.supergrecko.vexe.llvm.ir.Context
import dev.supergrecko.vexe.llvm.setup
import org.bytedeco.javacpp.IntPointer
import org.spekframework.spek2.Spek
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal object ContextTest : Spek({
    setup()

    val context: Context by memoized()

    test("setting a diagnostic handler with a payload") {
        val payload = IntPointer(100)
        context.setDiagnosticHandler(payload) { }

        val ptr = context.getDiagnosticContext()

        assertEquals(payload, ptr)
    }

    group("setting a yield callback does not unexpectedly fail") {
        test("without a payload") {
            context.setYieldCallback { }
        }

        test("with a payload") {
            val payload = IntPointer(99)

            context.setYieldCallback(payload) { }
        }
    }

    test("discarding value names") {
        for (i in listOf(true, false)) {
            context.setDiscardValueNames(i)
            assertEquals(i, context.isDiscardingValueNames())
        }
    }

    test("global context is a singleton") {
        val a = Context.getGlobalContext()
        val b = Context.getGlobalContext()

        assertEquals(a.ref, b.ref)
    }

    test("retrieving metadata ids") {
        // 4 is the id for "range"
        val id = context.getMetadataKindId("range")

        assertEquals(4, id)
    }
})

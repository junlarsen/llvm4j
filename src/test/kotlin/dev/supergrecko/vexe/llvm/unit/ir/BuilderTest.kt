package dev.supergrecko.vexe.llvm.unit.ir

import dev.supergrecko.vexe.llvm.ir.Builder
import dev.supergrecko.vexe.llvm.ir.Module
import dev.supergrecko.vexe.llvm.ir.types.FunctionType
import dev.supergrecko.vexe.llvm.ir.types.VoidType
import dev.supergrecko.vexe.llvm.setup
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import org.spekframework.spek2.Spek

internal class BuilderTest : Spek({
    setup()

    val module: Module by memoized()
    val builder: Builder by memoized()

    group("positioning the builder") {
        test("may position after a basic block") {
            val fn = module.createFunction(
                "test", FunctionType(
                    VoidType(),
                    listOf(),
                    false
                )
            )
            val bb = fn.createBlock("entry")

            assertNull(builder.getInsertionBlock())

            builder.setPositionAtEnd(bb)

            assertEquals(builder.getInsertionBlock()?.ref, bb.ref)
        }

        test("the builder hand may be cleared") {
            val fn = module.createFunction(
                "test", FunctionType(
                    VoidType(),
                    listOf(),
                    false
                )
            )
            val bb = fn.createBlock("entry")

            builder.setPositionAtEnd(bb)

            assertNotNull(builder.getInsertionBlock())

            builder.clear()

            assertNull(builder.getInsertionBlock())
        }
    }
})

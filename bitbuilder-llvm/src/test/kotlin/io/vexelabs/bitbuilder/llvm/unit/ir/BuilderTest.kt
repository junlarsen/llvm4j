package io.vexelabs.bitbuilder.llvm.unit.ir

import io.vexelabs.bitbuilder.llvm.ir.Builder
import io.vexelabs.bitbuilder.llvm.ir.Module
import io.vexelabs.bitbuilder.llvm.ir.types.FunctionType
import io.vexelabs.bitbuilder.llvm.ir.types.VoidType
import io.vexelabs.bitbuilder.llvm.setup
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

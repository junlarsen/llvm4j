package io.vexelabs.bitbuilder.llvm.unit.ir.types

import io.vexelabs.bitbuilder.llvm.ir.Context
import io.vexelabs.bitbuilder.llvm.ir.types.FloatType
import io.vexelabs.bitbuilder.llvm.setup
import io.vexelabs.bitbuilder.llvm.utils.runAll
import org.spekframework.spek2.Spek
import kotlin.test.assertEquals

internal class FloatTypeTest : Spek({
    setup()

    val context: Context by memoized()

    test("creation of each float type") {
        runAll(*FloatType.kinds.toTypedArray()) { it, _ ->
            val type = context.getFloatType(it)

            assertEquals(it, type.getTypeKind())
        }
    }
})

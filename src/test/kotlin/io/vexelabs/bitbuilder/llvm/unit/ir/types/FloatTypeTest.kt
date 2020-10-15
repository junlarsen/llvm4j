package io.vexelabs.bitbuilder.llvm.unit.ir.types

import io.vexelabs.bitbuilder.llvm.ir.types.FloatType
import io.vexelabs.bitbuilder.llvm.utils.runAll
import org.spekframework.spek2.Spek
import kotlin.test.assertEquals

internal class FloatTypeTest : Spek({
    test("creation of each float type") {
        runAll(*FloatType.kinds.toTypedArray()) { it, _ ->
            val type = FloatType(it)

            assertEquals(it, type.getTypeKind())
        }
    }
})

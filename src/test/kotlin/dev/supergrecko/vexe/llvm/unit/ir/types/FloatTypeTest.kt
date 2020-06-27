package dev.supergrecko.vexe.llvm.unit.ir.types

import dev.supergrecko.vexe.llvm.ir.TypeKind
import dev.supergrecko.vexe.llvm.ir.types.FloatType
import dev.supergrecko.vexe.llvm.utils.runAll
import dev.supergrecko.vexe.test.TestSuite
import kotlin.test.assertEquals

internal class FloatTypeTest : TestSuite({
    describe("Creation from user-land constructor") {
        runAll(*FloatType.kinds.toTypedArray()) { it, _ ->
            val type = FloatType(it)

            assertEquals(it, type.getTypeKind())
        }
    }

    describe("Creation via LLVM reference") {
        val ref = FloatType(TypeKind.Float)
        val second = FloatType(ref.ref)

        assertEquals(TypeKind.Float, second.getTypeKind())
    }
})

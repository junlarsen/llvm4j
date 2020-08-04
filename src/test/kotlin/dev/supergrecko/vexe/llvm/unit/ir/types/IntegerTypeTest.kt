package dev.supergrecko.vexe.llvm.unit.ir.types

import dev.supergrecko.vexe.llvm.ir.Context
import dev.supergrecko.vexe.llvm.ir.TypeKind
import dev.supergrecko.vexe.llvm.ir.types.IntType
import dev.supergrecko.vexe.llvm.utils.runAll
import org.spekframework.spek2.Spek
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class IntegerTypeTest : Spek({
    test("creationg of an arbitrary int type") {
        val size = (0..8388606).random()
        val type = IntType(size)

        assertEquals(TypeKind.Integer, type.getTypeKind())
    }

    test("the width is consistent across contexts") {
        val ctx = Context()

        runAll(1, 8, 16, 32, 64, 128) { it, _ ->
            val contextType = IntType(it, ctx)
            val globalType = IntType(it)

            assertEquals(contextType.getTypeWidth(), globalType.getTypeWidth())
        }
    }

    test("the type width matches") {
        val ctx = Context()

        runAll(1, 8, 16, 32, 64, 128) { it, _ ->
            val type = IntType(it, ctx)

            assertEquals(it, type.getTypeWidth())
        }
    }

    test("the integer bit size may not be negative") {
        assertFailsWith<IllegalArgumentException> {
            IntType(-1)
        }
    }

    test("the size may not exceed 8388606") {
        assertFailsWith<IllegalArgumentException> {
            IntType(1238234672)
        }
    }
})

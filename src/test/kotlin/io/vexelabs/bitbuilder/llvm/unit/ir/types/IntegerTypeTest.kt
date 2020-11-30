package io.vexelabs.bitbuilder.llvm.unit.ir.types

import io.vexelabs.bitbuilder.llvm.ir.Context
import io.vexelabs.bitbuilder.llvm.ir.TypeKind
import io.vexelabs.bitbuilder.llvm.setup
import io.vexelabs.bitbuilder.llvm.utils.runAll
import org.spekframework.spek2.Spek
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class IntegerTypeTest : Spek({
    setup()

    val context: Context by memoized()

    test("creation of an arbitrary int type") {
        for (x in 0..100) {
            val size = (0..8388606).random()
            val type = context.getIntType(size)

            assertEquals(TypeKind.Integer, type.getTypeKind())
        }
    }

    test("the width is consistent across contexts") {
        runAll(1, 8, 16, 32, 64, 128) { it, _ ->
            val contextType = context.getIntType(it)
            val globalType = Context.getGlobalContext().getIntType(it)

            assertEquals(contextType.getTypeWidth(), globalType.getTypeWidth())
        }
    }

    test("the type width matches") {
        runAll(1, 8, 16, 32, 64, 128) { it, _ ->
            val type = context.getIntType(it)

            assertEquals(it, type.getTypeWidth())
        }
    }

    test("the integer bit size may not be negative") {
        assertFailsWith<IllegalArgumentException> {
            context.getIntType(-1)
        }
    }

    test("the size may not exceed 8388606") {
        assertFailsWith<IllegalArgumentException> {
            context.getIntType(1238234672)
        }
    }
})

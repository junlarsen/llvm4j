package dev.supergrecko.kllvm.ir.types

import dev.supergrecko.kllvm.test.runAll
import dev.supergrecko.kllvm.ir.Context
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test

class IntegerTypeTest {
    @Test
    fun `global module values equate to module values`() {
        val ctx = Context()

        runAll(1, 8, 16, 32, 64, 128) { it, _ ->
            val contextType = IntType(it, ctx)
            val globalType = IntType(it)

            assertEquals(contextType.getTypeWidth(), globalType.getTypeWidth())
        }
    }

    @Test
    fun `it actually grabs types instead of null pointers`() {
        val ctx = Context()

        runAll(1, 8, 16, 32, 64, 128) { it, _ ->
            val type = IntType(it, ctx)

            assertEquals(it, type.getTypeWidth())
        }
    }
}

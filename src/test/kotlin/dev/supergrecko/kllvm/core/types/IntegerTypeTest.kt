package dev.supergrecko.kllvm.core.types

import dev.supergrecko.kllvm.core.typedefs.Context
import dev.supergrecko.kllvm.factories.TypeFactory
import dev.supergrecko.kllvm.utils.runAll
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class IntegerTypeTest {
    @Test
    fun `global module values equate to module values`() {
        val ctx = Context.create()

        runAll(1, 8, 16, 32, 64, 128) {
            val contextType = TypeFactory.integer(it, ctx)
            val globalType = TypeFactory.integer(it)

            assertEquals(contextType.getTypeWidth(), globalType.getTypeWidth())
        }
    }

    @Test
    fun `it actually grabs types instead of null pointers`() {
        val ctx = Context.create()

        runAll(1, 8, 16, 32, 64, 128) {
            val type = TypeFactory.integer(it, ctx)

            assertTrue { !type.llvmType.isNull }
        }
    }
}

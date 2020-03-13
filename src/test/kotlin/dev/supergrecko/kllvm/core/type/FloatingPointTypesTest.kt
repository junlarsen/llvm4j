package dev.supergrecko.kllvm.core.type

import dev.supergrecko.kllvm.core.Context
import dev.supergrecko.kllvm.utils.runAll
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class FloatingPointTypesTest {
    @Test
    fun `it actually grabs types instead of null pointers`() {
        val ctx = Context.create()

        runAll(*FloatingPointTypes.TypeKinds.values()) {
            val type = ctx.floatType(it)

            assertTrue { !type.isNull }
        }
    }
}
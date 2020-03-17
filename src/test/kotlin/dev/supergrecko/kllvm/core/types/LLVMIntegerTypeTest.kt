package dev.supergrecko.kllvm.core.types

import dev.supergrecko.kllvm.core.LLVMContext
import dev.supergrecko.kllvm.core.LLVMType
import dev.supergrecko.kllvm.utils.runAll
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LLVMIntegerTypeTest {
    @Test
    fun `global module values equate to module values`() {
        val ctx = LLVMContext.create()

        runAll(1, 8, 16, 32, 64, 128) {
            val contextType = ctx.createIntegerType(it)
            val globalType = LLVMType.createInteger(it)

            assertEquals(contextType.getTypeWidth(), globalType.getTypeWidth())
        }
    }

    @Test
    fun `it actually grabs types instead of null pointers`() {
        val ctx = LLVMContext.create()

        runAll(1, 8, 16, 32, 64, 128) {
            val type = ctx.createIntegerType(it)

            assertTrue { !type.llvmType.isNull }
        }
    }
}
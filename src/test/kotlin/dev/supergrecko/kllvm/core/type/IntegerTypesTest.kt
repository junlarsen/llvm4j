package dev.supergrecko.kllvm.core.type

import dev.supergrecko.kllvm.core.Context
import dev.supergrecko.kllvm.utils.runAll
import org.bytedeco.llvm.global.LLVM
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class IntegerTypesTest {
    @Test
    fun `global module values equate to module values`() {
        val ctx = Context.create()

        runAll(1, 8, 16, 32, 64, 128) {
            val contextType = ctx.intType(it)
            val globalType = IntegerTypes.type(it)

            assertEquals(LLVM.LLVMGetIntTypeWidth(contextType), LLVM.LLVMGetIntTypeWidth(globalType))
        }
    }

    @Test
    fun `it actually grabs types instead of null pointers`() {
        val ctx = Context.create()

        runAll(*IntegerTypes.TypeKinds.values()) {
            val type = IntegerTypes.type(ctx.llvmCtx, it, 100)

            assertTrue { !type.isNull }
        }
    }
}
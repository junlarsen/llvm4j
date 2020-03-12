package dev.supergrecko.kllvm.core.type

import dev.supergrecko.kllvm.core.Context
import dev.supergrecko.kllvm.utils.runAll
import org.bytedeco.llvm.global.LLVM
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class IntegerTypeTest {
    @Test
    fun `global module values equate to module values`() {
        val ctx = Context.create()

        runAll(1, 8, 16, 32, 64, 128) {
            val contextType = ctx.iType(it)
            val globalType = IntegerType.iType(it)

            assertEquals(LLVM.LLVMGetIntTypeWidth(contextType), LLVM.LLVMGetIntTypeWidth(globalType))
        }
    }
}
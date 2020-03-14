package dev.supergrecko.kllvm.core.type

import dev.supergrecko.kllvm.core.LLVMContext
import dev.supergrecko.kllvm.utils.runAll
import org.bytedeco.llvm.global.LLVM
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LLVMIntegerTypeTest {
    @Test
    fun `global module values equate to module values`() {
        val ctx = LLVMContext.create()

        runAll(1, 8, 16, 32, 64, 128) {
            val contextType = ctx.integerType(LLVMType.IntegerTypeKinds.LLVM_INT_TYPE, it)
            val globalType = LLVMType.makeInteger(LLVMType.IntegerTypeKinds.LLVM_INT_TYPE, it)

            assertEquals(contextType.typeWidth(), globalType.typeWidth())
        }
    }

    @Test
    fun `it actually grabs types instead of null pointers`() {
        val ctx = LLVMContext.create()

        runAll(*LLVMType.IntegerTypeKinds.values()) {
            val type = LLVMType.makeInteger(it, 1024, ctx.llvmCtx)

            assertTrue { !type.llvmType.isNull }
        }
    }
}
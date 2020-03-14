package dev.supergrecko.kllvm.core.type

import dev.supergrecko.kllvm.core.LLVMContext
import dev.supergrecko.kllvm.utils.runAll
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class LLVMFloatTypeTest {
    @Test
    fun `it actually grabs types instead of null pointers`() {
        val ctx = LLVMContext.create()

        runAll(*LLVMTypeKind.Float.values()) {
            val type = ctx.floatType(it)

            assertTrue { !type.llvmType.isNull }
        }
    }
}

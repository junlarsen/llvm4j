package dev.supergrecko.kllvm.ir.values

import dev.supergrecko.kllvm.internal.util.runAll
import dev.supergrecko.kllvm.ir.Module
import dev.supergrecko.kllvm.ir.ThreadLocalMode
import dev.supergrecko.kllvm.ir.types.IntType
import dev.supergrecko.kllvm.ir.values.constants.ConstantInt
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import org.junit.jupiter.api.Test

class GlobalVariableTest {
    @Test
    fun `creating a global value`() {
        val ty = IntType(32)
        val value = Module("test.ll").addGlobal(ty, "v")

        val v = ConstantInt(ty, 100L, true)
        value.initializer = v

        assertFalse { value.globalConstant }
        assertFalse { value.threadLocal }

        assertEquals(100L, value.initializer.asIntValue().getSignedValue())
        assertEquals("v", value.valueName)
        assertEquals(ThreadLocalMode.NotThreadLocal, value.threadLocalMode)
    }

    @Test
    fun `thread localization works as expected`() {
        val ty = IntType(32)
        val value = Module("test.ll").addGlobal(ty, "v")

        value.threadLocal = true

        // While this may seem redundant it is not, see impl for the getter
        runAll(*ThreadLocalMode.values()) {
            value.threadLocalMode = it

            assertEquals(it, value.threadLocalMode)
        }
    }
}

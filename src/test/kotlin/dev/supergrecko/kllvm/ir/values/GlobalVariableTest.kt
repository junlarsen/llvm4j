package dev.supergrecko.kllvm.ir.values

import dev.supergrecko.kllvm.test.runAll
import dev.supergrecko.kllvm.ir.Module
import dev.supergrecko.kllvm.ir.ThreadLocalMode
import dev.supergrecko.kllvm.ir.types.IntType
import dev.supergrecko.kllvm.ir.values.constants.ConstantInt
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class GlobalVariableTest {
    @Test
    fun `creating a global value`() {
        val ty = IntType(32)
        val value = Module("test.ll").addGlobal("v", ty)

        val v = ConstantInt(ty, 100L, true)
        value.initializer = v

        assertFalse { value.globalConstant }
        assertFalse { value.threadLocal }
        assertFalse { value.externallyInitialized }

        assertEquals(100L, value.initializer.asIntValue().getSignedValue())
        assertEquals("v", value.valueName)
        assertEquals(ThreadLocalMode.NotThreadLocal, value.threadLocalMode)
    }

    @Test
    fun `set global constant`() {
        val ty = IntType(32)
        val value = Module("test.ll").addGlobal("v", ty)

        value.initializer = ConstantInt(IntType(32), 100L, true)
        value.globalConstant = true

        assertTrue { value.globalConstant }
    }

    @Test
    fun `adding a value in an address space`() {
        val module = Module("test.ll")
        val v = module.addGlobal("v", IntType(32), 0x03f7d)

        assertFailsWith<RuntimeException> {
            v.initializer
        }
    }

    @Test
    fun `thread localization properties works as expected`() {
        val ty = IntType(32)
        val value = Module("test.ll").addGlobal("v", ty)

        value.threadLocal = true

        // While this may seem redundant it is not, see impl for the getter
        runAll(*ThreadLocalMode.values()) { it, _ ->
            value.threadLocalMode = it

            assertEquals(it, value.threadLocalMode)
        }
    }
}

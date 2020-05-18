package dev.supergrecko.kllvm.ir.values

import dev.supergrecko.kllvm.ir.Module
import dev.supergrecko.kllvm.ir.ThreadLocalMode
import dev.supergrecko.kllvm.ir.types.IntType
import dev.supergrecko.kllvm.ir.values.constants.ConstantInt
import dev.supergrecko.kllvm.test.runAll
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test

class GlobalVariableTest {
    @Test
    fun `creating a global value`() {
        val ty = IntType(32)
        val v = ConstantInt(ty, 100L, true)

        val mod = Module("test.ll")
        val value = mod.addGlobal("v", ty).apply {
            setInitializer(v)
        }

        with(value) {
            assertFalse { isGlobalConstant() }
            assertFalse { isThreadLocal() }
            assertFalse { isExternallyInitialized() }

            val initializer = value.getInitializer()
                ?.asIntValue()
                ?.getSignedValue()

            assertEquals(100L, initializer)
            assertEquals("v", getName())
            assertEquals(ThreadLocalMode.NotThreadLocal, getThreadLocalMode())
        }

        mod.dispose()
    }

    @Test
    fun `set global constant`() {
        val ty = IntType(32)
        val v = ConstantInt(IntType(32), 100L, true)
        val mod = Module("test.ll")

        val value = mod.addGlobal("v", ty).apply {
            setInitializer(v)
            setGlobalConstant(true)
        }

        assertTrue { value.isGlobalConstant() }

        mod.dispose()
    }

    @Test
    fun `adding a value in an address space`() {
        val module = Module("test.ll")
        val v = module.addGlobal("v", IntType(32), 0x03f7d)

        assertNull(v.getInitializer())

        module.dispose()
    }

    @Test
    fun `thread localization properties works as expected`() {
        val ty = IntType(32)
        val mod = Module("test.ll")
        val value = mod.addGlobal("v", ty).apply {
            setThreadLocal(true)
        }

        // While this may seem redundant it is not, see impl for the getter
        runAll(*ThreadLocalMode.values()) { it, _ ->
            value.setThreadLocalMode(it)

            assertEquals(it, value.getThreadLocalMode())
        }

        mod.dispose()
    }
}

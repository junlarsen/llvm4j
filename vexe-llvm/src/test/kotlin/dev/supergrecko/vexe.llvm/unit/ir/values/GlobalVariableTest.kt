package dev.supergrecko.vexe.llvm.unit.ir.values

import dev.supergrecko.vexe.llvm.ir.Module
import dev.supergrecko.vexe.llvm.ir.ThreadLocalMode
import dev.supergrecko.vexe.llvm.ir.types.IntType
import dev.supergrecko.vexe.llvm.ir.values.constants.ConstantInt
import dev.supergrecko.vexe.llvm.utils.VexeLLVMTestCase
import dev.supergrecko.vexe.llvm.utils.runAll
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test

internal class GlobalVariableTest : VexeLLVMTestCase() {
    @Test
    fun `Creation of global variable`() {
        val ty = IntType(32)
        val v = ConstantInt(ty, 100L, true)

        val mod = Module("utils.ll")
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

        cleanup(mod)
    }

    @Test
    fun `Turning a global constant`() {
        val ty = IntType(32)
        val v = ConstantInt(IntType(32), 100L, true)
        val mod = Module("utils.ll")

        val value = mod.addGlobal("v", ty).apply {
            setInitializer(v)
            setGlobalConstant(true)
        }

        assertTrue { value.isGlobalConstant() }

        cleanup(mod)
    }

    @Test
    fun `Assigning a global to an address space`() {
        val module = Module("utils.ll")
        val v = module.addGlobal("v", IntType(32), 0x03f7d)

        assertNull(v.getInitializer())

        cleanup(module)
    }

    @Test
    fun `Mutating thread localization`() {
        val ty = IntType(32)
        val mod = Module("utils.ll")
        val value = mod.addGlobal("v", ty).apply {
            setThreadLocal(true)
        }

        // While this may seem redundant it is not, see impl for the getter
        runAll(*ThreadLocalMode.values()) { it, _ ->
            value.setThreadLocalMode(it)

            assertEquals(it, value.getThreadLocalMode())
        }

        cleanup(mod)
    }
}

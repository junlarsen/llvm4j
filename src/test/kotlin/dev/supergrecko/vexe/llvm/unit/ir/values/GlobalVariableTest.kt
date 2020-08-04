package dev.supergrecko.vexe.llvm.unit.ir.values

import dev.supergrecko.vexe.llvm.ir.Module
import dev.supergrecko.vexe.llvm.ir.ThreadLocalMode
import dev.supergrecko.vexe.llvm.ir.types.IntType
import dev.supergrecko.vexe.llvm.ir.values.constants.ConstantInt
import dev.supergrecko.vexe.llvm.setup
import dev.supergrecko.vexe.llvm.utils.runAll
import org.spekframework.spek2.Spek
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class GlobalVariableTest : Spek({
    setup()

    val module: Module by memoized()

    test("create a global variable") {
        val ty = IntType(32)
        val v = ConstantInt(ty, 100L, true)
        val value = module.addGlobal("v", ty).apply {
            setInitializer(v)
        }

        with(value) {
            assertFalse { isGlobalConstant() }
            assertFalse { isThreadLocal() }
            assertFalse { isExternallyInitialized() }

            val initializer = ConstantInt(value.getInitializer()!!.ref)
                .getSignedValue()

            assertEquals(100L, initializer)
            assertEquals("v", getName())
            assertEquals(ThreadLocalMode.NotThreadLocal, getThreadLocalMode())
        }
    }

    // TODO: test with non-global value
    test("flagging a global as constant") {
        val ty = IntType(32)
        val v = ConstantInt(IntType(32), 100L, true)
        val mod = Module("utils.ll")

        val value = mod.addGlobal("v", ty).apply {
            setInitializer(v)
            setGlobalConstant(true)
        }

        assertTrue { value.isGlobalConstant() }
    }

    test("assigning a global to an address space") {
        val v = module.addGlobal("v", IntType(32), 0x03f7d)

        assertNull(v.getInitializer())
    }

    test("flagging a global as thread local") {
        val ty = IntType(32)
        val value = module.addGlobal("v", ty).apply {
            setThreadLocal(true)
        }

        runAll(*ThreadLocalMode.values()) { it, _ ->
            value.setThreadLocalMode(it)

            assertEquals(it, value.getThreadLocalMode())
        }

    }
})

package io.vexelabs.bitbuilder.llvm.unit.ir.values

import io.vexelabs.bitbuilder.internal.cast
import io.vexelabs.bitbuilder.llvm.ir.Context
import io.vexelabs.bitbuilder.llvm.ir.Module
import io.vexelabs.bitbuilder.llvm.ir.ThreadLocalMode
import io.vexelabs.bitbuilder.llvm.ir.types.IntType
import io.vexelabs.bitbuilder.llvm.ir.values.constants.ConstantInt
import io.vexelabs.bitbuilder.llvm.setup
import io.vexelabs.bitbuilder.llvm.utils.runAll
import org.spekframework.spek2.Spek
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class GlobalVariableTest : Spek({
    setup()

    val module: Module by memoized()
    val context: Context by memoized()

    test("create a global variable") {
        val ty = context.getIntType(32)
        val v = ConstantInt(ty, 100L, true)
        val value = module.addGlobal("v", ty).apply {
            setInitializer(v)
        }

        with(value) {
            assertFalse { isGlobalConstant() }
            assertFalse { isThreadLocal() }
            assertFalse { isExternallyInitialized() }

            val initializer = cast<ConstantInt>(value.getInitializer()!!)
                .getSignedValue()

            assertEquals(100L, initializer)
            assertEquals("v", getName())
            assertEquals(ThreadLocalMode.NotThreadLocal, getThreadLocalMode())
        }
    }

    // TODO: test with non-global value
    test("flagging a global as constant") {
        val ty = context.getIntType(32)
        val v = ConstantInt(ty, 100L, true)
        val mod = context.createModule("utils.ll")

        val value = mod.addGlobal("v", ty).apply {
            setInitializer(v)
            setGlobalConstant(true)
        }

        assertTrue { value.isGlobalConstant() }
    }

    test("assigning a global to an address space") {
        val i32 = context.getIntType(32)
        val v = module.addGlobal("v", i32, 0x03f7d)

        assertNull(v.getInitializer())
    }

    test("flagging a global as thread local") {
        val ty = context.getIntType(32)
        val value = module.addGlobal("v", ty).apply {
            setThreadLocal(true)
        }

        runAll(*ThreadLocalMode.values()) { it, _ ->
            value.setThreadLocalMode(it)

            assertEquals(it, value.getThreadLocalMode())
        }
    }
})

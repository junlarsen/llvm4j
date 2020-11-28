package io.vexelabs.bitbuilder.llvm.unit.ir

import io.vexelabs.bitbuilder.llvm.ir.Context
import io.vexelabs.bitbuilder.llvm.ir.Module
import io.vexelabs.bitbuilder.llvm.ir.ThreadLocalMode
import io.vexelabs.bitbuilder.llvm.ir.types.IntType
import io.vexelabs.bitbuilder.llvm.ir.values.constants.ConstantInt
import io.vexelabs.bitbuilder.llvm.setup
import org.spekframework.spek2.Spek
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal object GlobalVariableTest : Spek({
    setup()

    val module: Module by memoized()
    val context: Context by memoized()

    group("the initializer values for a global") {
        test("an uninitialized value returns null") {
            val i32 = context.getIntType(32)
            val global = module.addGlobal("global", i32)

            assertNull(global.getInitializer())
        }

        test("an initialized value returns") {
            val i32 = context.getIntType(32)
            val initializer = ConstantInt(i32, 8)
            val global = module.addGlobal("global", i32).apply {
                setInitializer(initializer)
            }
            val subject = global.getInitializer()

            assertNotNull(subject)
            assertEquals(initializer.ref, subject.ref)
        }
    }

    group("toggleable properties for global values") {
        test("is externally initialized") {
            val i32 = context.getIntType(32)

            module.addGlobal("global", i32).apply {
                assertFalse { isExternallyInitialized() }

                setExternallyInitialized(true)

                assertTrue { isExternallyInitialized() }
            }
        }

        test("is global constant") {
            val i32 = context.getIntType(32)

            module.addGlobal("global", i32).apply {
                assertFalse { isGlobalConstant() }

                setGlobalConstant(true)

                assertTrue { isGlobalConstant() }
            }
        }

        test("thread locality") {
            val i32 = context.getIntType(32)

            val global = module.addGlobal("global", i32).apply {
                assertEquals(
                    ThreadLocalMode.NotThreadLocal,
                    getThreadLocalMode()
                )
                assertFalse { isThreadLocal() }
            }

            for (i in ThreadLocalMode.values()) {
                global.setThreadLocalMode(i)

                assertEquals(i, global.getThreadLocalMode())
            }
        }
    }
})

package dev.supergrecko.vexe.llvm.unit.ir

import dev.supergrecko.vexe.llvm.ir.Module
import dev.supergrecko.vexe.llvm.ir.ThreadLocalMode
import dev.supergrecko.vexe.llvm.ir.types.IntType
import dev.supergrecko.vexe.llvm.ir.values.constants.ConstantInt
import dev.supergrecko.vexe.llvm.setup
import org.spekframework.spek2.Spek
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal object GlobalVariableTest : Spek({
    setup()

    val module: Module by memoized()

    group("the initializer values for a global") {
        test("an uninitialized value returns null") {
            val global = module.addGlobal("global", IntType(32))

            assertNull(global.getInitializer())
        }

        test("an initialized value returns") {
            val initializer = ConstantInt(IntType(32), 8)
            val global = module.addGlobal("global", IntType(32)).apply {
                setInitializer(initializer)
            }
            val subject = global.getInitializer()

            assertNotNull(subject)
            assertEquals(initializer.ref, subject.ref)
        }
    }

    group("toggleable properties for global values") {
        test("is externally initialized") {
            module.addGlobal("global", IntType(32)).apply {
                assertFalse { isExternallyInitialized() }

                setExternallyInitialized(true)

                assertTrue { isExternallyInitialized() }
            }
        }

        test("is global constant") {
            module.addGlobal("global", IntType(32)).apply {
                assertTrue { isGlobalConstant() }

                setGlobalConstant(false)

                assertFalse { isGlobalConstant() }
            }
        }

        test("thread locality") {
            val global = module.addGlobal("global", IntType(32)).apply {
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
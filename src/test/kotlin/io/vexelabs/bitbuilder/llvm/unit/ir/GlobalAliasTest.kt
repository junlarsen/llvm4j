package io.vexelabs.bitbuilder.llvm.unit.ir

import io.vexelabs.bitbuilder.llvm.ir.Context
import io.vexelabs.bitbuilder.llvm.ir.Module
import io.vexelabs.bitbuilder.llvm.setup
import org.spekframework.spek2.Spek
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

internal object GlobalAliasTest : Spek({
    setup()

    val module: Module by memoized()
    val context: Context by memoized()

    group("assigning and retrieving aliases") {
        test("a global which does not exist returns null") {
            val doesntExist = module.getAlias("nothing")

            assertNull(doesntExist)
        }

        test("a global which exist returns") {
            val i32 = context.getIntType(32)
            val global = module.addGlobal("item", i32)
            val alias = module.addAlias(
                i32.getPointerType(),
                global,
                "alias"
            )

            val subject = module.getAlias("alias")

            assertNotNull(subject)
            assertEquals(alias.ref, subject.ref)
        }
    }

    test("reassigning the aliasee") {
        val i32 = context.getIntType(32)
        val global = module.addGlobal("item", i32)
        val alias = module.addAlias(
            i32.getPointerType(),
            global,
            "alias"
        )

        assertEquals(global.ref, alias.getAliasOf().ref)

        val i1 = context.getIntType(1)
        val alternative = module.addGlobal("alt", i1)

        alias.setAliasOf(alternative)

        assertEquals(alternative.ref, alias.getAliasOf().ref)
    }
})

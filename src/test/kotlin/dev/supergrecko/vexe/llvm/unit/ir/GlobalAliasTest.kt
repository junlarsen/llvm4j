package dev.supergrecko.vexe.llvm.unit.ir

import dev.supergrecko.vexe.llvm.ir.Module
import dev.supergrecko.vexe.llvm.ir.types.IntType
import dev.supergrecko.vexe.llvm.ir.types.PointerType
import dev.supergrecko.vexe.llvm.setup
import org.spekframework.spek2.Spek
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

internal object GlobalAliasTest : Spek({
    setup()

    val module: Module by memoized()

    group("assigning and retrieving aliases") {
        test("a global which does not exist returns null") {
            val doesntExist = module.getAlias("nothing")

            assertNull(doesntExist)
        }

        test("a global which exist returns") {
            val global = module.addGlobal("item", IntType(32))
            val alias = module.addAlias(
                PointerType(IntType(32)),
                global,
                "alias"
            )

            val subject = module.getAlias("alias")

            assertNotNull(subject)
            assertEquals(alias.ref, subject.ref)
        }
    }

    test("reassigning the aliasee") {
        val global = module.addGlobal("item", IntType(32))
        val alias = module.addAlias(
            PointerType(IntType(32)),
            global,
            "alias"
        )

        assertEquals(global.ref, alias.getAliasOf().ref)

        val alternative = module.addGlobal("alt", IntType(1))

        alias.setAliasOf(alternative)

        assertEquals(alternative.ref, alias.getAliasOf().ref)
    }
})
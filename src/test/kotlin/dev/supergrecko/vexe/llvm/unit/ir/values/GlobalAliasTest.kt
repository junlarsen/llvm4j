package dev.supergrecko.vexe.llvm.unit.ir.values

import dev.supergrecko.vexe.llvm.ir.Module
import dev.supergrecko.vexe.llvm.ir.types.IntType
import dev.supergrecko.vexe.llvm.ir.values.constants.ConstantInt
import dev.supergrecko.vexe.llvm.setup
import org.spekframework.spek2.Spek
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class GlobalAliasTest : Spek({
    setup()

    val module: Module by memoized()

    test("a module alias copies any globals from the original module") {
        val ty = IntType(32)
        val v = ConstantInt(ty, 32L, true)

        val global = module.addGlobal("value_1", ty).apply {
            setInitializer(v)
        }

        val alias = module.addAlias(ty.toPointerType(), global, "value_2")
        val aliasValue = alias.getAliasOf()

        assertEquals(
            ConstantInt(aliasValue.ref).getSignedValue(),
            ConstantInt(global.ref).getSignedValue()
        )
    }

    test("aliases which are not found return null") {
        val alias = module.getAlias("unknown_alias")

        assertNull(alias)
    }

    test("pulling the alias") {
        val ty = IntType(32).toPointerType()
        val global = module.addGlobal("value_1", ty)
        val alias = module.addAlias(ty, global, "alias_1")
        val aliasOf = module.getAlias("alias_1")

        assertEquals(alias.ref, aliasOf?.ref)
    }
})

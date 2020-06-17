package dev.supergrecko.vexe.llvm.unit.ir.values

import dev.supergrecko.vexe.llvm.ir.Module
import dev.supergrecko.vexe.llvm.ir.types.IntType
import dev.supergrecko.vexe.llvm.ir.values.constants.ConstantInt
import dev.supergrecko.vexe.llvm.utils.cleanup
import dev.supergrecko.vexe.test.TestSuite
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class GlobalAliasTest : TestSuite({
    describe("A module alias copies any globals") {
        val mod = Module("utils.ll")
        val ty = IntType(32)
        val v = ConstantInt(ty, 32L, true)

        val global = mod.addGlobal("value_1", ty).apply {
            setInitializer(v)
        }

        val alias = mod.addAlias(ty.toPointerType(), global, "value_2")
        val aliasValue = alias.getAliasOf()

        assertEquals(
            ConstantInt(aliasValue.ref).getSignedValue(),
            ConstantInt(global.ref).getSignedValue()
        )

        cleanup(mod)
    }

    describe("A non-existing alias returns null") {
        val mod = Module("utils.ll")
        val alias = mod.getAlias("unknown_alias")

        assertNull(alias)

        cleanup(mod)
    }

    describe("Retrieving aliases works as expected") {
        val mod = Module("utils.ll")
        val ty = IntType(32).toPointerType()
        val global = mod.addGlobal("value_1", ty)
        val alias = mod.addAlias(ty, global, "alias_1")
        val aliasOf = mod.getAlias("alias_1")

        assertEquals(alias.ref, aliasOf?.ref)

        cleanup(mod)
    }
})

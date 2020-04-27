package dev.supergrecko.kllvm.ir.values

import dev.supergrecko.kllvm.ir.Module
import dev.supergrecko.kllvm.ir.types.IntType
import dev.supergrecko.kllvm.ir.values.constants.ConstantInt
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GlobalAliasTest {
    @Test
    fun `aliases track the value`() {
        val mod = Module("test.ll")

        val ty = IntType(32)
        val v = ConstantInt(ty, 32L, true)
        val global = mod.addGlobal("value_1", ty)
        global.initializer = v

        val alias = mod.addAlias(ty.toPointerType(), global, "value_2")

        val aliasValue = alias.aliasOf

        assertEquals(
            aliasValue.asIntValue().getSignedValue(),
            global.asIntValue().getSignedValue()
        )
    }

    @Test
    fun `non-existing alias returns null`() {
        val mod = Module("test.ll")

        val alias = mod.getAlias("unknown_alias")

        assertNull(alias)
    }

    @Test
    fun `fetching alias from module`() {
        val mod = Module("test.ll")
        val ty = IntType(32).toPointerType()
        val global = mod.addGlobal("value_1", ty)
        val alias = mod.addAlias(ty, global, "alias_1")
        val aliasOf = mod.getAlias("alias_1")

        assertEquals(alias.ref, aliasOf?.ref)
    }
}
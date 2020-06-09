package dev.supergrecko.vexe.llvm.unit.ir.values

import dev.supergrecko.vexe.llvm.ir.Module
import dev.supergrecko.vexe.llvm.ir.types.IntType
import dev.supergrecko.vexe.llvm.ir.values.constants.ConstantInt
import dev.supergrecko.vexe.llvm.utils.VexeLLVMTestCase
import kotlin.test.assertEquals
import kotlin.test.assertNull
import org.junit.jupiter.api.Test

internal class GlobalAliasTest : VexeLLVMTestCase() {
    @Test
    fun `A module alias copies any globals`() {
        val mod = Module("utils.ll")
        val ty = IntType(32)
        val v = ConstantInt(ty, 32L, true)

        val global = mod.addGlobal("value_1", ty).apply {
            setInitializer(v)
        }

        val alias = mod.addAlias(ty.toPointerType(), global, "value_2")
        val aliasValue = alias.getAliasOf()

        assertEquals(
            aliasValue.asIntValue().getSignedValue(),
            global.asIntValue().getSignedValue()
        )

        cleanup(mod)
    }

    @Test
    fun `A non-existing alias returns null`() {
        val mod = Module("utils.ll")
        val alias = mod.getAlias("unknown_alias")

        assertNull(alias)

        cleanup(mod)
    }

    @Test
    fun `Retrieving aliases works as expected`() {
        val mod = Module("utils.ll")
        val ty = IntType(32).toPointerType()
        val global = mod.addGlobal("value_1", ty)
        val alias = mod.addAlias(ty, global, "alias_1")
        val aliasOf = mod.getAlias("alias_1")

        assertEquals(alias.ref, aliasOf?.ref)

        cleanup(mod)
    }
}

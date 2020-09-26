package io.vexelabs.bitbuilder.llvm.unit.ir.values

import io.vexelabs.bitbuilder.llvm.ir.Module
import io.vexelabs.bitbuilder.llvm.ir.types.IntType
import io.vexelabs.bitbuilder.llvm.ir.values.constants.ConstantInt
import io.vexelabs.bitbuilder.llvm.setup
import io.vexelabs.bitbuilder.rtti.cast
import kotlin.test.assertEquals
import kotlin.test.assertNull
import org.spekframework.spek2.Spek

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
            cast<ConstantInt>(aliasValue).getSignedValue(),
            cast<ConstantInt>(global).getSignedValue()
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

package dev.supergrecko.vexe.llvm.unit.ir.values

import dev.supergrecko.vexe.llvm.ir.Module
import dev.supergrecko.vexe.llvm.ir.types.IntType
import dev.supergrecko.vexe.llvm.ir.values.GlobalValue
import dev.supergrecko.vexe.llvm.setup
import org.spekframework.spek2.Spek
import kotlin.test.assertEquals

internal class GlobalValueTest : Spek({
    setup()

    val module: Module by memoized()

    test("pulling the module from a global value") {
        module.setModuleIdentifier("basic")

        val global = module.addGlobal("my_int", IntType(32))
        val globalModule = GlobalValue(global.ref).getModule()

        assertEquals(
            module.getModuleIdentifier(), globalModule.getModuleIdentifier()
        )
    }
})

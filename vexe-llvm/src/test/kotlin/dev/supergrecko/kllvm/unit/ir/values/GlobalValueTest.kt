package dev.supergrecko.kllvm.unit.ir.values

import dev.supergrecko.kllvm.ir.Module
import dev.supergrecko.kllvm.ir.types.IntType
import dev.supergrecko.kllvm.utils.KLLVMTestCase
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test

internal class GlobalValueTest : KLLVMTestCase() {
    @Test
    fun `Fetching the module from a global works`() {
        val module = Module("utils.ll").apply {
            setModuleIdentifier("basic")
        }

        val global = module.addGlobal("my_int", IntType(32))
        val globalModule = global.asGlobalValue().getModule()

        assertEquals(
            module.getModuleIdentifier(), globalModule.getModuleIdentifier()
        )

        cleanup(module)
    }
}

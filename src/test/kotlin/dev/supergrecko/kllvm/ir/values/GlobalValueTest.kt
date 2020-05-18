package dev.supergrecko.kllvm.ir.values

import dev.supergrecko.kllvm.ir.Module
import dev.supergrecko.kllvm.ir.types.IntType
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test

class GlobalValueTest {
    @Test
    fun `Fetching the module from a global works`() {
        val module = Module("test.ll").apply {
            setModuleIdentifier("basic")
        }

        val global = module.addGlobal("my_int", IntType(32))
        val globalModule = global.asGlobalValue().getModule()

        assertEquals(
            module.getModuleIdentifier(), globalModule.getModuleIdentifier()
        )
    }
}

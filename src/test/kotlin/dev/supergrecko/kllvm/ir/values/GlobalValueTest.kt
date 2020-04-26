package dev.supergrecko.kllvm.ir.values

import dev.supergrecko.kllvm.ir.Module
import dev.supergrecko.kllvm.ir.types.IntType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GlobalValueTest {
    @Test
    fun `fetching module`() {
        val module = Module("test.ll")
        module.setModuleIdentifier("basic")

        val global = module.addGlobal(IntType(32), "my_int")
        val globalModule = global.asGlobalValue().getModule()

        assertEquals(
            module.getModuleIdentifier(), globalModule.getModuleIdentifier()
        )
    }
}
package io.vexelabs.bitbuilder.llvm.unit.ir

import io.vexelabs.bitbuilder.llvm.ir.Comdat
import io.vexelabs.bitbuilder.llvm.ir.Module
import io.vexelabs.bitbuilder.llvm.ir.types.IntType
import io.vexelabs.bitbuilder.llvm.setup
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import org.spekframework.spek2.Spek

internal object ComdatTest : Spek({
    setup()

    val module: Module by memoized()

    test("creating a comdat and setting its selection kind") {
        val comdat = module.getOrCreateComdat("hello")
        val value = module.addGlobal("test", IntType(32))

        assertNull(value.getComdat())

        value.setComdat(comdat)

        assertNotNull(value.getComdat())

        for (i in Comdat.SelectionKind.values()) {
            comdat.setSelectionKind(i)
            assertEquals(i, comdat.getSelectionKind())
        }
    }
})

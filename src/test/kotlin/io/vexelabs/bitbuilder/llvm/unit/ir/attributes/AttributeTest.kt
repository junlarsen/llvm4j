package io.vexelabs.bitbuilder.llvm.unit.ir.attributes

import io.vexelabs.bitbuilder.llvm.ir.Attribute
import io.vexelabs.bitbuilder.llvm.ir.Context
import io.vexelabs.bitbuilder.llvm.setup
import org.spekframework.spek2.Spek
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal object AttributeTest : Spek({
    setup()

    val context: Context by memoized()
    val stringAttr by memoized { context.createStringAttribute("test", "value") }
    val enumAttr by memoized { context.createEnumAttribute(0, 1L) }

    test("construction from either type") {
        assertTrue { stringAttr.isStringAttribute() }
        assertTrue { enumAttr.isEnumAttribute() }

        val opaqueString = Attribute.fromRef(stringAttr.ref)
        val opaqueEnum = Attribute.fromRef(enumAttr.ref)

        assertTrue { opaqueString.isStringAttribute() }
        assertTrue { opaqueEnum.isEnumAttribute() }
    }

    test("retrieving values and kinds works") {
        assertEquals("test", stringAttr.getKind())
        assertEquals(0, enumAttr.getKind())

        assertEquals("value", stringAttr.getValue())
        assertEquals(1L, enumAttr.getValue())
    }
})

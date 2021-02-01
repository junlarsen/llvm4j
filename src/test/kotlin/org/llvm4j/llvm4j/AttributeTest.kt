package org.llvm4j.llvm4j

import org.junit.jupiter.api.Test
import org.llvm4j.llvm4j.testing.assertIsNone
import org.llvm4j.llvm4j.testing.assertIsSome
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AttributeTest {
    @Test fun `Test attributes are of their respective kinds`() {
        val ctx = Context()
        val enum = ctx.newEnumAttribute(1, 0)
        val string = ctx.newStringAttribute("key", "value")

        assertTrue { enum.isEnumAttribute() }
        assertTrue { string.isStringAttribute() }
        assertFalse { enum.isStringAttribute() }
        assertFalse { string.isEnumAttribute() }

        assertEquals(0, enum.getEnumValue().get())
        assertEquals(1, enum.getEnumKind().get())
        assertEquals("key", string.getStringKind().get())
        assertEquals("value", string.getStringValue().get())
    }

    @Test fun `Test locating enum kinds`() {
        val ctx = Context()
        val last = Attribute.getLastEnumKind()
        val subject1 = Attribute.getEnumKindByName("doesnt-exist-at-all")
        val subject2 = Attribute.getEnumKindByName("noinline")
        val subject3 = ctx.newEnumAttribute(last, 0)

        assertIsNone(subject1)
        assertIsSome(subject2)

        assertTrue { subject3.isEnumAttribute() }
        assertEquals(last, subject3.getEnumKind().get())
        assertEquals(0, subject3.getEnumValue().get())
    }
}

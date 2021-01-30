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
        val enum = ctx.createEnumAttribute(1, 0)
        val string = ctx.createStringAttribute("key", "value")

        assertTrue { enum.isEnumAttribute() }
        assertTrue { string.isStringAttribute() }
        assertFalse { enum.isStringAttribute() }
        assertFalse { string.isEnumAttribute() }

        assertEquals(0, enum.getValue())
        assertEquals(1, enum.getKind())
        assertEquals("key", string.getKind())
        assertEquals("value", string.getValue())
    }

    @Test fun `Test locating enum kinds`() {
        val ctx = Context()
        val last = Attribute.getLastEnumKind()
        val subject1 = Attribute.getEnumKindByName("doesnt-exist-at-all")
        val subject2 = Attribute.getEnumKindByName("noinline")
        val subject3 = ctx.createEnumAttribute(last, 0)

        assertIsNone(subject1)
        assertIsSome(subject2)

        assertTrue { subject3.isEnumAttribute() }
        assertEquals(last, subject3.getKind())
        assertEquals(0, subject3.getValue())
    }
}

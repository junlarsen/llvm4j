package org.llvm4j.llvm4j

import org.junit.jupiter.api.Test
import org.llvm4j.llvm4j.util.None
import org.llvm4j.llvm4j.util.Some
import kotlin.test.assertEquals

class MetadataTest {
    @Test fun `Test metadata ids are recognized by context`() {
        val ctx = Context()

        assertEquals(Some(4), ctx.getMetadataKindId("range"))
        assertEquals(None, ctx.getMetadataKindId("doesnt-exist-at-all"))
    }
}

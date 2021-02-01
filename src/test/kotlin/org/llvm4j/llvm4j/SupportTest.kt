package org.llvm4j.llvm4j

import org.junit.jupiter.api.Test
import org.llvm4j.llvm4j.testing.assertIsOk
import java.io.File
import kotlin.test.assertEquals

class SupportTest {
    @Test fun `Test usage of LLVM messages`() {
        val str = LLVMString.create("Hello World")

        assertEquals(11, str.ref.stringBytes.size)
        assertEquals(11, str.getString().length)

        val subject = str.getString()

        str.deallocate()

        // test memory copy is preserved after de-allocation of pointer

        assertEquals(11, subject.length)
        assertEquals("Hello World", subject)
    }

    @Test fun `Test creation of memory buffers`() {
        val file = File.createTempFile("test", ".bc")

        file.deleteOnExit()
        file.writeText("Hello World")

        val subject = MemoryBuffer.create(file)

        assertIsOk(subject)
        assertEquals(11, subject.get().getSize())
        assertEquals("Hello World", subject.get().getString())
        assertEquals('d', subject.get().getStartPointer().getChar(10))
    }
}

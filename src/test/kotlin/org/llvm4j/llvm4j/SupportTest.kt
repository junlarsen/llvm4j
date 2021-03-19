package org.llvm4j.llvm4j

import org.junit.jupiter.api.Test
import org.llvm4j.llvm4j.testing.assertIsNone
import org.llvm4j.llvm4j.testing.assertIsOk
import org.llvm4j.llvm4j.testing.assertIsSome
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SupportTest {
    @Test
    fun `Test usage of LLVM messages`() {
        val str = LLVMString.of("Hello World")

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

        val subject = MemoryBuffer.of(file)

        assertIsOk(subject)
        assertEquals(11, subject.unwrap().getSize())
        assertEquals("Hello World", subject.unwrap().getString())
        assertEquals('d', subject.unwrap().getStartPointer().getChar(10))
    }

    @Test fun `Test isa conversion assertions`() {
        val ctx = Context()
        val i32 = ctx.getInt32Type()
        val subject = i32.getConstant(1234)

        assertTrue { isa<Constant>(subject) }
        assertTrue { isa<ConstantInt>(subject) }
        assertFalse { isa<Instruction>(subject) }

        assertFailsWith<ClassCastException> {
            cast<Instruction>(subject)
        }

        assertIsSome(dyncast<Constant>(subject))
        assertIsNone(dyncast<Instruction>(subject))
    }
}

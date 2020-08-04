package dev.supergrecko.vexe.llvm.unit.support

import dev.supergrecko.vexe.llvm.support.Message
import org.bytedeco.javacpp.BytePointer
import org.spekframework.spek2.Spek
import java.lang.IllegalArgumentException
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

internal object MessageTest : Spek({
    test("create a message from a string") {
        val msg = Message("Hello World")

        assertTrue { msg.valid }
    }

    test("create from a byte pointer") {
        val msg = Message(BytePointer("Test"))

        assertEquals("Test", msg.getString())
    }

    test("retrieving the string value") {
        val msg = Message("Hello World")

        assertEquals("Hello World", msg.getString())
    }

    test("disposal of a message") {
        val msg = Message("Hello World")

        msg.dispose()

        assertFailsWith<IllegalArgumentException> {
            msg.dispose()
        }
    }
})
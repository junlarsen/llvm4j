package dev.supergrecko.kllvm.core.message

import dev.supergrecko.kllvm.contracts.Disposable
import org.bytedeco.llvm.global.LLVM
import java.nio.ByteBuffer

/**
 * A glorified char*
 *
 * A [Message] is a wrapper around Java's ByteBuffer and Bytedeco's BytePointers. It contains a char buffer which will
 * be decoded into a Kotlin [String]. Because these are obtained from C++ they need to be de-allocated which is why
 * they provide the [dispose] method. Failing to call this method will leak memory.
 */
public class Message(private val buffer: ByteBuffer) : Disposable, AutoCloseable {
    public override var valid: Boolean = true

    public override fun close() {
        dispose(this)
    }

    public fun getString(): String {
        require(valid)

        val res = StringBuilder()

        for (i in 0 until buffer.capacity()) {
            res.append(buffer.get(i).toChar())
        }

        return res.toString()
    }

    override fun dispose() {
        dispose(this)
    }

    public companion object {
        public fun create(buffer: ByteBuffer): Message {
            return Message(buffer)
        }

        public fun dispose(message: Message) {
            require(message.valid)
            message.valid = false
            LLVM.LLVMDisposeMessage(message.buffer)
        }
    }
}
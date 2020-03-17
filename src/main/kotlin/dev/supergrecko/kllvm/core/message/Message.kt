package dev.supergrecko.kllvm.core.message

import dev.supergrecko.kllvm.contracts.Disposable
import dev.supergrecko.kllvm.contracts.Validatable
import org.bytedeco.llvm.global.LLVM

public class Message(private val buffer: ByteArray) : Disposable, AutoCloseable {
    public override var valid: Boolean = true

    public override fun close() {
        dispose(this)
    }

    public fun getString(): String {
        require(valid)

        return buffer.toString()
    }

    override fun dispose() {
        dispose(this)
    }

    public companion object {
        public fun create(buffer: ByteArray): Message {
            return Message(buffer)
        }

        public fun dispose(message: Message) {
            require(message.valid)
            message.valid = false
            LLVM.LLVMDisposeMessage(message.buffer)
        }
    }
}
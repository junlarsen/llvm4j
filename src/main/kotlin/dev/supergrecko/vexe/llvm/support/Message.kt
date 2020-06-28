package dev.supergrecko.vexe.llvm.support

import dev.supergrecko.vexe.llvm.internal.contracts.Disposable
import org.bytedeco.javacpp.BytePointer
import java.nio.ByteBuffer
import org.bytedeco.llvm.global.LLVM

public class Message(
    private val pointer: BytePointer
) : Disposable {
    public override var valid: Boolean = true

    /**
     * Get a string representation of this [Message]
     *
     * For this method to be available the [Message] needs to be [valid].
     * If this was called on a de-allocated object, the JVM would crash.
     */
    public fun getString(): String {
        require(valid) { "Cannot use disposed memory" }

        return pointer.string
    }

    public override fun dispose() {
        require(valid) { "Cannot dispose object twice" }

        valid = false

        LLVM.LLVMDisposeMessage(pointer)
    }
}

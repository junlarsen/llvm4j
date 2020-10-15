package io.vexelabs.bitbuilder.llvm.support

import io.vexelabs.bitbuilder.llvm.internal.contracts.ContainsReference
import io.vexelabs.bitbuilder.llvm.internal.contracts.Disposable
import org.bytedeco.javacpp.BytePointer
import org.bytedeco.llvm.global.LLVM

/**
 * Class representing a byte pointer which must be de-allocated manually
 *
 * These byte pointers are retrieved via JNI. Failing to de-allocate them will
 * leak memory.
 */
public open class Message internal constructor() :
    Disposable,
    ContainsReference<BytePointer> {
    public override var valid: Boolean = true
    public final override lateinit var ref: BytePointer

    /**
     * Create a Message from a string
     *
     * @see LLVM.LLVMCreateMessage
     */
    public constructor(message: String) : this() {
        val ptr = BytePointer(message)

        ref = LLVM.LLVMCreateMessage(ptr)
    }

    /**
     * Create a message from a byte pointer
     *
     * This constructor should not be used with a byte pointer which did not
     * originally come from LLVM.
     */
    public constructor(pointer: BytePointer) : this() {
        ref = pointer
    }

    /**
     * Get a string representation of this [Message]
     *
     * For this method to be available the [Message] needs to be [valid].
     * If this was called on a de-allocated object, the JVM would crash.
     */
    public fun getString(): String {
        require(valid) { "Cannot use disposed memory" }

        return ref.string
    }

    public override fun dispose() {
        require(valid) { "Cannot dispose object twice" }

        valid = false

        LLVM.LLVMDisposeMessage(ref)
    }
}

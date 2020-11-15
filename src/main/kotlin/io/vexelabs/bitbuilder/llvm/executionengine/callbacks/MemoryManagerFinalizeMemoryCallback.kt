package io.vexelabs.bitbuilder.llvm.executionengine.callbacks

import io.vexelabs.bitbuilder.internal.map
import io.vexelabs.bitbuilder.llvm.internal.contracts.Callback
import org.bytedeco.javacpp.BytePointer
import org.bytedeco.javacpp.Pointer
import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.llvm.LLVM.LLVMMemoryManagerFinalizeMemoryCallback

/**
 * Kotlin lambda type for [LLVMMemoryManagerFinalizeMemoryCallback]
 *
 * @see LLVMMemoryManagerFinalizeMemoryCallback
 */
public typealias MemoryManagerFinalizeMemoryCallback = (
    MemoryManagerFinalizeMemoryCallbackContext
) -> Int

/**
 * Data payload for [MemoryManagerFinalizeMemoryCallback]
 *
 * @property payload Opaque pointer value assigned at callback registration
 * @property error Error message, if present
 */
public data class MemoryManagerFinalizeMemoryCallbackContext(
    public val payload: Pointer?,
    public val error: String
)

/**
 * Pointer type for [LLVMMemoryManagerFinalizeMemoryCallback]
 *
 * This is the value passed back into LLVM
 *
 * TODO: Find a reliable way of testing this (see #179)
 *
 * @see LLVMMemoryManagerFinalizeMemoryCallback
 */
public class MemoryManagerFinalizeMemoryBase(
    private val callback: MemoryManagerFinalizeMemoryCallback
) : LLVMMemoryManagerFinalizeMemoryCallback(), Callback {
    public override fun call(
        arg0: Pointer?,
        arg1: PointerPointer<*>?
    ): Int {
        val msg = (arg1 as? PointerPointer<BytePointer>)
            ?.map { it.string }
            ?.joinToString("\n")
            ?: ""

        val data = MemoryManagerFinalizeMemoryCallbackContext(
            payload = arg0,
            error = msg
        )

        return callback.invoke(data)
    }
}

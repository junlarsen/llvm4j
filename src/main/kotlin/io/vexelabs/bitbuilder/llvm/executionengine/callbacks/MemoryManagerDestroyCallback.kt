package io.vexelabs.bitbuilder.llvm.executionengine.callbacks

import io.vexelabs.bitbuilder.llvm.internal.contracts.Callback
import org.bytedeco.javacpp.Pointer
import org.bytedeco.llvm.LLVM.LLVMMemoryManagerDestroyCallback

/**
 * Kotlin lambda type for [LLVMMemoryManagerDestroyCallback]
 *
 * @see LLVMMemoryManagerDestroyCallback
 */
public typealias MemoryManagerDestroyCallback = (
    MemoryManagerDestroyCallbackContext
) -> Unit

/**
 * Data payload for [MemoryManagerDestroyCallback]
 *
 * @property payload Opaque pointer value assigned at callback registration
 */
public data class MemoryManagerDestroyCallbackContext(
    public val payload: Pointer?
)

/**
 * Pointer type for [LLVMMemoryManagerDestroyCallback]
 *
 * This is the value passed back into LLVM
 *
 * @see LLVMMemoryManagerDestroyCallback
 */
public class MemoryManagerDestroyBase(
    private val callback: MemoryManagerDestroyCallback
) : LLVMMemoryManagerDestroyCallback(), Callback {
    public override fun call(arg0: Pointer?) {
        val data = MemoryManagerDestroyCallbackContext(
            payload = arg0
        )

        callback.invoke(data)
    }
}

package io.vexelabs.bitbuilder.llvm.executionengine.callbacks

import io.vexelabs.bitbuilder.llvm.internal.contracts.Callback
import org.bytedeco.javacpp.Pointer
import org.bytedeco.llvm.LLVM.LLVMMemoryManagerDestroyCallback

public typealias MemoryManagerDestroyCallback = (
    MemoryManagerDestroyCallbackContext
) -> Unit

public data class MemoryManagerDestroyCallbackContext(
    public val payload: Pointer?
)

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

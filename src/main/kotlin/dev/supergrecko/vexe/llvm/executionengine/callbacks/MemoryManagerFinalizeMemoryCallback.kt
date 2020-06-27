package dev.supergrecko.vexe.llvm.executionengine.callbacks

import dev.supergrecko.vexe.llvm.internal.contracts.Callback
import org.bytedeco.javacpp.BytePointer
import org.bytedeco.javacpp.Pointer
import org.bytedeco.llvm.LLVM.LLVMMemoryManagerFinalizeMemoryCallback

public typealias MemoryManagerFinalizeMemoryCallback = (
    MemoryManagerFinalizeMemoryCallbackContext
) -> Int

public data class MemoryManagerFinalizeMemoryCallbackContext(
    public val payload: Pointer?,
    public val error: String
)

public class MemoryManagerFinalizeMemoryBase(
    private val callback: MemoryManagerFinalizeMemoryCallback
) : LLVMMemoryManagerFinalizeMemoryCallback(), Callback {
    public override fun call(arg0: Pointer?, arg1: BytePointer?): Int {
        val data = MemoryManagerFinalizeMemoryCallbackContext(
            payload = arg0,
            error = arg1?.string ?: ""
        )

        return callback.invoke(data)
    }
}

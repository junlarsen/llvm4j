package io.vexelabs.bitbuilder.llvm.executionengine.callbacks

import io.vexelabs.bitbuilder.llvm.internal.contracts.Callback
import org.bytedeco.javacpp.BytePointer
import org.bytedeco.javacpp.Pointer
import org.bytedeco.llvm.LLVM.LLVMMemoryManagerAllocateCodeSectionCallback

public typealias MemoryManagerAllocateCodeSectionCallback = (
    MemoryManagerAllocateCodeSectionCallbackContext
) -> BytePointer

public data class MemoryManagerAllocateCodeSectionCallbackContext(
    public val payload: Pointer?,
    public val size: Long,
    public val alignment: Int,
    public val sectionId: Int,
    public val sectionName: String
)

public class MemoryManagerAllocateCodeSectionBase(
    private val callback: MemoryManagerAllocateCodeSectionCallback
) : LLVMMemoryManagerAllocateCodeSectionCallback(), Callback {
    public override fun call(
        arg0: Pointer?,
        arg1: Long,
        arg2: Int,
        arg3: Int,
        arg4: BytePointer?
    ): BytePointer {
        val data = MemoryManagerAllocateCodeSectionCallbackContext(
            payload = arg0,
            size = arg1,
            alignment = arg2,
            sectionId = arg3,
            sectionName = arg4?.string ?: ""
        )

        return callback.invoke(data)
    }
}

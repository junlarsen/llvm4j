package dev.supergrecko.vexe.llvm.executionengine.callbacks

import dev.supergrecko.vexe.llvm.internal.contracts.Callback
import dev.supergrecko.vexe.llvm.internal.util.fromLLVMBool
import org.bytedeco.javacpp.BytePointer
import org.bytedeco.javacpp.Pointer
import org.bytedeco.llvm.LLVM.LLVMMemoryManagerAllocateDataSectionCallback

public typealias MemoryManagerAllocateDataSectionCallback = (
    MemoryManagerAllocateDataSectionCallbackContext
) -> BytePointer

public data class MemoryManagerAllocateDataSectionCallbackContext(
    public val payload: Pointer?,
    public val size: Long,
    public val alignment: Int,
    public val sectionId: Int,
    public val sectionName: String,
    public val isReadOnly: Boolean
)

public class MemoryManagerAllocateDataSectionBase(
    private val callback: MemoryManagerAllocateDataSectionCallback
) : LLVMMemoryManagerAllocateDataSectionCallback(), Callback {
    public override fun call(
        arg0: Pointer?,
        arg1: Long,
        arg2: Int,
        arg3: Int,
        arg4: BytePointer?,
        arg5: Int
    ): BytePointer {
        val data = MemoryManagerAllocateDataSectionCallbackContext(
            payload = arg0,
            size = arg1,
            alignment = arg2,
            sectionId = arg3,
            sectionName = arg4?.string ?: "",
            isReadOnly = arg5.fromLLVMBool()
        )

        return callback.invoke(data)
    }
}

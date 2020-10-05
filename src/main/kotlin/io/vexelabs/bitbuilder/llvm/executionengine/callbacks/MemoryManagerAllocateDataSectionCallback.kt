package io.vexelabs.bitbuilder.llvm.executionengine.callbacks

import io.vexelabs.bitbuilder.llvm.internal.contracts.Callback
import io.vexelabs.bitbuilder.llvm.internal.util.fromLLVMBool
import org.bytedeco.javacpp.BytePointer
import org.bytedeco.javacpp.Pointer
import org.bytedeco.llvm.LLVM.LLVMMemoryManagerAllocateDataSectionCallback

/**
 * Kotlin lambda type for [LLVMMemoryManagerAllocateDataSectionCallback]
 *
 * @see LLVMMemoryManagerAllocateDataSectionCallback
 */
public typealias MemoryManagerAllocateDataSectionCallback = (
    MemoryManagerAllocateDataSectionCallbackContext
) -> BytePointer

/**
 * Data payload for [MemoryManagerAllocateDataSectionCallback]
 *
 * @property payload Opaque pointer value assigned at callback registration
 * @property size
 * @property alignment
 * @property sectionId The section which was just allocated
 * @property sectionName The name of the section
 * @property isReadOnly
 */
public data class MemoryManagerAllocateDataSectionCallbackContext(
    public val payload: Pointer?,
    public val size: Long,
    public val alignment: Int,
    public val sectionId: Int,
    public val sectionName: String,
    public val isReadOnly: Boolean
)

/**
 * Pointer type for [LLVMMemoryManagerAllocateDataSectionCallback]
 *
 * This is the value passed back into LLVM
 *
 * @see LLVMMemoryManagerAllocateDataSectionCallback
 */
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

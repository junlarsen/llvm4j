package dev.supergrecko.vexe.llvm.executionengine.callbacks

import dev.supergrecko.vexe.llvm.internal.contracts.Callback
import dev.supergrecko.vexe.llvm.internal.util.fromLLVMBool
import org.bytedeco.javacpp.BytePointer
import org.bytedeco.javacpp.Pointer
import org.bytedeco.llvm.LLVM.LLVMMemoryManagerAllocateDataSectionCallback

public typealias MemoryManagerAllocateDataSectionCallback = (
    Pointer?, Long, Int, Int, BytePointer?, Boolean
) -> BytePointer

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
        val bool = arg5.fromLLVMBool()

        return callback.invoke(arg0, arg1, arg2, arg3, arg4, bool)
    }
}
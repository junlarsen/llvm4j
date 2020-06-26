package dev.supergrecko.vexe.llvm.executionengine.callbacks

import dev.supergrecko.vexe.llvm.internal.contracts.Callback
import org.bytedeco.javacpp.BytePointer
import org.bytedeco.javacpp.Pointer
import org.bytedeco.llvm.LLVM.LLVMMemoryManagerAllocateCodeSectionCallback

public typealias MemoryManagerAllocateCodeSectionCallback = (
    Pointer?, Long, Int, Int, BytePointer?
) -> BytePointer

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
        return callback.invoke(arg0, arg1, arg2, arg3, arg4)
    }
}
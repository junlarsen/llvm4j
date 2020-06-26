package dev.supergrecko.vexe.llvm.executionengine.callbacks

import dev.supergrecko.vexe.llvm.internal.contracts.Callback
import org.bytedeco.javacpp.BytePointer
import org.bytedeco.javacpp.Pointer
import org.bytedeco.llvm.LLVM.LLVMMemoryManagerFinalizeMemoryCallback

public typealias MemoryManagerFinalizeMemoryCallback = (
    Pointer?, String?
) -> Int

public class MemoryManagerFinalizeMemoryBase(
    private val callback: MemoryManagerFinalizeMemoryCallback
) : LLVMMemoryManagerFinalizeMemoryCallback(), Callback {
    public override fun call(arg0: Pointer?, arg1: BytePointer?): Int {
        val msg = arg1?.string

        return callback.invoke(arg0, msg)
    }
}
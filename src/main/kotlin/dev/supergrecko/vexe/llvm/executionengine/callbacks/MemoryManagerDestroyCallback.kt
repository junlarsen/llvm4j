package dev.supergrecko.vexe.llvm.executionengine.callbacks

import dev.supergrecko.vexe.llvm.internal.contracts.Callback
import org.bytedeco.javacpp.Pointer
import org.bytedeco.llvm.LLVM.LLVMMemoryManagerDestroyCallback

public typealias MemoryManagerDestroyCallback = (
    Pointer?
) -> Unit

public class MemoryManagerDestroyBase(
    private val callback: MemoryManagerDestroyCallback
) : LLVMMemoryManagerDestroyCallback(), Callback {
    public override fun call(arg0: Pointer?) {
        callback.invoke(arg0)
    }
}

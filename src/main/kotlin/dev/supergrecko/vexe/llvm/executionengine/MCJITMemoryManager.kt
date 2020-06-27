package dev.supergrecko.vexe.llvm.executionengine

import dev.supergrecko.vexe.llvm.executionengine.callbacks.MemoryManagerAllocateCodeSectionBase
import dev.supergrecko.vexe.llvm.executionengine.callbacks.MemoryManagerAllocateCodeSectionCallback
import dev.supergrecko.vexe.llvm.executionengine.callbacks.MemoryManagerAllocateDataSectionBase
import dev.supergrecko.vexe.llvm.executionengine.callbacks.MemoryManagerAllocateDataSectionCallback
import dev.supergrecko.vexe.llvm.executionengine.callbacks.MemoryManagerDestroyBase
import dev.supergrecko.vexe.llvm.executionengine.callbacks.MemoryManagerDestroyCallback
import dev.supergrecko.vexe.llvm.executionengine.callbacks.MemoryManagerFinalizeMemoryBase
import dev.supergrecko.vexe.llvm.executionengine.callbacks.MemoryManagerFinalizeMemoryCallback
import dev.supergrecko.vexe.llvm.internal.contracts.ContainsReference
import dev.supergrecko.vexe.llvm.internal.contracts.Disposable
import org.bytedeco.javacpp.Pointer
import org.bytedeco.llvm.LLVM.LLVMMCJITMemoryManagerRef
import org.bytedeco.llvm.global.LLVM

public class MCJITMemoryManager internal constructor() :
    ContainsReference<LLVMMCJITMemoryManagerRef>, Disposable {
    public override lateinit var ref: LLVMMCJITMemoryManagerRef
        internal set
    public override var valid: Boolean = true

    public constructor(llvmRef: LLVMMCJITMemoryManagerRef) : this() {
        ref = llvmRef
    }

    //region ExecutionEngine
    /**
     * Create a new simple MCJIT memory manager
     *
     * This is a memory manager for an MCJIT compiler which operators on
     * callbacks. You may pass a [client] which will be passed into each
     * callback upon call.
     *
     * @see LLVM.LLVMCreateSimpleMCJITMemoryManager
     */
    public constructor(
        client: Pointer,
        onAllocateCode: MemoryManagerAllocateCodeSectionCallback,
        onAllocateData: MemoryManagerAllocateDataSectionCallback,
        onFinalizeMemory: MemoryManagerFinalizeMemoryCallback,
        onManagerDestroy: MemoryManagerDestroyCallback
    ) : this() {
        ref = LLVM.LLVMCreateSimpleMCJITMemoryManager(
            client,
            MemoryManagerAllocateCodeSectionBase(onAllocateCode),
            MemoryManagerAllocateDataSectionBase(onAllocateData),
            MemoryManagerFinalizeMemoryBase(onFinalizeMemory),
            MemoryManagerDestroyBase(onManagerDestroy)
        )
    }
    //endregion ExecutionEngine

    override fun dispose() {
        require(valid) { "Cannot dispose object twice" }

        valid = false

        LLVM.LLVMDisposeMCJITMemoryManager(ref)
    }
}

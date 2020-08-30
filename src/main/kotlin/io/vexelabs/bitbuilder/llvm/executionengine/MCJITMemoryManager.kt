package io.vexelabs.bitbuilder.llvm.executionengine

import io.vexelabs.bitbuilder.llvm.executionengine.callbacks.MemoryManagerAllocateCodeSectionBase
import io.vexelabs.bitbuilder.llvm.executionengine.callbacks.MemoryManagerAllocateCodeSectionCallback
import io.vexelabs.bitbuilder.llvm.executionengine.callbacks.MemoryManagerAllocateDataSectionBase
import io.vexelabs.bitbuilder.llvm.executionengine.callbacks.MemoryManagerAllocateDataSectionCallback
import io.vexelabs.bitbuilder.llvm.executionengine.callbacks.MemoryManagerDestroyBase
import io.vexelabs.bitbuilder.llvm.executionengine.callbacks.MemoryManagerDestroyCallback
import io.vexelabs.bitbuilder.llvm.executionengine.callbacks.MemoryManagerFinalizeMemoryBase
import io.vexelabs.bitbuilder.llvm.executionengine.callbacks.MemoryManagerFinalizeMemoryCallback
import io.vexelabs.bitbuilder.llvm.internal.contracts.ContainsReference
import io.vexelabs.bitbuilder.llvm.internal.contracts.Disposable
import org.bytedeco.javacpp.Pointer
import org.bytedeco.llvm.LLVM.LLVMMCJITMemoryManagerRef
import org.bytedeco.llvm.global.LLVM

public class MCJITMemoryManager internal constructor() :
    ContainsReference<LLVMMCJITMemoryManagerRef>, Disposable {
    public override var valid: Boolean = true
    public override lateinit var ref: LLVMMCJITMemoryManagerRef
        internal set

    public constructor(llvmRef: LLVMMCJITMemoryManagerRef) : this() {
        ref = llvmRef
    }

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

    public override fun dispose() {
        require(valid) { "Cannot dispose object twice" }

        valid = false

        LLVM.LLVMDisposeMCJITMemoryManager(ref)
    }
}

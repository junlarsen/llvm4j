package dev.supergrecko.vexe.llvm.executionengine

import dev.supergrecko.vexe.llvm.internal.contracts.ContainsReference
import dev.supergrecko.vexe.llvm.internal.contracts.Disposable
import dev.supergrecko.vexe.llvm.internal.contracts.Validatable
import org.bytedeco.javacpp.Pointer
import org.bytedeco.llvm.LLVM.LLVMMCJITMemoryManagerRef
import org.bytedeco.llvm.LLVM.LLVMMemoryManagerAllocateCodeSectionCallback
import org.bytedeco.llvm.LLVM.LLVMMemoryManagerAllocateDataSectionCallback
import org.bytedeco.llvm.LLVM.LLVMMemoryManagerDestroyCallback
import org.bytedeco.llvm.LLVM.LLVMMemoryManagerFinalizeMemoryCallback
import org.bytedeco.llvm.global.LLVM

public class MCJITMemoryManager internal constructor() :
    ContainsReference<LLVMMCJITMemoryManagerRef>, Validatable, Disposable,
    AutoCloseable {
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
     * TODO: Replace callbacks with Kotlin Lambda
     *
     * @see LLVM.LLVMCreateSimpleMCJITMemoryManager
     */
    public constructor(
        client: Pointer,
        onAllocateCode: LLVMMemoryManagerAllocateCodeSectionCallback,
        onAllocateData: LLVMMemoryManagerAllocateDataSectionCallback,
        onFinalizeMemory: LLVMMemoryManagerFinalizeMemoryCallback,
        onManagerDestroy: LLVMMemoryManagerDestroyCallback
    ) : this() {
        ref = LLVM.LLVMCreateSimpleMCJITMemoryManager(
            client,
            onAllocateCode,
            onAllocateData,
            onFinalizeMemory,
            onManagerDestroy
        )
    }
    //endregion ExecutionEngine

    override fun dispose() {
        require(valid) { "Cannot dispose object twice" }

        valid = false

        LLVM.LLVMDisposeMCJITMemoryManager(ref)
    }

    override fun close() = dispose()
}
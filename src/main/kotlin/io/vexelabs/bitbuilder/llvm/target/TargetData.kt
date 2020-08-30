package io.vexelabs.bitbuilder.llvm.target

import io.vexelabs.bitbuilder.llvm.internal.contracts.ContainsReference
import io.vexelabs.bitbuilder.llvm.internal.contracts.Disposable
import io.vexelabs.bitbuilder.llvm.target.TargetMachine
import org.bytedeco.llvm.LLVM.LLVMTargetDataRef
import org.bytedeco.llvm.global.LLVM

/**
 * Interface to llvm::TargetData
 *
 * A parsed version of the target data layout string in and methods for
 * querying it
 *
 * @see LLVMTargetDataRef
 */
public class TargetData internal constructor() :
    ContainsReference<LLVMTargetDataRef>, Disposable {
    public override var valid: Boolean = true
    public override lateinit var ref: LLVMTargetDataRef
        internal set

    public constructor(llvmRef: LLVMTargetDataRef) : this() {
        ref = llvmRef
    }

    /**
     * Create a new target data layout
     *
     * @see LLVM.LLVMCreateTargetData
     */
    public constructor(target: String) : this() {
        ref = LLVM.LLVMCreateTargetData(target)
    }

    /**
     * Create a target data layout from a target machine
     *
     * @see LLVM.LLVMCreateTargetDataLayout
     */
    public constructor(machine: TargetMachine) : this() {
        ref = LLVM.LLVMCreateTargetDataLayout(machine.ref)
    }

    public override fun dispose() {
        require(valid) { "Cannot dispose object twice" }

        valid = false

        LLVM.LLVMDisposeTargetData(ref)
    }
}

package io.vexelabs.bitbuilder.llvm.ir

import io.vexelabs.bitbuilder.llvm.internal.contracts.ContainsReference
import org.bytedeco.llvm.LLVM.LLVMPassRegistryRef
import org.bytedeco.llvm.global.LLVM

/**
 * Interface to llvm::PassRegistry
 *
 * @see LLVMPassRegistryRef
 */
public object PassRegistry : ContainsReference<LLVMPassRegistryRef> {
    public override val ref: LLVMPassRegistryRef by lazy {
        LLVM.LLVMGetGlobalPassRegistry()
    }
}

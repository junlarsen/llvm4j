package dev.supergrecko.vexe.llvm.ir

import dev.supergrecko.vexe.llvm.internal.contracts.ContainsReference
import org.bytedeco.llvm.LLVM.LLVMPassRegistryRef
import org.bytedeco.llvm.global.LLVM

public object PassRegistry : ContainsReference<LLVMPassRegistryRef> {
    public override val ref: LLVMPassRegistryRef by lazy {
        LLVM.LLVMGetGlobalPassRegistry()
    }
}

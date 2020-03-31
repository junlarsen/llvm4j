package dev.supergrecko.kllvm.core.typedefs

import org.bytedeco.llvm.LLVM.LLVMPassRegistryRef

public class PassRegistry internal constructor(registry: LLVMPassRegistryRef) {
    internal var ref: LLVMPassRegistryRef = registry
}
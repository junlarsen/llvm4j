package dev.supergrecko.kllvm.core.typedefs

import org.bytedeco.llvm.LLVM.LLVMPassRegistryRef

public class PassRegistry internal constructor() {
    internal lateinit var ref: LLVMPassRegistryRef

    internal constructor(registry: LLVMPassRegistryRef) : this() {
        ref = registry
    }
}
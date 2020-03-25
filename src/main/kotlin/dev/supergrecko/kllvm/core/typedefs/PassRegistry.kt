package dev.supergrecko.kllvm.core.typedefs

import org.bytedeco.llvm.LLVM.LLVMPassRegistryRef

public class PassRegistry internal constructor(internal val llvmRegistry: LLVMPassRegistryRef)

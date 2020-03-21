package dev.supergrecko.kllvm.core.typedefs

import org.bytedeco.llvm.LLVM.LLVMPassRegistryRef

public class LLVMPassRegistry internal constructor(internal val llvmRegistry: LLVMPassRegistryRef)

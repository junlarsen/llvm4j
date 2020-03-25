package dev.supergrecko.kllvm.core.typedefs

import org.bytedeco.llvm.LLVM.LLVMBasicBlockRef

public class BasicBlock internal constructor(internal val llvmBlock: LLVMBasicBlockRef)

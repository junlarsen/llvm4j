package dev.supergrecko.kllvm.core

import org.bytedeco.llvm.LLVM.LLVMValueRef

public class LLVMValue internal constructor(internal val llvmValue: LLVMValueRef)
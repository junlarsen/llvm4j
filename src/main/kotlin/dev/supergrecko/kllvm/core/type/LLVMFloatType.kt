package dev.supergrecko.kllvm.core.type

import org.bytedeco.llvm.LLVM.LLVMTypeRef

public class LLVMFloatType internal constructor(llvmType: LLVMTypeRef) : LLVMType(llvmType)

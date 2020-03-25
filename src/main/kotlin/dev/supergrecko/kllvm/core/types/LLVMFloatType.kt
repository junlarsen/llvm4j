package dev.supergrecko.kllvm.core.types

import dev.supergrecko.kllvm.core.typedefs.LLVMType
import org.bytedeco.llvm.LLVM.LLVMTypeRef

public class LLVMFloatType(llvmType: LLVMTypeRef) : LLVMType(llvmType)
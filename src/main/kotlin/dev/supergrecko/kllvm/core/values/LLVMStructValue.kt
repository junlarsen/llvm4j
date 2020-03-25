package dev.supergrecko.kllvm.core.values

import dev.supergrecko.kllvm.core.typedefs.LLVMValue
import org.bytedeco.llvm.LLVM.LLVMValueRef

public class LLVMStructValue(llvmValue: LLVMValueRef) : LLVMValue(llvmValue)
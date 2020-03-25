package dev.supergrecko.kllvm.core.values

import dev.supergrecko.kllvm.core.typedefs.LLVMValue
import org.bytedeco.llvm.LLVM.LLVMValueRef

// TODO: Learn how to use/implement this
public class LLVMBasicValueUse(llvmValue: LLVMValueRef) : LLVMValue(llvmValue)
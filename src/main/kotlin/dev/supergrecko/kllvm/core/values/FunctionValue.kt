package dev.supergrecko.kllvm.core.values

import dev.supergrecko.kllvm.core.typedefs.Value
import org.bytedeco.llvm.LLVM.LLVMValueRef

public class FunctionValue(llvmValue: LLVMValueRef) : Value(llvmValue)
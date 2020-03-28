package dev.supergrecko.kllvm.core.values

import dev.supergrecko.kllvm.core.typedefs.BasicBlock
import dev.supergrecko.kllvm.core.typedefs.Value
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public class FunctionValue(llvmValue: LLVMValueRef) : Value(llvmValue) {
    fun appendBasicBlock(name: String): BasicBlock {
        return BasicBlock(LLVM.LLVMAppendBasicBlock(getUnderlyingReference(), name))
    }
}
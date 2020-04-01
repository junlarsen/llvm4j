package dev.supergrecko.kllvm.core.values

import dev.supergrecko.kllvm.core.typedefs.BasicBlock
import dev.supergrecko.kllvm.core.typedefs.Value
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public class FunctionValue internal constructor() : Value() {
    public constructor(llvmValue: LLVMValueRef) : this() {
        ref = llvmValue
    }

    public constructor(value: Value) : this(value.ref)

    fun appendBasicBlock(name: String): BasicBlock {
        return BasicBlock(LLVM.LLVMAppendBasicBlock(getUnderlyingReference(), name))
    }

    /**
     * TODO: Maybe throw an index out of bounds exception here
     * in case a param isn't found? Or maybe return nullable value? Up for investigation
     */
    fun getParam(index: Int): Value {
        return Value(LLVM.LLVMGetParam(ref, index))
    }
}
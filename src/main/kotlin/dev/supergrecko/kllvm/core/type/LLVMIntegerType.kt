package dev.supergrecko.kllvm.core.type

import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM

public class LLVMIntegerType internal constructor(llvmType: LLVMTypeRef) : LLVMType(llvmType) {
    /**
     * Returns the amount of bits this integer type can hold
     */
    public fun typeWidth(): Int {
        return LLVM.LLVMGetIntTypeWidth(llvmType)
    }
}

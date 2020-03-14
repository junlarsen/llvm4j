package dev.supergrecko.kllvm.core.type

import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM

public class LLVMIntegerType internal constructor(llvmType: LLVMTypeRef) : LLVMType(llvmType) {
    /**
     * Get the amount of bits this type can hold
     */
    public fun typeWidth(): Int {
        return LLVM.LLVMGetIntTypeWidth(llvmType)
    }
}

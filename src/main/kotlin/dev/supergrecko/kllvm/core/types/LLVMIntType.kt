package dev.supergrecko.kllvm.core.types

import dev.supergrecko.kllvm.core.typedefs.LLVMType
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM

public class LLVMIntType(llvmType: LLVMTypeRef) : LLVMType(llvmType) {
    public fun getTypeWidth(): Int {
        return LLVM.LLVMGetIntTypeWidth(llvmType)
    }
}
package dev.supergrecko.kllvm.core.types

import dev.supergrecko.kllvm.core.typedefs.Type
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM

public class IntType(llvmType: LLVMTypeRef) : Type(llvmType) {
    public fun getTypeWidth(): Int {
        return LLVM.LLVMGetIntTypeWidth(llvmType)
    }
}
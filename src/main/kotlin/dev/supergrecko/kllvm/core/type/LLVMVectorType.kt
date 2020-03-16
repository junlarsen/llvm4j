package dev.supergrecko.kllvm.core.type

import dev.supergrecko.kllvm.utils.iterateIntoType
import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM

public class LLVMVectorType internal constructor(llvmType: LLVMTypeRef) : LLVMType(llvmType) {
    public fun getSize(): Int {
        return LLVM.LLVMGetVectorSize(llvmType)
    }

    /**
     * TODO: Learn how to test this
     */
    public fun getSubtypes(): List<LLVMType> {
        val dest = PointerPointer<LLVMTypeRef>(getSize().toLong())
        LLVM.LLVMGetSubtypes(llvmType, dest)

        return dest.iterateIntoType { LLVMType(it) }
    }

    public fun getElementType(): LLVMType {
        return LLVMType(LLVM.LLVMGetElementType(llvmType))
    }
}

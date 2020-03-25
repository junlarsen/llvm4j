package dev.supergrecko.kllvm.core.types

import dev.supergrecko.kllvm.core.typedefs.LLVMType
import dev.supergrecko.kllvm.utils.iterateIntoType
import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM

public class LLVMPointerType(llvmType: LLVMTypeRef) : LLVMType(llvmType) {
    public fun getAddressSpace(): Int {
        return LLVM.LLVMGetPointerAddressSpace(llvmType)
    }

    public fun getElementCount(): Int {
        return LLVM.LLVMGetNumContainedTypes(llvmType)
    }

    public fun getSubtypes(): List<LLVMType> {
        val dest = PointerPointer<LLVMTypeRef>(getElementCount().toLong())
        LLVM.LLVMGetSubtypes(llvmType, dest)

        return dest.iterateIntoType { LLVMType(it) }
    }

    public fun getElementType(): LLVMType {
        val type = LLVM.LLVMGetElementType(llvmType)

        return LLVMType(type)
    }
}
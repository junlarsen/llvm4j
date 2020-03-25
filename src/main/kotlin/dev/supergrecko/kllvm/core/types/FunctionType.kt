package dev.supergrecko.kllvm.core.types

import dev.supergrecko.kllvm.core.typedefs.LLVMType
import dev.supergrecko.kllvm.utils.iterateIntoType
import dev.supergrecko.kllvm.utils.toBoolean
import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM

public class FunctionType(llvmType: LLVMTypeRef) : LLVMType(llvmType) {
    public fun isVariadic(): Boolean {
        return LLVM.LLVMIsFunctionVarArg(llvmType).toBoolean()
    }

    public fun getParameterCount(): Int {
        return LLVM.LLVMCountParamTypes(llvmType)
    }

    public fun getReturnType(): LLVMType {
        val type = LLVM.LLVMGetReturnType(llvmType)

        return LLVMType(type)
    }

    public fun getParameterTypes(): List<LLVMType> {
        val dest = PointerPointer<LLVMTypeRef>(getParameterCount().toLong())
        LLVM.LLVMGetParamTypes(llvmType, dest)

        return dest
                .iterateIntoType { LLVMType(it) }
    }
}
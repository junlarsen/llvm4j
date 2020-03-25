package dev.supergrecko.kllvm.core.types

import dev.supergrecko.kllvm.core.typedefs.Type
import dev.supergrecko.kllvm.utils.iterateIntoType
import dev.supergrecko.kllvm.utils.toBoolean
import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM

public class FunctionType(llvmType: LLVMTypeRef) : Type(llvmType) {
    public fun isVariadic(): Boolean {
        return LLVM.LLVMIsFunctionVarArg(llvmType).toBoolean()
    }

    public fun getParameterCount(): Int {
        return LLVM.LLVMCountParamTypes(llvmType)
    }

    public fun getReturnType(): Type {
        val type = LLVM.LLVMGetReturnType(llvmType)

        return Type(type)
    }

    public fun getParameterTypes(): List<Type> {
        val dest = PointerPointer<LLVMTypeRef>(getParameterCount().toLong())
        LLVM.LLVMGetParamTypes(llvmType, dest)

        return dest
                .iterateIntoType { Type(it) }
    }
}
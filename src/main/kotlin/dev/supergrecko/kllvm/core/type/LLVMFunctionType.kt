package dev.supergrecko.kllvm.core.type

import dev.supergrecko.kllvm.utils.toBoolean
import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM

/**
 * Wrapper around LLVM Function Types
 *
 * @property llvmType Internal [LLVMTypeRef] reference
 */
public class LLVMFunctionType internal constructor(
        llvmType: LLVMTypeRef
) : LLVMType(llvmType) {
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

    public fun getParameters(): List<LLVMType> {
        val dest = PointerPointer<LLVMTypeRef>()
        LLVM.LLVMGetParamTypes(llvmType, dest)

        val res = mutableListOf<LLVMType>()

        for (i in 0..dest.capacity()) {
            res += LLVMType(dest.get(i) as LLVMTypeRef)
        }

        return res
    }
}

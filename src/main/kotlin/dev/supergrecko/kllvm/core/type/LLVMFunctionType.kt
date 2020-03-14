package dev.supergrecko.kllvm.core.type

import dev.supergrecko.kllvm.utils.toBoolean
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM

/**
 * Wrapper around LLVM Function Types
 *
 * @property llvmType Internal [LLVMTypeRef] reference
 * @property returnType [LLVMType] for function return type
 * @property paramTypes [LLVMType] list for function parameters
 *
 * TODO: Determine whether passing [returnType] and [paramTypes] is okay (does LLVM use this elsewhere?)
 */
public class LLVMFunctionType internal constructor(
        llvmType: LLVMTypeRef,
        internal val returnType: LLVMType,
        internal val paramTypes: List<LLVMType>
) : LLVMType(llvmType) {
    /**
     * Is this function type variadic?
     *
     * TODO: Determine whether passing this from [LLVMFunctionType.type] is more optimal
     */
    public fun isVariadic(): Boolean {
        return LLVM.LLVMIsFunctionVarArg(llvmType).toBoolean()
    }

    /**
     * Get how many parameters this function type expects
     *
     * TODO: Determine whether passing this from [LLVMFunctionType.type] is more optimal
     */
    public fun getParameterCount(): Int {
        return LLVM.LLVMCountParamTypes(llvmType)
    }

    public fun getReturnType(): LLVMType = returnType
    public fun getParameters(): List<LLVMType> = paramTypes
}

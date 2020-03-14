package dev.supergrecko.kllvm.core.type

import dev.supergrecko.kllvm.contracts.CompositeTypeFactory
import dev.supergrecko.kllvm.utils.toBoolean
import dev.supergrecko.kllvm.utils.toInt
import org.bytedeco.javacpp.PointerPointer
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

    public companion object : CompositeTypeFactory<LLVMFunctionType> {
        /**
         * Create a function type
         *
         * Argument count is automatically calculated from [paramTypes].
         */
        public fun type(returnType: LLVMType, paramTypes: List<LLVMType>, isVariadic: Boolean): LLVMFunctionType {
            val types = paramTypes.map { it.llvmType }
            val array = ArrayList(types).toTypedArray()

            val ptr = PointerPointer(*array)

            val type = LLVM.LLVMFunctionType(returnType.llvmType, ptr, paramTypes.size, isVariadic.toInt())

            return LLVMFunctionType(type, returnType, paramTypes)
        }
    }
}

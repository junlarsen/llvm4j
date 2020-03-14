package dev.supergrecko.kllvm.core.type

import dev.supergrecko.kllvm.contracts.CompositeTypeFactory
import dev.supergrecko.kllvm.utils.toInt
import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM

public class LLVMFunctionType internal constructor(llvmType: LLVMTypeRef) : LLVMType(llvmType) {
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

            return LLVMFunctionType(type)
        }
    }
}

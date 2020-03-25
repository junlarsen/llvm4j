package dev.supergrecko.kllvm.core.types

import dev.supergrecko.kllvm.core.typedefs.Type
import dev.supergrecko.kllvm.utils.iterateIntoType
import dev.supergrecko.kllvm.utils.toBoolean
import dev.supergrecko.kllvm.utils.toInt
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

    public companion object {
        /**
         * Create a function type
         *
         * This will construct a function type which returns the type provided in [returns] which expects to receive
         * parameters of the types provided in [tys]. You can mark a function type as variadic by setting the [variadic] arg
         * to true.
         */
        @JvmStatic
        public fun new(returns: Type, types: List<Type>, variadic: Boolean): FunctionType {
            val arr = ArrayList(types.map { it.llvmType }).toTypedArray()

            val fn = LLVM.LLVMFunctionType(returns.llvmType, PointerPointer(*arr), arr.size, variadic.toInt())

            return FunctionType(fn)
        }
    }
}
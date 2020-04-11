package dev.supergrecko.kllvm.types

import dev.supergrecko.kllvm.internal.util.iterateIntoType
import dev.supergrecko.kllvm.internal.util.toBoolean
import dev.supergrecko.kllvm.internal.util.toInt
import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM

public class FunctionType internal constructor() : Type() {
    public constructor(llvmType: LLVMTypeRef) : this() {
        ref = llvmType
        requireKind(TypeKind.Function)
    }

    /**
     * Create a function type
     *
     * This will construct a function type which returns the type provided in [returns] which expects to receive
     * parameters of the types provided in [tys]. You can mark a function type as variadic by setting the [variadic] arg
     * to true.
     */
    public constructor(
        returns: Type,
        types: List<Type>,
        variadic: Boolean
    ) : this() {
        val arr = ArrayList(types.map { it.ref }).toTypedArray()

        ref = LLVM.LLVMFunctionType(
            returns.ref,
            PointerPointer(*arr),
            arr.size,
            variadic.toInt()
        )
    }

    //region Core::Types::FunctionTypes
    public fun isVariadic(): Boolean {
        return LLVM.LLVMIsFunctionVarArg(ref).toBoolean()
    }

    public fun getParameterCount(): Int {
        return LLVM.LLVMCountParamTypes(ref)
    }

    public fun getReturnType(): Type {
        val type = LLVM.LLVMGetReturnType(ref)

        return Type(type)
    }

    public fun getParameterTypes(): List<Type> {
        val dest = PointerPointer<LLVMTypeRef>(getParameterCount().toLong())
        LLVM.LLVMGetParamTypes(ref, dest)

        return dest.iterateIntoType {
            Type(
                it
            )
        }
    }
    //endregion Core::Types::FunctionTypes
}
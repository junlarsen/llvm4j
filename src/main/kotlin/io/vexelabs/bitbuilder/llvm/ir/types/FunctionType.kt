package io.vexelabs.bitbuilder.llvm.ir.types

import io.vexelabs.bitbuilder.internal.fromLLVMBool
import io.vexelabs.bitbuilder.internal.map
import io.vexelabs.bitbuilder.internal.toLLVMBool
import io.vexelabs.bitbuilder.internal.toPointerPointer
import io.vexelabs.bitbuilder.llvm.ir.Type
import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM

public class FunctionType internal constructor() : Type() {
    public constructor(llvmRef: LLVMTypeRef) : this() {
        ref = llvmRef
    }

    /**
     * Create a function types
     *
     * This will construct a function types which returns the types provided in
     * [returns] which expects to receive parameters of the types provided in
     * [types]. You can mark a function types as variadic by setting the
     * [variadic] arg to true.
     */
    public constructor(
        returns: Type,
        types: List<Type>,
        variadic: Boolean
    ) : this() {
        val ptr = types.map { it.ref }.toPointerPointer()

        ref = LLVM.LLVMFunctionType(
            returns.ref,
            ptr,
            types.size,
            variadic.toLLVMBool()
        )

        ptr.deallocate()
    }

    /**
     * Is this function type variadic?
     *
     * @see LLVM.LLVMIsFunctionVarArg
     */
    public fun isVariadic(): Boolean {
        return LLVM.LLVMIsFunctionVarArg(ref).fromLLVMBool()
    }

    /**
     * Get the parameter count
     *
     * @see LLVM.LLVMCountParamTypes
     */
    public fun getParameterCount(): Int {
        return LLVM.LLVMCountParamTypes(ref)
    }

    /**
     * Get the return type
     *
     * @see LLVM.LLVMGetReturnType
     */
    public fun getReturnType(): Type {
        val type = LLVM.LLVMGetReturnType(ref)

        return Type(type)
    }

    /**
     * Get the parameter types
     *
     * @see LLVM.LLVMGetParamTypes
     */
    public fun getParameterTypes(): List<Type> {
        val dest = PointerPointer<LLVMTypeRef>(getParameterCount().toLong())
        LLVM.LLVMGetParamTypes(ref, dest)

        return dest.map { Type(it) }.also {
            dest.deallocate()
        }
    }
}

package dev.supergrecko.kllvm.core.types

import dev.supergrecko.kllvm.core.typedefs.Context
import dev.supergrecko.kllvm.core.typedefs.Type
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM

public class VoidType public constructor(context: Context = Context.getGlobalContext()) : Type() {
    init {
        ref = LLVM.LLVMVoidTypeInContext(context.ref)
    }

    /**
     * Internal constructor for actual reference
     */
    internal constructor(llvmType: LLVMTypeRef) : this() {
        ref = llvmType
    }

    public constructor(type: Type) : this(type.ref)
}

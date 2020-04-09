package dev.supergrecko.kllvm.types

import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM

public class PointerType internal constructor() : Type(),
    CompositeType,
    SequentialType {
    public constructor(llvmType: LLVMTypeRef) : this() {
        ref = llvmType
        requireKind(TypeKind.Pointer)
    }

    /**
     * Create a pointer type
     *
     * Creates a pointer type of type [type]. An address space may be provided, but defaults to 0.
     */
    public constructor(type: Type, address: Int = 0) : this() {
        require(address >= 0) { "Cannot use negative address" }

        ref = LLVM.LLVMPointerType(type.ref, address)
    }

    //region Core::Types::SequentialTypes
    public fun getAddressSpace(): Int {
        return LLVM.LLVMGetPointerAddressSpace(ref)
    }
    //endregion Core::Types::SequentialTypes
}

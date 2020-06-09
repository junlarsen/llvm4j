package dev.supergrecko.kllvm.ir.types

import dev.supergrecko.kllvm.ir.Type
import dev.supergrecko.kllvm.ir.TypeKind
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM

public class PointerType internal constructor() : Type(),
    CompositeType,
    SequentialType {
    /**
     * Construct a new Type from an LLVM pointer reference
     */
    public constructor(llvmType: LLVMTypeRef) : this() {
        ref = llvmType
        requireKind(TypeKind.Pointer)
    }

    /**
     * Create a pointer types
     *
     * Creates a pointer types of types [type]. An address space may be provided
     * but defaults to 0.
     *
     * @see LLVM.LLVMPointerType
     */
    public constructor(type: Type, address: Int = 0) : this() {
        require(address >= 0) { "Cannot use negative address" }

        ref = LLVM.LLVMPointerType(type.ref, address)
    }

    //region Core::Types::SequentialTypes
    /**
     * Get the address of this pointer
     *
     * @see LLVM.LLVMGetPointerAddressSpace
     */
    public fun getAddressSpace(): Int {
        return LLVM.LLVMGetPointerAddressSpace(ref)
    }
    //endregion Core::Types::SequentialTypes
}

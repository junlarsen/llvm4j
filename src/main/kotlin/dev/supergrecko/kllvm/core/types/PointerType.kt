package dev.supergrecko.kllvm.core.types

import dev.supergrecko.kllvm.annotations.Shared
import dev.supergrecko.kllvm.core.typedefs.Type
import dev.supergrecko.kllvm.utils.iterateIntoType
import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM

public class PointerType internal constructor() : Type() {
    /**
     * Internal constructor for actual reference
     */
    internal constructor(llvmType: LLVMTypeRef) : this() {
        ref = llvmType
    }

    public constructor(type: Type) : this(type.ref)

    /**
     * Create a pointer type
     *
     * Creates a pointer type of type [ty]. An address space may be provided, but defaults to 0.
     */
    public constructor(type: Type, address: Int = 0) : this() {
        require(address >= 0) { "Cannot use negative address" }

        ref = LLVM.LLVMPointerType(type.ref, address)
    }

    //region Core::Types::SequentialTypes
    public fun getAddressSpace(): Int {
        return LLVM.LLVMGetPointerAddressSpace(ref)
    }

    /**
     * Returns the amount of elements contained in this type
     *
     * This is shared with [ArrayType], [VectorType], [PointerType]
     */
    @Shared
    public fun getElementCount(): Int {
        return LLVM.LLVMGetNumContainedTypes(ref)
    }

    /**
     * Returns type's subtypes
     *
     * This is shared with [ArrayType], [VectorType], [PointerType]
     */
    @Shared
    public fun getSubtypes(): List<Type> {
        val dest = PointerPointer<LLVMTypeRef>(getElementCount().toLong())
        LLVM.LLVMGetSubtypes(ref, dest)

        return dest.iterateIntoType { Type(it) }
    }

    /**
     * Obtain the type of elements within a sequential type
     *
     * This is shared with [ArrayType], [VectorType], [PointerType]
     */
    @Shared
    public fun getElementType(): Type {
        val type = LLVM.LLVMGetElementType(ref)

        return Type(type)
    }
    //endregion Core::Types::SequentialTypes
}
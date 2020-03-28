package dev.supergrecko.kllvm.core.types

import dev.supergrecko.kllvm.annotations.Shared
import dev.supergrecko.kllvm.core.typedefs.Type
import dev.supergrecko.kllvm.utils.iterateIntoType
import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM

public class PointerType(llvmType: LLVMTypeRef) : Type(llvmType) {
    //region Core::Types::SequentialTypes
    public fun getAddressSpace(): Int {
        return LLVM.LLVMGetPointerAddressSpace(llvmType)
    }

    /**
     * Returns the amount of elements contained in this type
     *
     * This is shared with [ArrayType], [VectorType], [PointerType]
     */
    @Shared
    public fun getElementCount(): Int {
        return LLVM.LLVMGetNumContainedTypes(llvmType)
    }

    /**
     * Returns type's subtypes
     *
     * This is shared with [ArrayType], [VectorType], [PointerType]
     */
    @Shared
    public fun getSubtypes(): List<Type> {
        val dest = PointerPointer<LLVMTypeRef>(getElementCount().toLong())
        LLVM.LLVMGetSubtypes(llvmType, dest)

        return dest.iterateIntoType { Type(it) }
    }

    /**
     * Obtain the type of elements within a sequential type
     *
     * This is shared with [ArrayType], [VectorType], [PointerType]
     */
    @Shared
    public fun getElementType(): Type {
        val type = LLVM.LLVMGetElementType(llvmType)

        return Type(type)
    }
    //endregion Core::Types::SequentialTypes

    public companion object {
        /**
         * Create a pointer type
         *
         * Creates a pointer type of type [ty]. An address space may be provided, but defaults to 0.
         */
        @JvmStatic
        public fun new(ty: Type, address: Int = 0): PointerType {
            require(address >= 0) { "Cannot use negative address" }

            val ptr = LLVM.LLVMPointerType(ty.llvmType, address)

            return PointerType(ptr)
        }
    }
}
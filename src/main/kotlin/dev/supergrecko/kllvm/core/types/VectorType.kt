package dev.supergrecko.kllvm.core.types

import dev.supergrecko.kllvm.annotations.Shared
import dev.supergrecko.kllvm.core.typedefs.Type
import dev.supergrecko.kllvm.core.typedefs.Value
import dev.supergrecko.kllvm.core.values.VectorValue
import dev.supergrecko.kllvm.utils.iterateIntoType
import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM

public class VectorType(llvmType: LLVMTypeRef) : Type(llvmType) {
    //region Core::Types::SequentialTypes
    /**
     * Returns the amount of elements contained in this type
     *
     * This is shared with [ArrayType], [VectorType], [PointerType]
     */
    @Shared
    public fun getElementCount(): Int {
        return LLVM.LLVMGetVectorSize(llvmType)
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

    //region Core::Values::Constants::CompositeConstants
    public fun getConstantVector(values: List<Value>): VectorValue {
        val ptr = ArrayList(values.map { it.llvmValue }).toTypedArray()

        val vec = LLVM.LLVMConstVector(PointerPointer(*ptr), ptr.size)

        return VectorValue(vec)
    }
    //endregion Core::Values::Constants::CompositeConstants

    public companion object {
        /**
         * Create a vector type
         *
         * Constructs a vector type of type [ty] with size [size].
         */
        @JvmStatic
        public fun new(type: Type, size: Int): VectorType {
            require(size >= 0) { "Cannot make vector of negative size" }

            return VectorType(LLVM.LLVMVectorType(type.llvmType, size))
        }
    }
}

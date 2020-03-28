package dev.supergrecko.kllvm.core.types

import dev.supergrecko.kllvm.core.typedefs.Type
import dev.supergrecko.kllvm.core.typedefs.Value
import dev.supergrecko.kllvm.core.values.VectorValue
import dev.supergrecko.kllvm.utils.iterateIntoType
import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM

public class VectorType(llvmType: LLVMTypeRef) : Type(llvmType) {
    //region Core::Types::SequentialTypes
    public fun getElementCount(): Int {
        return LLVM.LLVMGetVectorSize(llvmType)
    }

    public fun getSubtypes(): List<Type> {
        val dest = PointerPointer<LLVMTypeRef>(getElementCount().toLong())
        LLVM.LLVMGetSubtypes(llvmType, dest)

        return dest.iterateIntoType { Type(it) }
    }

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

package dev.supergrecko.kllvm.core.types

import dev.supergrecko.kllvm.core.typedefs.Type
import dev.supergrecko.kllvm.utils.iterateIntoType
import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM

public class VectorType(llvmType: LLVMTypeRef) : Type(llvmType) {
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

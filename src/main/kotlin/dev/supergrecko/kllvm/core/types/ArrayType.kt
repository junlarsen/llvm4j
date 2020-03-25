package dev.supergrecko.kllvm.core.types

import dev.supergrecko.kllvm.core.typedefs.Type
import dev.supergrecko.kllvm.utils.iterateIntoType
import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM

public class ArrayType(llvmType: LLVMTypeRef) : Type(llvmType) {
    public fun getElementCount(): Int {
        return LLVM.LLVMGetArrayLength(llvmType)
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
         * Create an array type
         *
         * Constructs an array of type [ty] with size [size].
         */
        @JvmStatic
        public fun new(type: Type, size: Int): ArrayType {
            require(size >= 0) { "Cannot make array of negative size" }

            return ArrayType(LLVM.LLVMArrayType(type.llvmType, size))
        }
    }
}

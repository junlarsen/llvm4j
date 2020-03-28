package dev.supergrecko.kllvm.core.types

import dev.supergrecko.kllvm.annotations.Shared
import dev.supergrecko.kllvm.core.typedefs.Context
import dev.supergrecko.kllvm.core.typedefs.Type
import dev.supergrecko.kllvm.core.values.ArrayValue
import dev.supergrecko.kllvm.utils.iterateIntoType
import dev.supergrecko.kllvm.utils.toInt
import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM

public class ArrayType(llvmType: LLVMTypeRef) : Type(llvmType) {
    //region Core::Types::SequentialTypes
    /**
     * Returns the amount of elements contained in this type
     *
     * This is shared with [ArrayType], [VectorType], [PointerType]
     */
    public fun getElementCount(): Int {
        return LLVM.LLVMGetArrayLength(llvmType)
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
    public fun getConstString(content: String, nullTerminate: Boolean, context: Context = Context.getGlobalContext()): ArrayValue {
        val str = LLVM.LLVMConstStringInContext(context.llvmCtx, content, content.length, nullTerminate.toInt())

        return ArrayValue(str)
    }
    //endregion Core::Values::Constants::CompositeConstants

    public companion object {
        /**
         * Create an array type
         *
         * Constructs an array of type [type] with size [size].
         */
        @JvmStatic
        public fun new(type: Type, size: Int): ArrayType {
            require(size >= 0) { "Cannot make array of negative size" }

            return ArrayType(LLVM.LLVMArrayType(type.llvmType, size))
        }
    }
}

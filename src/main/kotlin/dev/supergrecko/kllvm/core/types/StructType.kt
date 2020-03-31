package dev.supergrecko.kllvm.core.types

import dev.supergrecko.kllvm.core.typedefs.Context
import dev.supergrecko.kllvm.core.typedefs.Type
import dev.supergrecko.kllvm.core.typedefs.Value
import dev.supergrecko.kllvm.core.values.StructValue
import dev.supergrecko.kllvm.utils.iterateIntoType
import dev.supergrecko.kllvm.utils.toBoolean
import dev.supergrecko.kllvm.utils.toInt
import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM

public class StructType(llvmType: LLVMTypeRef) : Type(llvmType) {
    //region Core::Types::StructureTypes
    public fun isPacked(): Boolean {
        return LLVM.LLVMIsPackedStruct(llvmType).toBoolean()
    }

    public fun isOpaque(): Boolean {
        return LLVM.LLVMIsOpaqueStruct(llvmType).toBoolean()
    }

    public fun isLiteral(): Boolean {
        return LLVM.LLVMIsLiteralStruct(llvmType).toBoolean()
    }

    public fun setBody(elementTypes: List<Type>, packed: Boolean) {
        require(isOpaque())

        val types = elementTypes.map { it.llvmType }
        val array = ArrayList(types).toTypedArray()
        val ptr = PointerPointer(*array)

        LLVM.LLVMStructSetBody(llvmType, ptr, array.size, packed.toInt())
    }

    public fun getElementTypeAt(index: Int): Type {
        // Refactor when moved
        require(index <= getElementCount()) { "Requested index $index is out of bounds for this struct" }

        val type = LLVM.LLVMStructGetTypeAtIndex(llvmType, index)

        return Type(type)
    }

    public fun getName(): String {
        require(!isLiteral()) { "Literal structs are never named" }

        val name = LLVM.LLVMGetStructName(llvmType)

        return name.string
    }

    public fun getElementTypes(): List<Type> {
        val dest = PointerPointer<LLVMTypeRef>(getElementCount().toLong())
        LLVM.LLVMGetStructElementTypes(llvmType, dest)

        return dest.iterateIntoType { Type(it) }
    }

    public fun getElementCount(): Int {
        return LLVM.LLVMCountStructElementTypes(llvmType)
    }
    //endregion Core::Types::StructureTypes

    //region Core::Values::Constants::CompositeConstants
    /**
     * Create an anonymous ConstantStruct with the specified [values]
     */
    public fun getConstStruct(values: List<Value>, packed: Boolean, context: Context = Context.getGlobalContext()): StructValue {
        val ptr = ArrayList(values.map { it.ref }).toTypedArray()

        val struct = LLVM.LLVMConstStructInContext(context.ref, PointerPointer(*ptr), ptr.size, packed.toInt())

        return StructValue(struct)
    }

    /**
     * Create a non-anonymous ConstantStruct from values.
     */
    public fun getConstNamedStruct(type: StructType, values: List<Value>): StructValue {
        val ptr = ArrayList(values.map { it.ref }).toTypedArray()

        val struct = LLVM.LLVMConstNamedStruct(type.llvmType, PointerPointer(*ptr), ptr.size)

        return StructValue(struct)
    }
    //endregion Core::Values::Constants::CompositeConstants

    public companion object {
        /**
         * Create a structure type
         *
         * This method creates a structure type inside the given [ctx]. Do not that this method cannot produce opaque struct
         * types, use [opaque] for that.
         *
         * The struct body will be the types provided in [tys].
         */
        @JvmStatic
        public fun new(types: List<Type>, packed: Boolean, ctx: Context = Context.getGlobalContext()): StructType {
            val arr = ArrayList(types.map { it.llvmType }).toTypedArray()

            val struct = LLVM.LLVMStructTypeInContext(ctx.ref, PointerPointer(*arr), arr.size, packed.toInt())

            return StructType(struct)
        }

        /**
         * Create an opaque struct type
         *
         * This will create an opaque struct (a struct without a body, like C forward declaration) with the given [name].
         * You will be able to use [setBody] to assign a body to the opaque struct.
         */
        @JvmStatic
        public fun opaque(name: String, ctx: Context = Context.getGlobalContext()): StructType {
            val struct = LLVM.LLVMStructCreateNamed(ctx.ref, name)

            return StructType(struct)
        }
    }
}

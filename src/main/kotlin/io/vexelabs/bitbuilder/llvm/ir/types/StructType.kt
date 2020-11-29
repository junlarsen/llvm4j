package io.vexelabs.bitbuilder.llvm.ir.types

import io.vexelabs.bitbuilder.internal.fromLLVMBool
import io.vexelabs.bitbuilder.internal.map
import io.vexelabs.bitbuilder.internal.toLLVMBool
import io.vexelabs.bitbuilder.internal.toPointerPointer
import io.vexelabs.bitbuilder.llvm.ir.Context
import io.vexelabs.bitbuilder.llvm.ir.Type
import io.vexelabs.bitbuilder.llvm.ir.Value
import io.vexelabs.bitbuilder.llvm.ir.types.traits.CompositeType
import io.vexelabs.bitbuilder.llvm.ir.values.constants.ConstantInt
import io.vexelabs.bitbuilder.llvm.ir.values.constants.ConstantStruct
import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM

public class StructType internal constructor() :
    Type(),
    CompositeType {
    public constructor(llvmRef: LLVMTypeRef) : this() {
        ref = llvmRef
    }

    /**
     * Is this struct type packed?
     *
     * @see LLVM.LLVMIsPackedStruct
     */
    public fun isPacked(): Boolean {
        return LLVM.LLVMIsPackedStruct(ref).fromLLVMBool()
    }

    /**
     * Is this struct opaque?
     *
     * @see LLVM.LLVMIsOpaqueStruct
     */
    public fun isOpaque(): Boolean {
        return LLVM.LLVMIsOpaqueStruct(ref).fromLLVMBool()
    }

    /**
     * Is this struct literal?
     *
     * @see LLVM.LLVMIsLiteralStruct
     */
    public fun isLiteral(): Boolean {
        return LLVM.LLVMIsLiteralStruct(ref).fromLLVMBool()
    }

    /**
     * Set the element types of an opaque struct
     *
     * @see LLVM.LLVMStructSetBody
     */
    public fun setBody(types: List<Type>, packed: Boolean) {
        require(isOpaque()) { "Cannot set body of non-opaque struct" }

        val ptr = types.map { it.ref }.toPointerPointer()

        LLVM.LLVMStructSetBody(ref, ptr, types.size, packed.toLLVMBool())

        ptr.deallocate()
    }

    /**
     * Get the element type at the given [index]
     *
     * @see LLVM.LLVMGetElementType
     */
    public fun getElementTypeAt(index: Int): Type {
        require(index <= getElementCount()) {
            "Requested index $index is out of bounds for this struct"
        }

        val type = LLVM.LLVMStructGetTypeAtIndex(ref, index)

        return Type(type)
    }

    /**
     * Get the name of this non-literal struct
     *
     * @see LLVM.LLVMGetStructName
     */
    public fun getName(): String {
        require(!isLiteral()) { "Literal structures are never named" }

        val name = LLVM.LLVMGetStructName(ref)

        return name.string
    }

    /**
     * Get the element types of this struct
     *
     * @see LLVM.LLVMGetStructElementTypes
     */
    public fun getElementTypes(): List<Type> {
        val dest = PointerPointer<LLVMTypeRef>(getElementCount().toLong())

        LLVM.LLVMGetStructElementTypes(ref, dest)

        return dest.map { Type(it) }.also {
            dest.deallocate()
        }
    }

    /**
     * Get the size of this struct in bytes as an i64 ConstantInt
     *
     * @see LLVM.LLVMSizeOf
     */
    public fun getSizeOf(): ConstantInt {
        val ref = LLVM.LLVMSizeOf(ref)

        return ConstantInt(ref)
    }

    /**
     * Get the alignment of this type in bytes as a [Value] in [ConstantInt]
     * format
     *
     * @see LLVM.LLVMAlignOf
     */
    public fun alignOf(): ConstantInt {
        val ref = LLVM.LLVMAlignOf(ref)

        return ConstantInt(ref)
    }

    /**
     * Create a struct of this type and initialize it with the provided [values]
     *
     * The types of the provided [values] must match the types of this struct
     * type.
     *
     * @see LLVM.LLVMConstNamedStruct
     */
    public fun getConstant(vararg values: Value): ConstantStruct {
        val ptr = values.map { it.ref }.toPointerPointer()
        val ref = LLVM.LLVMConstNamedStruct(ref, ptr, values.size)

        ptr.deallocate()

        return ConstantStruct(ref)
    }
}

package dev.supergrecko.vexe.llvm.ir.types

import dev.supergrecko.vexe.llvm.internal.util.fromLLVMBool
import dev.supergrecko.vexe.llvm.internal.util.map
import dev.supergrecko.vexe.llvm.internal.util.toLLVMBool
import dev.supergrecko.vexe.llvm.ir.Context
import dev.supergrecko.vexe.llvm.ir.Type
import dev.supergrecko.vexe.llvm.ir.types.traits.CompositeType
import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM

public class StructType internal constructor() : Type(),
    CompositeType {
    public constructor(llvmRef: LLVMTypeRef) : this() {
        ref = llvmRef
    }

    //region Core::Types::StructureTypes
    /**
     * Create a structure types
     *
     * This method creates a structure types inside the given [ctx]. Do not that
     * this constructor cannot produce opaque types, use the secondary
     * constructor accepting a [String] for this.
     *
     * The struct body will be the types provided in [types].
     */
    public constructor(
        types: List<Type>,
        packed: Boolean,
        ctx: Context = Context.getGlobalContext()
    ) : this() {
        val arr = ArrayList(types.map { it.ref }).toTypedArray()

        ref =
            LLVM.LLVMStructTypeInContext(ctx.ref, PointerPointer(*arr), arr.size, packed.toLLVMBool())
    }

    /**
     * Create an opaque struct types
     *
     * This will create an opaque struct (a struct without a body, like C
     * forward declaration) with the given [name].
     *
     * You will be able to use [setBody] to assign a body to the opaque struct.
     */
    public constructor(
        name: String,
        ctx: Context = Context.getGlobalContext()
    ) : this() {
        ref = LLVM.LLVMStructCreateNamed(ctx.ref, name)
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
    public fun setBody(elementTypes: List<Type>, packed: Boolean) {
        require(isOpaque()) { "Cannot set body of non-opaque struct" }

        val types = elementTypes.map { it.ref }
        val array = ArrayList(types).toTypedArray()
        val ptr = PointerPointer(*array)

        LLVM.LLVMStructSetBody(ref, ptr, array.size, packed.toLLVMBool())
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

        return dest.map { Type(it) }
    }
    //endregion Core::Types::StructureTypes
}

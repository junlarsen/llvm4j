package dev.supergrecko.kllvm.core.types

import dev.supergrecko.kllvm.core.typedefs.Type
import dev.supergrecko.kllvm.utils.iterateIntoType
import dev.supergrecko.kllvm.utils.toBoolean
import dev.supergrecko.kllvm.utils.toInt
import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM

public class StructType(llvmType: LLVMTypeRef) : Type(llvmType) {
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
}
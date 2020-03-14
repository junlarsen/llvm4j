package dev.supergrecko.kllvm.core.type

import dev.supergrecko.kllvm.utils.getAll
import dev.supergrecko.kllvm.utils.toBoolean
import dev.supergrecko.kllvm.utils.toInt
import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM

public class LLVMStructureType internal constructor(
        llvmType: LLVMTypeRef
) : LLVMType(llvmType) {
    public fun setBody(elementTypes: List<LLVMType>, packed: Boolean) {
        val types = elementTypes.map { it.llvmType }
        val array = ArrayList(types).toTypedArray()
        val ptr = PointerPointer(*array)

        LLVM.LLVMStructSetBody(llvmType, ptr, array.size, packed.toInt())
    }

    public fun getElementTypeCount(): Int {
        return LLVM.LLVMCountStructElementTypes(llvmType)
    }

    public fun getType(index: Int): LLVMType? {
        // TODO: Make this access safe as we can calculate the capacity of the array
        val res = LLVM.LLVMStructGetTypeAtIndex(llvmType, index)

        return LLVMType(res)
    }

    public fun isPacked(): Boolean {
        return LLVM.LLVMIsPackedStruct(llvmType).toBoolean()
    }

    public fun isOpaque(): Boolean {
        return LLVM.LLVMIsOpaqueStruct(llvmType).toBoolean()
    }

    public fun isLiteral(): Boolean {
        return LLVM.LLVMIsLiteralStruct(llvmType).toBoolean()
    }

    public fun getName(): String? {
        val name = LLVM.LLVMGetStructName(llvmType)

        return if (name.isNull) {
            null
        } else {
            name.string
        }
    }

    public fun getElementTypes(): List<LLVMType> {
        val dest = PointerPointer<LLVMTypeRef>()
        LLVM.LLVMGetStructElementTypes(llvmType, dest)

        return dest.getAll().map { LLVMType(it) }
    }
}

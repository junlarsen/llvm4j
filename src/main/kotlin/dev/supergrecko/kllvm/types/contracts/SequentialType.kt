package dev.supergrecko.kllvm.types.contracts

import dev.supergrecko.kllvm.contracts.ContainsReference
import dev.supergrecko.kllvm.types.Type
import dev.supergrecko.kllvm.utils.iterateIntoType
import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM

public interface SequentialType : CompositeType, ContainsReference<LLVMTypeRef> {
    /**
     * Returns type's subtypes
     */
    public open fun getSubtypes(): List<Type> {
        val dest = PointerPointer<LLVMTypeRef>(getElementCount().toLong())
        LLVM.LLVMGetSubtypes(ref, dest)

        return dest.iterateIntoType { Type(it) }
    }

    /**
     * Obtain the type of elements within a sequential type
     */
    public open fun getElementType(): Type {
        val type = LLVM.LLVMGetElementType(ref)

        return Type(type)
    }
}

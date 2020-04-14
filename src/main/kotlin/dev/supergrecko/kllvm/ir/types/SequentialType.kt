package dev.supergrecko.kllvm.ir.types

import dev.supergrecko.kllvm.internal.contracts.ContainsReference
import dev.supergrecko.kllvm.internal.util.iterateIntoType
import dev.supergrecko.kllvm.ir.Type
import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM

public interface SequentialType : CompositeType, ContainsReference<LLVMTypeRef> {
    /**
     * Returns types's subtypes
     */
    public open fun getSubtypes(): List<Type> {
        val dest = PointerPointer<LLVMTypeRef>(getElementCount().toLong())
        LLVM.LLVMGetSubtypes(ref, dest)

        return dest.iterateIntoType { Type(it) }
    }

    /**
     * Obtain the types of elements within a sequential types
     */
    public open fun getElementType(): Type {
        val type = LLVM.LLVMGetElementType(ref)

        return Type(type)
    }
}

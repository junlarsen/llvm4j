package dev.supergrecko.kllvm.ir.types

import dev.supergrecko.kllvm.internal.contracts.ContainsReference
import dev.supergrecko.kllvm.internal.util.map
import dev.supergrecko.kllvm.ir.Type
import org.bytedeco.javacpp.Pointer
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

        val res = mutableListOf<LLVMTypeRef>()

        for (i in 0 until dest.capacity()) {
            res += dest.get(LLVMTypeRef::class.java, i)
        }

        return res.map { Type(it) }
    }

    /**
     * Obtain the types of elements within a sequential types
     */
    public open fun getElementType(): Type {
        val type = LLVM.LLVMGetElementType(ref)

        return Type(type)
    }
}

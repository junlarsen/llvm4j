package io.vexelabs.bitbuilder.llvm.ir.types.traits

import io.vexelabs.bitbuilder.llvm.internal.contracts.ContainsReference
import io.vexelabs.bitbuilder.llvm.ir.Type
import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM

public interface SequentialType :
    CompositeType,
    ContainsReference<LLVMTypeRef> {
    /**
     * Returns types's subtypes
     *
     * @see LLVM.LLVMGetSubtypes
     */
    public fun getSubtypes(): List<Type> {
        val dest = PointerPointer<LLVMTypeRef>(getElementCount().toLong())
        LLVM.LLVMGetSubtypes(ref, dest)

        val res = mutableListOf<LLVMTypeRef>()

        for (i in 0 until dest.capacity()) {
            res += LLVMTypeRef(dest.get(i))
        }

        return res.map { Type(it) }
    }

    /**
     * Obtain the types of elements within a sequential types
     *
     * @see LLVM.LLVMGetElementType
     */
    public fun getElementType(): Type {
        val type = LLVM.LLVMGetElementType(ref)

        return Type(type)
    }
}

package dev.supergrecko.kllvm.types

import dev.supergrecko.kllvm.internal.contracts.ContainsReference
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM

public interface CompositeType : ContainsReference<LLVMTypeRef> {
    /**
     * Returns the amount of elements contained in this type
     */
    public open fun getElementCount(): Int {
        return LLVM.LLVMGetNumContainedTypes(ref)
    }
}
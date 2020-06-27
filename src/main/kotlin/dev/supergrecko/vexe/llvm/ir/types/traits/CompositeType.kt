package dev.supergrecko.vexe.llvm.ir.types.traits

import dev.supergrecko.vexe.llvm.internal.contracts.ContainsReference
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM

public interface CompositeType : ContainsReference<LLVMTypeRef> {
    /**
     * Returns the amount of elements contained in this types
     *
     * @see LLVM.LLVMGetNumContainedTypes
     */
    public open fun getElementCount(): Int {
        return LLVM.LLVMGetNumContainedTypes(ref)
    }
}

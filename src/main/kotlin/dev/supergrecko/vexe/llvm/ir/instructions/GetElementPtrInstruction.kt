package dev.supergrecko.vexe.llvm.ir.instructions

import dev.supergrecko.vexe.llvm.internal.util.fromLLVMBool
import dev.supergrecko.vexe.llvm.internal.util.toLLVMBool
import dev.supergrecko.vexe.llvm.ir.Instruction
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public class GetElementPtrInstruction internal constructor() :
    Instruction() {
    public constructor(llvmRef: LLVMValueRef) : this() {
        ref = llvmRef
    }

    //region Core::Instructions::GEPs
    /**
     * Check whether this GEP is in bounds
     *
     * @see LLVM.LLVMIsInBounds
     */
    public fun isInBounds(): Boolean {
        return LLVM.LLVMIsInBounds(ref).fromLLVMBool()
    }

    /**
     * Set this GEP in or out of bounds
     *
     * @see LLVM.LLVMSetIsInBounds
     */
    public fun setInBounds(inBounds: Boolean) {
        LLVM.LLVMSetIsInBounds(ref, inBounds.toLLVMBool())
    }
    //endregion Core::Instructions::GEPs

    //region Core::Instructions::InsertValue
    /**
     * Obtain the number of indices
     *
     * @see LLVM.LLVMGetNumIndices
     */
    public fun getIndicesCount(): Int {
        return LLVM.LLVMGetNumIndices(ref)
    }
    //endregion Core::Instructions::InsertValue
}

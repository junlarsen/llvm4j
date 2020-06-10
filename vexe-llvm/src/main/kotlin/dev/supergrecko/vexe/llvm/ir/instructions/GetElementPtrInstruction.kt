package dev.supergrecko.vexe.llvm.ir.instructions

import dev.supergrecko.vexe.llvm.internal.util.fromLLVMBool
import dev.supergrecko.vexe.llvm.internal.util.toLLVMBool
import dev.supergrecko.vexe.llvm.ir.Instruction
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public class GetElementPtrInstruction internal constructor() :
    Instruction() {
    public constructor(llvmValue: LLVMValueRef) : this() {
        ref = llvmValue
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
}

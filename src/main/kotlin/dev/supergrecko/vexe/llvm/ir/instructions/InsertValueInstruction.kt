package dev.supergrecko.vexe.llvm.ir.instructions

import dev.supergrecko.vexe.llvm.ir.Instruction
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public class InsertValueInstruction internal constructor() : Instruction() {
    public constructor(llvmValue: LLVMValueRef) : this() {
        ref = llvmValue
    }

    //region Core::Instructions::InsertValue
    /**
     * Obtain the number of indices
     *
     * @see LLVM.LLVMGetNumIndices
     */
    public fun getIndicesCount(): Int {
        return LLVM.LLVMGetNumIndices(ref)
    }

    /**
     * Get all the indices
     *
     * @see LLVM.LLVMGetIndices
     */
    public fun getIndices(): List<Int> {
        val ptr = LLVM.LLVMGetIndices(ref)
        val res = mutableListOf<Int>()

        for (i in 0..getIndicesCount()) {
            res.add(ptr.get(i.toLong()))
        }

        return res
    }
    //endregion Core::Instructions::InsertValue
}

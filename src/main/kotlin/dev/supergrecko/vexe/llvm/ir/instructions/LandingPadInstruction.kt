package dev.supergrecko.vexe.llvm.ir.instructions

import dev.supergrecko.vexe.llvm.internal.util.fromLLVMBool
import dev.supergrecko.vexe.llvm.internal.util.toLLVMBool
import dev.supergrecko.vexe.llvm.internal.util.wrap
import dev.supergrecko.vexe.llvm.ir.Instruction
import dev.supergrecko.vexe.llvm.ir.Value
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public class LandingPadInstruction internal constructor() : Instruction() {
    public constructor(llvmRef: LLVMValueRef) : this() {
        ref = llvmRef
    }

    //region InstructionBuilders
    /**
     * Get the amount of clauses appended
     *
     * @see LLVM.LLVMGetNumClauses
     */
    public fun getClauseCount(): Int {
        return LLVM.LLVMGetNumClauses(ref)
    }

    /**
     * Get the clause at [index]
     *
     * @see LLVM.LLVMGetClause
     */
    public fun getClause(index: Int): Value? {
        val clause = LLVM.LLVMGetClause(ref, index)

        return wrap(clause) { Value(it) }
    }

    /**
     * Add a clause
     *
     * @see LLVM.LLVMAddClause
     */
    public fun addClause(value: Value) {
        LLVM.LLVMAddClause(ref, value.ref)
    }

    /**
     * Will this landing pad clean up?
     *
     * If a landing pad doesn't match any of the clauses upon an exception,
     * then unwinding continues further up the call stack
     *
     * @see LLVM.LLVMIsCleanup
     */
    public fun isCleanup(): Boolean {
        return LLVM.LLVMIsCleanup(ref).fromLLVMBool()
    }

    /**
     * Set whether this landing pad will clean up
     *
     * @see LLVM.LLVMSetCleanup
     */
    public fun setCleanup(isCleanup: Boolean) {
        LLVM.LLVMSetCleanup(ref, isCleanup.toLLVMBool())
    }
    //endregion InstructionBuilders
}

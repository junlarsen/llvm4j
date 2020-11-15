package io.vexelabs.bitbuilder.llvm.ir.instructions

import io.vexelabs.bitbuilder.internal.fromLLVMBool
import io.vexelabs.bitbuilder.internal.toLLVMBool
import io.vexelabs.bitbuilder.llvm.ir.Instruction
import io.vexelabs.bitbuilder.llvm.ir.Value
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public class LandingPadInstruction internal constructor() : Instruction() {
    public constructor(llvmRef: LLVMValueRef) : this() {
        ref = llvmRef
    }

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

        return clause?.let { Value(it) }
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
}

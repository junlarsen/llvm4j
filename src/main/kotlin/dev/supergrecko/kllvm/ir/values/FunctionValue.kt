package dev.supergrecko.kllvm.ir.values

import dev.supergrecko.kllvm.internal.util.toBoolean
import dev.supergrecko.kllvm.ir.BasicBlock
import dev.supergrecko.kllvm.ir.Value
import dev.supergrecko.kllvm.support.VerifierFailureAction
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public class FunctionValue internal constructor() : Value() {
    /**
     * Construct a new Type from an LLVM pointer reference
     */
    public constructor(llvmValue: LLVMValueRef) : this() {
        ref = llvmValue
    }

    fun appendBasicBlock(name: String): BasicBlock {
        return BasicBlock(LLVM.LLVMAppendBasicBlock(ref, name))
    }

    /**
     * Get a parameter from this function at [index]
     *
     * TODO: Maybe throw an index out of bounds exception here
     *   in case a param isn't found? Or maybe return nullable value? Up for
     *   investigation
     */
    fun getParam(index: Int): Value {
        val value = LLVM.LLVMGetParam(ref, index)

        return Value(value)
    }

    //region Analysis
    /**
     * Verify that the function structure is valid
     *
     * As opposed to the LLVM implementation, this returns true if the function
     * is valid.
     */
    public fun verify(action: VerifierFailureAction): Boolean {
        // LLVM Source says:
        // > Note that this function's return value is inverted from what you would
        // > expect of a function called "verify".
        // Thus we invert it again ...
        return !LLVM.LLVMVerifyFunction(ref, action.value).toBoolean()
    }

    /**
     * View the function structure
     *
     * From the LLVM Source:
     *
     * This function is meant for use from the debugger. You can just say
     * 'call F->viewCFG()' and a ghost view window should pop up from the
     * program, displaying the CFG of the current function. This depends on
     * there being a 'dot' and 'gv' program in your path.
     *
     * If [hideBasicBlocks] is true then [LLVM.LLVMViewFunctionCFGOnly] will be
     * used instead of [LLVM.LLVMViewFunctionCFG]
     *
     * TODO: Does this even work via JNI??
     */
    public fun viewConfiguration(hideBasicBlocks: Boolean) {
        if (hideBasicBlocks) {
            LLVM.LLVMViewFunctionCFGOnly(ref)
        } else {
            LLVM.LLVMViewFunctionCFG(ref)
        }
    }
    //endregion Analysis
}

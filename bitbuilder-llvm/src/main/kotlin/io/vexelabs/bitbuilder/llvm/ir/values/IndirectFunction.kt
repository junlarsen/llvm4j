package io.vexelabs.bitbuilder.llvm.ir.values

import io.vexelabs.bitbuilder.llvm.ir.Module
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public class IndirectFunction internal constructor() : FunctionValue() {
    public constructor(llvmRef: LLVMValueRef) : this() {
        ref = llvmRef
    }

    //region Core::Values::Constants::FunctionValues::IndirectFunctions
    /**
     * Convert a regular function to an indirect one
     *
     * Can be used for assigning [FunctionValue.getIndirectResolver]
     */
    public constructor(function: FunctionValue) : this() {
        ref = function.ref
    }

    /**
     * Remove a global indirect function from its parent module and delete it.
     *
     * @see LLVM.LLVMEraseGlobalIFunc
     */
    public override fun delete() {
        LLVM.LLVMEraseGlobalIFunc(ref)
    }

    /**
     * Remove a global indirect function from its parent module.
     *
     * This unlinks the global indirect function from its containing module but
     * keeps it alive.
     *
     * @see LLVM.LLVMRemoveGlobalIFunc
     */
    public fun remove() {
        LLVM.LLVMRemoveGlobalIFunc(ref)
    }

    public companion object {
        @JvmStatic
        public fun fromModule(module: Module, name: String): IndirectFunction {
            val fn = LLVM.LLVMGetNamedGlobalIFunc(
                module.ref,
                name,
                name.length.toLong()
            )

            return if (fn == null) {
                throw IllegalArgumentException(
                    "Function $name could not be found in module."
                )
            } else {
                IndirectFunction(fn)
            }
        }
    }
    //endregion Core::Values::Constants::FunctionValues::IndirectFunctions
}

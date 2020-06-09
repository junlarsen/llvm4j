package dev.supergrecko.vexe.llvm.ir.values

import dev.supergrecko.vexe.llvm.ir.Module
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

/**
 * Class corresponding to llvm::GlobalIFunc
 */
public class IndirectFunction internal constructor() : FunctionValue() {
    /**
     * Construct a new Type from an LLVM pointer reference
     */
    public constructor(llvmRef: LLVMValueRef) : this() {
        ref = llvmRef
    }

    /**
     * Convert a regular function to an indirect one
     *
     * Can be used for assigning [FunctionValue.getIndirectResolver]
     */
    public constructor(function: FunctionValue) : this() {
        ref = function.ref
    }

    //region Core::Values::Constants::FunctionValues::IndirectFunctions
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
    //endregion Core::Values::Constants::FunctionValues::IndirectFunctions

    public companion object {
        //region Core::Values::Constants::FunctionValues::IndirectFunctions
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
        //endregion Core::Values::Constants::FunctionValues::IndirectFunctions
    }
}

package dev.supergrecko.kllvm.ir.values

import dev.supergrecko.kllvm.internal.contracts.ContainsReference
import org.bytedeco.javacpp.IntPointer
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

/**
 * This is a sum type for an llvm::Instruction | llvm::GlobalVariable and
 * llvm::Function
 */
public interface DebugLocationValue : ContainsReference<LLVMValueRef> {
    //region Core::Modules
    /**
     * Get the directory of the debug location for this value
     *
     * @see LLVM.LLVMGetDebugLocDirectory
     */
    public fun getDebugLocationDirectory(): String? {
        val length = IntPointer(0)

        return LLVM.LLVMGetDebugLocDirectory(ref, length)?.string
    }

    /**
     * Get the filename of the debug location for this value
     *
     * @see LLVM.LLVMGetDebugLocFilename
     */
    public fun getDebugLocationFilename(): String? {
        val length = IntPointer(0)

        return LLVM.LLVMGetDebugLocFilename(ref, length)?.string
    }

    /**
     * Get the line number of the debug location for this value
     *
     * @see LLVM.LLVMGetDebugLocLine
     */
    public fun getDebugLocationLine(): Int {
        return LLVM.LLVMGetDebugLocLine(ref)
    }

    /**
     * Get the column number of the debug location for this value
     *
     * @see LLVM.LLVMGetDebugLocColumn
     */
    public fun getDebugLocationColumn(): Int {
        return LLVM.LLVMGetDebugLocColumn(ref)
    }
    //endregion Core::Modules
}
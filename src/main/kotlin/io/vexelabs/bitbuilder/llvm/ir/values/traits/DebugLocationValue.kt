package io.vexelabs.bitbuilder.llvm.ir.values.traits

import io.vexelabs.bitbuilder.llvm.internal.contracts.ContainsReference
import io.vexelabs.bitbuilder.raii.resourceScope
import io.vexelabs.bitbuilder.raii.toResource
import org.bytedeco.javacpp.IntPointer
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

/**
 * This is a sum type for an llvm::Instruction | llvm::GlobalVariable and
 * llvm::Function
 */
public interface DebugLocationValue : ContainsReference<LLVMValueRef> {
    /**
     * Get the directory of the debug location for this value
     *
     * @see LLVM.LLVMGetDebugLocDirectory
     */
    public fun getDebugLocationDirectory(): String? {
        val len = IntPointer(1).toResource()

        return resourceScope(len) {
            val ptr = LLVM.LLVMGetDebugLocDirectory(ref, it)
            val contents = ptr?.string

            ptr.deallocate()

            return@resourceScope contents
        }
    }

    /**
     * Get the filename of the debug location for this value
     *
     * @see LLVM.LLVMGetDebugLocFilename
     */
    public fun getDebugLocationFilename(): String? {
        val len = IntPointer(1).toResource()

        return resourceScope(len) {
            val ptr = LLVM.LLVMGetDebugLocFilename(ref, it)
            val contents = ptr?.string

            ptr.deallocate()

            return@resourceScope contents
        }
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
}

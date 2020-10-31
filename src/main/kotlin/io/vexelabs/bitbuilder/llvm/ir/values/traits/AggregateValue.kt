package io.vexelabs.bitbuilder.llvm.ir.values.traits

import io.vexelabs.bitbuilder.internal.resourceScope
import io.vexelabs.bitbuilder.internal.toPointerPointer
import io.vexelabs.bitbuilder.internal.toResource
import io.vexelabs.bitbuilder.llvm.internal.contracts.ContainsReference
import io.vexelabs.bitbuilder.llvm.ir.Value
import io.vexelabs.bitbuilder.llvm.ir.values.constants.ConstantInt
import org.bytedeco.javacpp.IntPointer
import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public interface AggregateValue : ContainsReference<LLVMValueRef> {
    /**
     * Access an address of a sub-element of an aggregate data structure
     * (arrays or structures)
     *
     * [See](https://llvm.org/docs/LangRef.html#getelementptr-instruction)
     * [See](https://llvm.org/docs/GetElementPtr.html)
     *
     * @see LLVM.LLVMConstGEP
     * @see LLVM.LLVMConstInBoundsGEP
     */
    public fun getGEP(inbounds: Boolean, indices: List<ConstantInt>): Value {
        val ptr = indices.map { it.ref }.toPointerPointer()
        val ref = if (inbounds) {
            LLVM.LLVMConstInBoundsGEP(ref, ptr, indices.size)
        } else {
            LLVM.LLVMConstGEP(ref, ptr, indices.size)
        }

        return Value(ref).also {
            ptr.deallocate()
        }
    }

    /**
     * Extract the value of a member field from an aggregate value
     *
     * This instruction is similar to GEP.
     *
     * The major differences to getelementptr indexing are:
     *
     * - Since the value being indexed is not a pointer, the first index is
     *   omitted and assumed to be zero.
     * - At least one index must be specified.
     * - Not only struct indices but also array indices must be in bounds.
     *
     * @see LLVM.LLVMConstExtractValue
     */
    public fun getExtractValue(indices: List<Int>): Value {
        val ptr = IntPointer(*indices.toTypedArray().toIntArray()).toResource()

        return resourceScope(ptr) {
            val ref = LLVM.LLVMConstExtractValue(ref, it, indices.size)

            return@resourceScope Value(ref)
        }
    }

    /**
     * Insert the value into an aggregate value
     *
     * This instruction uses the same navigation system as [getExtractValue]
     *
     * @see LLVM.LLVMConstInsertValue
     */
    public fun getInsertValue(value: Value, indices: List<Int>): Value {
        val ptr = IntPointer(*indices.toTypedArray().toIntArray()).toResource()

        return resourceScope(ptr) {
            val ref = LLVM.LLVMConstInsertValue(ref, value.ref, it, indices.size)

            return@resourceScope Value(ref)
        }
    }
}

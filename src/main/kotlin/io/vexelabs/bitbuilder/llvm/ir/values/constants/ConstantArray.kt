package io.vexelabs.bitbuilder.llvm.ir.values.constants

import io.vexelabs.bitbuilder.internal.fromLLVMBool
import io.vexelabs.bitbuilder.internal.resourceScope
import io.vexelabs.bitbuilder.internal.toLLVMBool
import io.vexelabs.bitbuilder.internal.toPointerPointer
import io.vexelabs.bitbuilder.internal.toResource
import io.vexelabs.bitbuilder.llvm.ir.Context
import io.vexelabs.bitbuilder.llvm.ir.Type
import io.vexelabs.bitbuilder.llvm.ir.Value
import io.vexelabs.bitbuilder.llvm.ir.values.ConstantValue
import io.vexelabs.bitbuilder.llvm.ir.values.traits.AggregateValue
import io.vexelabs.bitbuilder.llvm.ir.values.traits.CompositeValue
import org.bytedeco.javacpp.SizeTPointer
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public class ConstantArray internal constructor() :
    ConstantValue(),
    AggregateValue,
    CompositeValue {
    public constructor(llvmRef: LLVMValueRef) : this() {
        ref = llvmRef
    }

    /**
     * Determine whether this is an array of i8's
     *
     * @see LLVM.LLVMIsConstantString
     */
    public fun isConstantString(): Boolean {
        return LLVM.LLVMIsConstantString(ref).fromLLVMBool()
    }

    /**
     * Get the string for this array if it's an array of i8
     *
     * @see LLVM.LLVMGetAsString
     */
    public fun getAsString(): String {
        require(isConstantString())

        val len = SizeTPointer(1).toResource()

        return resourceScope(len) {
            val ptr = LLVM.LLVMGetAsString(ref, it)
            val contents = ptr.string

            ptr.deallocate()

            return@resourceScope contents
        }
    }

    public companion object {
        /**
         * Constructor to make an LLVM string
         *
         * A LLVM string is an array of i8's which contain the different
         * characters the string contains
         *
         * @see LLVM.LLVMConstStringInContext
         */
        @JvmStatic
        public fun fromString(
            value: String,
            context: Context,
            nullTerminate: Boolean
        ): ConstantArray {
            val ref = LLVM.LLVMConstStringInContext(
                context.ref,
                value,
                value.length,
                nullTerminate.toLLVMBool()
            )

            return ConstantArray(ref)
        }
    }
}

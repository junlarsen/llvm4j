package io.vexelabs.bitbuilder.llvm.ir.values.constants

import io.vexelabs.bitbuilder.llvm.internal.util.fromLLVMBool
import io.vexelabs.bitbuilder.llvm.internal.util.toLLVMBool
import io.vexelabs.bitbuilder.llvm.ir.Context
import io.vexelabs.bitbuilder.llvm.ir.Type
import io.vexelabs.bitbuilder.llvm.ir.Value
import io.vexelabs.bitbuilder.llvm.ir.values.ConstantValue
import io.vexelabs.bitbuilder.llvm.ir.values.traits.AggregateValue
import io.vexelabs.bitbuilder.llvm.ir.values.traits.CompositeValue
import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.javacpp.SizeTPointer
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public class ConstantArray internal constructor() : ConstantValue(),
    AggregateValue, CompositeValue {
    public constructor(llvmRef: LLVMValueRef) : this() {
        ref = llvmRef
    }

    /**
     * Create an array of values of a given [type]
     *
     * @see LLVM.LLVMConstArray
     */
    public constructor(type: Type, values: List<Value>) : this() {
        val ptr = PointerPointer(*values.map { it.ref }.toTypedArray())

        ref = LLVM.LLVMConstArray(type.ref, ptr, values.size)
    }

    /**
     * Constructor to make an LLVM string
     *
     * A LLVM string is an array of i8's which contain the different
     * characters the string contains
     */
    public constructor(
        content: String,
        nullTerminate: Boolean = true,
        context: Context = Context.getGlobalContext()
    ) : this() {
        ref = LLVM.LLVMConstStringInContext(
            context.ref,
            content,
            content.length,
            nullTerminate.toLLVMBool()
        )
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

        val len = SizeTPointer(0)
        val ptr = LLVM.LLVMGetAsString(ref, len)

        len.deallocate()

        return ptr.string
    }
}

package dev.supergrecko.kllvm.ir.values.constants

import dev.supergrecko.kllvm.internal.util.fromLLVMBool
import dev.supergrecko.kllvm.internal.util.toLLVMBool
import dev.supergrecko.kllvm.ir.Context
import dev.supergrecko.kllvm.ir.Value
import dev.supergrecko.kllvm.ir.values.AggregateValue
import dev.supergrecko.kllvm.ir.values.CompositeValue
import dev.supergrecko.kllvm.ir.values.ConstantValue
import org.bytedeco.javacpp.SizeTPointer
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public class ConstantArray internal constructor() : Value(), ConstantValue,
    AggregateValue, CompositeValue {
    /**
     * Construct a new Type from an LLVM pointer reference
     */
    public constructor(llvmValue: LLVMValueRef) : this() {
        ref = llvmValue
    }

    public constructor(
        content: String,
        nullTerminate: Boolean,
        context: Context = Context.getGlobalContext()
    ) : this() {
        ref = LLVM.LLVMConstStringInContext(
            context.ref,
            content,
            content.length,
            nullTerminate.toLLVMBool()
        )
    }

    //region Core::Values::Constants::CompositeConstants
    /**
     * Determine whether this is a constant string
     *
     * @see LLVM.LLVMIsConstantString
     */
    public fun isConstantString(): Boolean {
        return LLVM.LLVMIsConstantString(ref).fromLLVMBool()
    }

    /**
     * Get the array in a string representation
     *
     * @see LLVM.LLVMGetAsString
     */
    public fun getAsString(): String {
        require(isConstantString())

        val ptr = LLVM.LLVMGetAsString(ref, SizeTPointer(0))

        return ptr.string
    }
    //endregion Core::Values::Constants::CompositeConstants

    override fun toString(): String = getAsString()
}

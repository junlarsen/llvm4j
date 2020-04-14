package dev.supergrecko.kllvm.ir.values.constants

import dev.supergrecko.kllvm.internal.util.toBoolean
import dev.supergrecko.kllvm.internal.util.toInt
import dev.supergrecko.kllvm.ir.Value
import dev.supergrecko.kllvm.ir.values.Constant
import dev.supergrecko.kllvm.ir.Context
import org.bytedeco.javacpp.SizeTPointer
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public class ConstantArray internal constructor() : Value(), Constant {
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
            nullTerminate.toInt()
        )
    }

    //region Core::Values::Constants::CompositeConstants
    /**
     * Determine whether this is a constant string
     *
     * @see LLVM.LLVMIsConstantString
     */
    public fun isConstantString(): Boolean {
        return LLVM.LLVMIsConstantString(ref).toBoolean()
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

    /**
     * Get an element at specified [index] as a constant
     *
     * This is shared with [ConstantArray], [ConstantVector], [ConstantStruct]
     *
     * TODO: Move into shared contract
     *
     * @see LLVM.LLVMGetElementAsConstant
     */
    public fun getElementAsConstant(index: Int): Value {
        val value = LLVM.LLVMGetElementAsConstant(ref, index)

        return Value(value)
    }
    //endregion Core::Values::Constants::CompositeConstants
}

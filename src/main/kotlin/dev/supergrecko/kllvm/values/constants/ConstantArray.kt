package dev.supergrecko.kllvm.values.constants

import dev.supergrecko.kllvm.llvm.typedefs.Context
import dev.supergrecko.kllvm.internal.util.toBoolean
import dev.supergrecko.kllvm.internal.util.toInt
import dev.supergrecko.kllvm.values.Constant
import dev.supergrecko.kllvm.values.Value
import org.bytedeco.javacpp.SizeTPointer
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public class ConstantArray internal constructor() : Value(), Constant {
    public constructor(llvmValue: LLVMValueRef) : this() {
        ref = llvmValue
    }

    public constructor(content: String, nullTerminate: Boolean, context: Context = Context.getGlobalContext()) : this() {
        ref = LLVM.LLVMConstStringInContext(context.ref, content, content.length, nullTerminate.toInt())
    }

    //region Core::Values::Constants::CompositeConstants
    public fun isConstantString(): Boolean {
        return LLVM.LLVMIsConstantString(ref).toBoolean()
    }

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
     */
    public fun getElementAsConstant(index: Int): Value {
        val value = LLVM.LLVMGetElementAsConstant(ref, index)

        return Value(value)
    }
    //endregion Core::Values::Constants::CompositeConstants
}
package dev.supergrecko.kllvm.core.values

import dev.supergrecko.kllvm.annotations.Shared
import dev.supergrecko.kllvm.core.typedefs.Context
import dev.supergrecko.kllvm.core.typedefs.Value
import dev.supergrecko.kllvm.utils.toBoolean
import dev.supergrecko.kllvm.utils.toInt
import org.bytedeco.javacpp.SizeTPointer
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public class ArrayValue internal constructor() : Value() {
    public constructor(llvmValue: LLVMValueRef) : this() {
        ref = llvmValue
    }

    public constructor(value: Value) : this(value.ref)

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
     * This is shared with [ArrayValue], [VectorValue], [StructValue]
     */
    @Shared
    public fun getElementAsConstant(index: Int): Value {
        val value = LLVM.LLVMGetElementAsConstant(ref, index)

        return Value(value)
    }
    //endregion Core::Values::Constants::CompositeConstants
}
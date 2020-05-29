package dev.supergrecko.kllvm.unit.ir.values.constants

import dev.supergrecko.kllvm.unit.internal.util.fromLLVMBool
import dev.supergrecko.kllvm.unit.internal.util.toLLVMBool
import dev.supergrecko.kllvm.unit.ir.Context
import dev.supergrecko.kllvm.unit.ir.Type
import dev.supergrecko.kllvm.unit.ir.Value
import dev.supergrecko.kllvm.unit.ir.values.AggregateValue
import dev.supergrecko.kllvm.unit.ir.values.CompositeValue
import dev.supergrecko.kllvm.unit.ir.values.ConstantValue
import org.bytedeco.javacpp.PointerPointer
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

    public constructor(type: Type, values: List<Value>) : this() {
        val ptr = values.map { it.ref }.toTypedArray()

        ref = LLVM.LLVMConstArray(type.ref, PointerPointer(*ptr), ptr.size)
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

    //region Core::Values::Constants::CompositeConstants
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

        val ptr = LLVM.LLVMGetAsString(ref, SizeTPointer(0))

        return ptr.string
    }
    //endregion Core::Values::Constants::CompositeConstants
}

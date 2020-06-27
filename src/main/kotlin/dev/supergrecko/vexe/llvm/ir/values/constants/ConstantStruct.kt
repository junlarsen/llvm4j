package dev.supergrecko.vexe.llvm.ir.values.constants

import dev.supergrecko.vexe.llvm.internal.util.toLLVMBool
import dev.supergrecko.vexe.llvm.ir.Context
import dev.supergrecko.vexe.llvm.ir.Value
import dev.supergrecko.vexe.llvm.ir.types.StructType
import dev.supergrecko.vexe.llvm.ir.values.traits.AggregateValue
import dev.supergrecko.vexe.llvm.ir.values.traits.CompositeValue
import dev.supergrecko.vexe.llvm.ir.values.traits.ConstantValue
import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public class ConstantStruct internal constructor() : Value(),
    ConstantValue, AggregateValue, CompositeValue {
    public constructor(llvmRef: LLVMValueRef) : this() {
        ref = llvmRef
    }

    /**
     * Create a constant struct of a list of values
     *
     * @see LLVM.LLVMConstStructInContext
     */
    public constructor(
        values: List<Value>,
        packed: Boolean,
        context: Context = Context.getGlobalContext()
    ) : this() {
        val ptr = ArrayList(values.map { it.ref }).toTypedArray()

        ref = LLVM.LLVMConstStructInContext(
            context.ref,
            PointerPointer(*ptr),
            ptr.size,
            packed.toLLVMBool()
        )
    }

    /**
     * Create a struct of a [type] and initialize it with the provided [values]
     *
     * @see LLVM.LLVMConstNamedStruct
     */
    public constructor(type: StructType, values: List<Value>) : this() {
        val ptr = ArrayList(values.map { it.ref }).toTypedArray()

        ref = LLVM.LLVMConstNamedStruct(
            type.ref,
            PointerPointer(*ptr),
            ptr.size
        )
    }
}

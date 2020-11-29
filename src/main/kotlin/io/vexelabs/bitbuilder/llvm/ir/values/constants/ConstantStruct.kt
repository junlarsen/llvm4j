package io.vexelabs.bitbuilder.llvm.ir.values.constants

import io.vexelabs.bitbuilder.internal.toLLVMBool
import io.vexelabs.bitbuilder.internal.toPointerPointer
import io.vexelabs.bitbuilder.llvm.ir.Context
import io.vexelabs.bitbuilder.llvm.ir.Value
import io.vexelabs.bitbuilder.llvm.ir.types.StructType
import io.vexelabs.bitbuilder.llvm.ir.values.ConstantValue
import io.vexelabs.bitbuilder.llvm.ir.values.traits.AggregateValue
import io.vexelabs.bitbuilder.llvm.ir.values.traits.CompositeValue
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public class ConstantStruct internal constructor() :
    ConstantValue(),
    AggregateValue,
    CompositeValue {
    public constructor(llvmRef: LLVMValueRef) : this() {
        ref = llvmRef
    }
}

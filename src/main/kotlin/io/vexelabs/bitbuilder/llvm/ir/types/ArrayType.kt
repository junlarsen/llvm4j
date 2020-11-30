package io.vexelabs.bitbuilder.llvm.ir.types

import io.vexelabs.bitbuilder.llvm.ir.Type
import io.vexelabs.bitbuilder.llvm.ir.types.traits.CompositeType
import io.vexelabs.bitbuilder.llvm.ir.types.traits.SequentialType
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM

public class ArrayType internal constructor() :
    Type(),
    CompositeType,
    SequentialType {
    public constructor(llvmRef: LLVMTypeRef) : this() {
        ref = llvmRef
    }

    /**
     * Get the amount of elements in this array
     *
     * @see LLVM.LLVMGetArrayLength
     */
    public override fun getElementCount(): Int {
        return LLVM.LLVMGetArrayLength(ref)
    }
}

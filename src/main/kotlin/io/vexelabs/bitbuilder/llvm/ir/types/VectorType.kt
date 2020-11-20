package io.vexelabs.bitbuilder.llvm.ir.types

import io.vexelabs.bitbuilder.llvm.ir.Type
import io.vexelabs.bitbuilder.llvm.ir.types.traits.CompositeType
import io.vexelabs.bitbuilder.llvm.ir.types.traits.SequentialType
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM

public class VectorType internal constructor() :
    Type(),
    CompositeType,
    SequentialType {
    public constructor(llvmType: LLVMTypeRef) : this() {
        ref = llvmType
    }

    /**
     * Get the amount of elements this vector supports
     *
     * @see LLVM.LLVMGetVectorSize
     */
    public override fun getElementCount(): Int {
        return LLVM.LLVMGetVectorSize(ref)
    }
}

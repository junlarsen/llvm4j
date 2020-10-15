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
     * Create a vector types
     *
     * Constructs a vector types of types [type] with size [size].
     */
    public constructor(type: Type, size: Int) : this() {
        require(size >= 0) { "Cannot make vector of negative size" }

        ref = LLVM.LLVMVectorType(type.ref, size)
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

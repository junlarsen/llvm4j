package dev.supergrecko.vexe.llvm.ir.types

import dev.supergrecko.vexe.llvm.ir.Type
import dev.supergrecko.vexe.llvm.ir.types.traits.CompositeType
import dev.supergrecko.vexe.llvm.ir.types.traits.SequentialType
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM

public class VectorType internal constructor() : Type(), CompositeType,
    SequentialType {
    public constructor(llvmType: LLVMTypeRef) : this() {
        ref = llvmType
    }

    //region Core::Types::SequentialTypes
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
    //endregion Core::Types::SequentialTypes
}

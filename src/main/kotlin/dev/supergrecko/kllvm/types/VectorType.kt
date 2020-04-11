package dev.supergrecko.kllvm.types

import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM

public class VectorType internal constructor() : Type(),
    CompositeType,
    SequentialType {
    public constructor(llvmType: LLVMTypeRef) : this() {
        ref = llvmType
        requireKind(TypeKind.Vector)
    }

    /**
     * Create a vector type
     *
     * Constructs a vector type of type [ty] with size [size].
     */
    public constructor(type: Type, size: Int) : this() {
        require(size >= 0) { "Cannot make vector of negative size" }

        ref = LLVM.LLVMVectorType(type.ref, size)
    }

    public override fun getElementCount(): Int {
        return LLVM.LLVMGetVectorSize(ref)
    }
}

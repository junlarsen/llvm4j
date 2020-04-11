package dev.supergrecko.kllvm.types

import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM

public class ArrayType internal constructor() : Type(),
    CompositeType,
    SequentialType {
    public constructor(llvmType: LLVMTypeRef) : this() {
        ref = llvmType
        requireKind(TypeKind.Array)
    }

    /**
     * Create an array type
     *
     * Constructs an array of type [type] with size [size].
     */
    public constructor(type: Type, size: Int) : this() {
        require(size >= 0) { "Cannot make array of negative size" }

        ref = LLVM.LLVMArrayType(type.ref, size)
    }

    public override fun getElementCount(): Int {
        return LLVM.LLVMGetArrayLength(ref)
    }
}

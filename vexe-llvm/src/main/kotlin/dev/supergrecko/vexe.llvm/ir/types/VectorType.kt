package dev.supergrecko.vexe.llvm.ir.types

import dev.supergrecko.vexe.llvm.ir.Type
import dev.supergrecko.vexe.llvm.ir.TypeKind
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM

public class VectorType internal constructor() : Type(),
    CompositeType,
    SequentialType {
    /**
     * Construct a new Type from an LLVM pointer reference
     */
    public constructor(llvmType: LLVMTypeRef) : this() {
        ref = llvmType
        requireKind(TypeKind.Vector)
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

    public override fun getElementCount(): Int {
        return LLVM.LLVMGetVectorSize(ref)
    }
}

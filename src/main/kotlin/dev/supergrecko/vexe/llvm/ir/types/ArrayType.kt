package dev.supergrecko.vexe.llvm.ir.types

import dev.supergrecko.vexe.llvm.ir.Type
import dev.supergrecko.vexe.llvm.ir.types.traits.CompositeType
import dev.supergrecko.vexe.llvm.ir.types.traits.SequentialType
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM

public class ArrayType internal constructor() : Type(),
    CompositeType,
    SequentialType {
    public constructor(llvmRef: LLVMTypeRef) : this() {
        ref = llvmRef
    }

    //region Core::Types::SequentialTypes
    /**
     * Create an array types
     *
     * Constructs an array of types [type] with size [size].
     */
    public constructor(type: Type, size: Int) : this() {
        require(size >= 0) { "Cannot make array of negative size" }

        ref = LLVM.LLVMArrayType(type.ref, size)
    }

    /**
     * Get the amount of elements in this array
     *
     * @see LLVM.LLVMGetArrayLength
     */
    public override fun getElementCount(): Int {
        return LLVM.LLVMGetArrayLength(ref)
    }
    //endregion Core::Types::SequentialTypes
}

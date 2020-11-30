package io.vexelabs.bitbuilder.llvm.ir.types

import io.vexelabs.bitbuilder.llvm.ir.Type
import io.vexelabs.bitbuilder.llvm.ir.types.traits.CompositeType
import io.vexelabs.bitbuilder.llvm.ir.types.traits.SequentialType
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM

public class PointerType internal constructor() :
    Type(),
    CompositeType,
    SequentialType {
    public constructor(llvmRef: LLVMTypeRef) : this() {
        ref = llvmRef
    }

    /**
     * Get the address of this pointer
     *
     * @see LLVM.LLVMGetPointerAddressSpace
     */
    public fun getAddressSpace(): Int {
        return LLVM.LLVMGetPointerAddressSpace(ref)
    }
}

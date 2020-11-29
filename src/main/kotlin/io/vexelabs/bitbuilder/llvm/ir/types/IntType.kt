package io.vexelabs.bitbuilder.llvm.ir.types

import io.vexelabs.bitbuilder.internal.toLLVMBool
import io.vexelabs.bitbuilder.llvm.ir.Type
import io.vexelabs.bitbuilder.llvm.ir.values.constants.ConstantInt
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM

public class IntType internal constructor() : Type() {
    public constructor(llvmRef: LLVMTypeRef) : this() {
        ref = llvmRef
    }

    /**
     * Create a new integer value
     *
     * This creates a new integer of this type with [value]. You can decide if
     * this is unsigned with [unsigned].
     *
     * @see LLVM.LLVMConstInt
     */
    public fun getConstant(
        value: Long,
        unsigned: Boolean = false
    ): ConstantInt {
        val ref = LLVM.LLVMConstInt(ref, value, (!unsigned).toLLVMBool())

        return ConstantInt(ref)
    }

    /**N
     * Create a new integer value
     *
     * This creates a new integer of this type with [value]. You can decide if
     * this is unsigned with [unsigned].
     *
     * @see LLVM.LLVMConstInt
     */
    public fun getConstant(value: Int, unsigned: Boolean = false): ConstantInt {
        return getConstant(value.toLong(), unsigned)
    }

    /**
     * Get the size of the integer
     *
     * @see LLVM.LLVMGetIntTypeWidth
     */
    public fun getTypeWidth(): Int {
        return LLVM.LLVMGetIntTypeWidth(ref)
    }

    /**
     * Get a constant all 1's for this integer type
     *
     * @see LLVM.LLVMConstAllOnes
     */
    public fun getConstantAllOnes(): ConstantInt {
        val v = LLVM.LLVMConstAllOnes(ref)

        return ConstantInt(v)
    }
}

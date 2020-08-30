package io.vexelabs.bitbuilder.llvm.ir.values.constants

import io.vexelabs.bitbuilder.llvm.ir.Type
import io.vexelabs.bitbuilder.llvm.ir.types.IntType
import io.vexelabs.bitbuilder.llvm.ir.types.PointerType
import io.vexelabs.bitbuilder.llvm.ir.values.ConstantValue
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public class ConstantPointer internal constructor() : ConstantValue() {
    public constructor(llvmRef: LLVMValueRef) : this() {
        ref = llvmRef
    }

    // core Core::Values::Constants::ConstantExpressions
    /**
     * Conversion to integer
     *
     * @see LLVM.LLVMConstPtrToInt
     */
    public fun getIntCast(type: IntType): ConstantInt {
        val ref = LLVM.LLVMConstPtrToInt(ref, type.ref)

        return ConstantInt(ref)
    }

    /**
     * Cast this value to another type
     *
     * The specified type must be an integer or pointer type or a vector of
     * either of these types.
     *
     * @see LLVM.LLVMConstPointerCast
     */
    public fun getPointerCast(toType: Type): ConstantPointer {
        val value = LLVM.LLVMConstPointerCast(ref, toType.ref)

        return ConstantPointer(value)
    }

    /**
     * Convert this value to the target pointer type's address space
     *
     * The address spaces of these two types cannot be the same
     *
     * @see LLVM.LLVMConstAddrSpaceCast
     */
    public fun getAddrSpaceCast(type: PointerType): ConstantPointer {
        val ref = LLVM.LLVMConstAddrSpaceCast(ref, type.ref)

        return ConstantPointer(ref)
    }
}

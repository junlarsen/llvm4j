package dev.supergrecko.vexe.llvm.ir.values.constants

import dev.supergrecko.vexe.llvm.ir.Type
import dev.supergrecko.vexe.llvm.ir.Value
import dev.supergrecko.vexe.llvm.ir.types.IntType
import dev.supergrecko.vexe.llvm.ir.types.PointerType
import dev.supergrecko.vexe.llvm.ir.values.ConstantValue
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public class ConstantPointer internal constructor() : Value(), ConstantValue {
    public constructor(llvmRef: LLVMValueRef) : this() {
        ref = llvmRef
    }

    // core Core::Values::Constants::ConstantExpressions
    /**
     * Conversion to integer
     *
     * TODO: Assert that this points to an int
     *
     * @see LLVM.LLVMConstPtrToInt
     */
    public fun intcast(type: IntType): ConstantInt {
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
    fun cast(toType: Type): ConstantPointer {
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
    public fun addrspacecast(type: PointerType): ConstantPointer {
        val ref = LLVM.LLVMConstAddrSpaceCast(ref, type.ref)

        return ConstantPointer(ref)
    }
    //endregion Core::Values::Constants::ConstantExpressions
}

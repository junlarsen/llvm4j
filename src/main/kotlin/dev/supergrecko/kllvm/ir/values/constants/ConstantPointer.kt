package dev.supergrecko.kllvm.ir.values.constants

import dev.supergrecko.kllvm.ir.Value
import dev.supergrecko.kllvm.ir.types.IntType
import dev.supergrecko.kllvm.ir.types.PointerType
import dev.supergrecko.kllvm.ir.values.Constant
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public class ConstantPointer internal constructor() : Value(), Constant {
    public constructor(llvmValue: LLVMValueRef) : this() {
        ref = llvmValue
    }

    //core Core::Values::Constants::ConstantExpressions
    /**
     * Conversion to integer
     *
     * @see LLVM.LLVMConstPtrToInt
     *
     * TODO: Assert that this points to an int
     */
    public fun intcast(type: IntType): ConstantInt {
        val ref = LLVM.LLVMConstPtrToInt(ref, type.ref)

        return ConstantInt(ref)
    }

    /**
     * Convert this value to the target pointer type's address space
     *
     * The address spaces of these two types cannot be the same
     *
     * @see LLVM.LLVMConstAddrSpaceCast
     */
    public fun addrspacecast(type: PointerType): ConstantPointer {
        val selfAddr = getType().asPointerType().getAddressSpace()
        val destAddr = type.getAddressSpace()

        require(selfAddr != destAddr)

        val ref = LLVM.LLVMConstAddrSpaceCast(ref, type.ref)

        return ConstantPointer(ref)
    }
    //endregion Core::Values::Constants::ConstantExpressions
}
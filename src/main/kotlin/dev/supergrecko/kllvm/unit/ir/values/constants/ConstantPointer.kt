package dev.supergrecko.kllvm.unit.ir.values.constants

import dev.supergrecko.kllvm.unit.ir.Type
import dev.supergrecko.kllvm.unit.ir.TypeKind
import dev.supergrecko.kllvm.unit.ir.Value
import dev.supergrecko.kllvm.unit.ir.types.IntType
import dev.supergrecko.kllvm.unit.ir.types.PointerType
import dev.supergrecko.kllvm.unit.ir.values.ConstantValue
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public class ConstantPointer internal constructor() : Value(), ConstantValue {
    public constructor(llvmValue: LLVMValueRef) : this() {
        ref = llvmValue
    }

    // core Core::Values::Constants::ConstantExpressions
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
     * Cast this value to another type
     *
     * The specified type must be an integer or pointer type or a vector of
     * either of these types.
     *
     * @see LLVM.LLVMConstPointerCast
     */
    fun cast(toType: Type): ConstantPointer {
        val typeKind = toType.getTypeKind()

        require(typeKind == TypeKind.Vector || typeKind == TypeKind.Integer)

        if (typeKind == TypeKind.Vector) {
            val vecType = toType.asVectorType().getElementType().getTypeKind()
            require(vecType == TypeKind.Integer || vecType == TypeKind.Pointer)
        }

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
        val selfAddr = getType().asPointerType().getAddressSpace()
        val destAddr = type.getAddressSpace()

        require(selfAddr != destAddr)

        val ref = LLVM.LLVMConstAddrSpaceCast(ref, type.ref)

        return ConstantPointer(ref)
    }
    //endregion Core::Values::Constants::ConstantExpressions
}

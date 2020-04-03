package dev.supergrecko.kllvm.core.values

import dev.supergrecko.kllvm.annotations.Shared
import dev.supergrecko.kllvm.core.enumerations.TypeKind
import dev.supergrecko.kllvm.core.typedefs.Value
import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public class VectorValue internal constructor() : Value() {
    public constructor(llvmValue: LLVMValueRef) : this() {
        ref = llvmValue
    }

    /**
     * @see [LLVM.LLVMConstVector]
     */
    public constructor(values: List<Value>) : this() {
        val ptr = ArrayList(values.map { it.ref }).toTypedArray()

        ref = LLVM.LLVMConstVector(PointerPointer(*ptr), ptr.size)
    }

    /**
     * Get an element at specified [index] as a constant
     *
     * This is shared with [ArrayValue], [VectorValue], [StructValue]
     */
    @Shared
    public fun getElementAsConstant(index: Int): Value {
        val value = LLVM.LLVMGetElementAsConstant(ref, index)

        return Value(value)
    }

    //region Core::Values::Constants::ConstantExpressions
    /**
     * Negate all the values in the vector
     *
     * This operation is only valid on Vectors of Integers. Use FNeg for floats
     *
     * @see [LLVM.LLVMConstNeg]
     */
    public fun neg(
        hasNUW: Boolean = false,
        hasNSW: Boolean = false
    ): VectorValue {
        require(isConstant())
        require(getType().getTypeKind() == TypeKind.Integer)
        require(!(hasNSW && hasNSW)) { "Cannot negate with both NSW and NUW" }

        val ref = when (true) {
            hasNSW -> LLVM.LLVMConstNSWNeg(ref)
            hasNUW -> LLVM.LLVMConstNUWNeg(ref)
            else -> LLVM.LLVMConstNeg(ref)
        }

        return VectorValue(ref)
    }

    public fun not(): VectorValue {
        require(isConstant())
        require(getType().getTypeKind() == TypeKind.Integer)

        val ref = LLVM.LLVMConstNot(ref)

        return VectorValue(ref)
    }

    public fun add(
        v: IntValue,
        hasNUW: Boolean = false,
        hasNSW: Boolean = false
    ): VectorValue {
        require(isConstant() && v.isConstant())
        require(getType().getTypeKind() == TypeKind.Integer)
        require(!(hasNSW && hasNSW)) { "Cannot add with both NSW and NUW" }

        val ref = when (true) {
            hasNSW -> LLVM.LLVMConstNSWAdd(ref, v.ref)
            hasNUW -> LLVM.LLVMConstNUWAdd(ref, v.ref)
            else -> LLVM.LLVMConstAdd(ref, v.ref)
        }

        return VectorValue(ref)
    }

    public fun sub(
        v: IntValue,
        hasNUW: Boolean = false,
        hasNSW: Boolean = false
    ): VectorValue {
        require(isConstant() && v.isConstant())
        require(getType().getTypeKind() == TypeKind.Integer)
        require(!(hasNSW && hasNSW)) { "Cannot sub with both NSW and NUW" }

        val ref = when (true) {
            hasNSW -> LLVM.LLVMConstNSWSub(ref, v.ref)
            hasNUW -> LLVM.LLVMConstNUWSub(ref, v.ref)
            else -> LLVM.LLVMConstSub(ref, v.ref)
        }

        return VectorValue(ref)
    }

    public fun mul(
        v: IntValue,
        hasNUW: Boolean = false,
        hasNSW: Boolean = false
    ): VectorValue {
        require(isConstant() && v.isConstant())
        require(getType().getTypeKind() == TypeKind.Integer)
        require(!(hasNSW && hasNSW)) { "Cannot sub with both NSW and NUW" }

        val ref = when (true) {
            hasNSW -> LLVM.LLVMConstNSWMul(ref, v.ref)
            hasNUW -> LLVM.LLVMConstNUWMul(ref, v.ref)
            else -> LLVM.LLVMConstMul(ref, v.ref)
        }

        return VectorValue(ref)
    }
    //endregion Core::Values::Constants::ConstantExpressions
}
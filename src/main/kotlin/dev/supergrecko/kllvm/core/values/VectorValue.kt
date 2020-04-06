package dev.supergrecko.kllvm.core.values

import dev.supergrecko.kllvm.core.enumerations.IntPredicate
import dev.supergrecko.kllvm.types.TypeKind
import dev.supergrecko.kllvm.core.typedefs.Value
import dev.supergrecko.kllvm.types.IntType
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
     *
     * TODO: Move into contract
     */
    public fun getElementAsConstant(index: Int): Value {
        val value = LLVM.LLVMGetElementAsConstant(ref, index)

        return Value(value)
    }

    //region Core::Values::Constants::ConstantExpressions
    /**
     * Negate the constant value
     *
     * LLVM doesn't actually have a neg instruction, but it's implemented using
     * subtraction. It subtracts the value of max value of the type of the value
     *
     * NUW and NSW stand for "No Unsigned Wrap" and "No Signed Wrap",
     * respectively. If [hasNUW] [hasNSW] are present, the result
     * value of the add is a poison value if unsigned and/or signed overflow,
     * respectively, occurs.
     *
     * @see LLVM.LLVMConstNeg
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

    /**
     * Invert each integer value using XOR
     *
     * This in short performs the same action as [neg] but instead of using
     * subtraction it uses a bitwise XOR where B is always true.
     *
     * @see LLVM.LLVMConstNot
     */
    public fun not(): VectorValue {
        require(isConstant())
        require(getType().getTypeKind() == TypeKind.Integer)

        val ref = LLVM.LLVMConstNot(ref)

        return VectorValue(ref)
    }

    /**
     * Add another value to this vector of integers
     *
     * If the sum has unsigned overflow, the result returned is the
     * mathematical result modulo 2n, where n is the bit width of the result.
     *
     * NUW and NSW stand for "No Unsigned Wrap" and "No Signed Wrap",
     * respectively. If [hasNUW] [hasNSW] are present, the result
     * value of the add is a poison value if unsigned and/or signed overflow,
     * respectively, occurs.
     *
     * @see LLVM.LLVMConstAdd
     */
    public fun add(
        v: IntValue,
        hasNUW: Boolean = false,
        hasNSW: Boolean = false
    ): VectorValue {
        require(isConstant() && v.isConstant())
        require(getType().getTypeKind() == TypeKind.Integer)
        require(v.getType().getTypeKind() == TypeKind.Integer)
        require(!(hasNSW && hasNSW)) { "Cannot add with both NSW and NUW" }

        val ref = when (true) {
            hasNSW -> LLVM.LLVMConstNSWAdd(ref, v.ref)
            hasNUW -> LLVM.LLVMConstNUWAdd(ref, v.ref)
            else -> LLVM.LLVMConstAdd(ref, v.ref)
        }

        return VectorValue(ref)
    }

    /**
     * Subtract another value from this vector of integers
     *
     * If the sum has unsigned overflow, the result returned is the
     * mathematical result modulo 2n, where n is the bit width of the result.
     *
     * NUW and NSW stand for "No Unsigned Wrap" and "No Signed Wrap",
     * respectively. If [hasNUW] [hasNSW] are present, the result
     * value of the add is a poison value if unsigned and/or signed overflow,
     * respectively, occurs.
     */
    public fun sub(
        v: IntValue,
        hasNUW: Boolean = false,
        hasNSW: Boolean = false
    ): VectorValue {
        require(isConstant() && v.isConstant())
        require(getType().getTypeKind() == TypeKind.Integer)
        require(v.getType().getTypeKind() == TypeKind.Integer)
        require(!(hasNSW && hasNSW)) { "Cannot sub with both NSW and NUW" }

        val ref = when (true) {
            hasNSW -> LLVM.LLVMConstNSWSub(ref, v.ref)
            hasNUW -> LLVM.LLVMConstNUWSub(ref, v.ref)
            else -> LLVM.LLVMConstSub(ref, v.ref)
        }

        return VectorValue(ref)
    }

    /**
     * Multiply another value with this vector of integers
     *
     * If the sum has unsigned overflow, the result returned is the
     * mathematical result modulo 2n, where n is the bit width of the result.
     *
     * NUW and NSW stand for "No Unsigned Wrap" and "No Signed Wrap",
     * respectively. If [hasNUW] [hasNSW] are present, the result
     * value of the add is a poison value if unsigned and/or signed overflow,
     * respectively, occurs.
     */
    public fun mul(
        v: IntValue,
        hasNUW: Boolean = false,
        hasNSW: Boolean = false
    ): VectorValue {
        require(isConstant() && v.isConstant())
        require(getType().getTypeKind() == TypeKind.Integer)
        require(v.getType().getTypeKind() == TypeKind.Integer)
        require(!(hasNSW && hasNSW)) { "Cannot sub with both NSW and NUW" }

        val ref = when (true) {
            hasNSW -> LLVM.LLVMConstNSWMul(ref, v.ref)
            hasNUW -> LLVM.LLVMConstNUWMul(ref, v.ref)
            else -> LLVM.LLVMConstMul(ref, v.ref)
        }

        return VectorValue(ref)
    }

    /**
     * Perform division with another signed integer vector
     *
     * Division by zero is undefined behavior. For vectors, if any element of
     * the divisor is zero, the operation has undefined behavior. Overflow also
     * leads to undefined behavior; this is a rare case, but can occur,
     * for example, by doing a 32-bit division of -2147483648 by -1.
     *
     * If the [exact] arg is present, the result value of the sdiv is a poison
     * value if the result would be rounded.
     *
     * TODO: Find a way to determine if type is unsigned
     */
    public fun sdiv(
        v: VectorValue,
        exact: Boolean
    ): VectorValue {
        require(isConstant() && v.isConstant())
        require(getType().getTypeKind() == TypeKind.Integer)
        require(v.getType().getTypeKind() == TypeKind.Integer)

        val ref = if (exact) {
            LLVM.LLVMConstExactSDiv(ref, v.ref)
        } else {
            LLVM.LLVMConstSDiv(ref, v.ref)
        }

        return VectorValue(ref)
    }

    /**
     * Perform division with another unsigned integer vector
     *
     * Division by zero is undefined behavior. For vectors, if any element of
     * the divisor is zero, the operation has undefined behavior
     *
     * If the [exact] arg is present, the result value of the udiv is a poison
     * value if %op1 is not a multiple of %op2, eg "((a udiv exact b) mul b) == a".
     *
     * TODO: Find a way to determine if type is unsigned
     */
    public fun udiv(
        v: VectorValue,
        exact: Boolean
    ): VectorValue {
        require(isConstant() && v.isConstant())
        require(getType().getTypeKind() == TypeKind.Integer)
        require(v.getType().getTypeKind() == TypeKind.Integer)

        val ref = if (exact) {
            LLVM.LLVMConstExactUDiv(ref, v.ref)
        } else {
            LLVM.LLVMConstUDiv(ref, v.ref)
        }

        return VectorValue(ref)
    }

    public fun rem(v: VectorValue, unsigned: Boolean): VectorValue {
        require(isConstant() && v.isConstant())
        require(getType().getTypeKind() == TypeKind.Integer)
        require(v.getType().getTypeKind() == TypeKind.Integer)

        val ref = if (unsigned) {
            LLVM.LLVMConstURem(ref, v.ref)
        } else {
            LLVM.LLVMConstSRem(ref, v.ref)
        }

        return VectorValue(ref)
    }


    public fun and(v: VectorValue): VectorValue {
        require(isConstant() && v.isConstant())
        require(getType().getTypeKind() == TypeKind.Integer)
        require(v.getType().getTypeKind() == TypeKind.Integer)

        val ref = LLVM.LLVMConstAnd(ref, v.ref)

        return VectorValue(ref)
    }

    public fun or(v: VectorValue): VectorValue {
        require(isConstant() && v.isConstant())
        require(getType().getTypeKind() == TypeKind.Integer)
        require(v.getType().getTypeKind() == TypeKind.Integer)

        val ref = LLVM.LLVMConstOr(ref, v.ref)

        return VectorValue(ref)
    }

    public fun xor(v: VectorValue): VectorValue {
        require(isConstant() && v.isConstant())
        require(getType().getTypeKind() == TypeKind.Integer)
        require(v.getType().getTypeKind() == TypeKind.Integer)

        val ref = LLVM.LLVMConstXor(ref, v.ref)

        return VectorValue(ref)
    }

    public fun cmp(predicate: IntPredicate, v: VectorValue): VectorValue {
        require(isConstant() && v.isConstant())
        require(getType().getTypeKind() == TypeKind.Integer)
        require(v.getType().getTypeKind() == TypeKind.Integer)

        val ref = LLVM.LLVMConstICmp(predicate.value, ref, v.ref)

        return VectorValue(ref)
    }

    public fun shl(v: VectorValue): VectorValue {
        require(isConstant() && v.isConstant())
        require(getType().getTypeKind() == TypeKind.Integer)
        require(v.getType().getTypeKind() == TypeKind.Integer)

        val ref = LLVM.LLVMConstShl(ref, v.ref)

        return VectorValue(ref)
    }

    public fun lshr(v: VectorValue): VectorValue {
        require(isConstant() && v.isConstant())
        require(getType().getTypeKind() == TypeKind.Integer)
        require(v.getType().getTypeKind() == TypeKind.Integer)

        val ref = LLVM.LLVMConstLShr(ref, v.ref)

        return VectorValue(ref)
    }

    public fun ashr(v: VectorValue): VectorValue {
        require(isConstant() && v.isConstant())
        require(getType().getTypeKind() == TypeKind.Integer)
        require(v.getType().getTypeKind() == TypeKind.Integer)

        val ref = LLVM.LLVMConstAShr(ref, v.ref)

        return VectorValue(ref)
    }

    public fun trunc(type: IntType): VectorValue {
        require(isConstant())
        require(getType().getTypeKind() == TypeKind.Integer)

        val ref = LLVM.LLVMConstTrunc(ref, type.ref)

        return VectorValue(ref)
    }

    public fun sext(type: IntType): VectorValue {
        require(isConstant())
        require(getType().getTypeKind() == TypeKind.Integer)

        val ref = LLVM.LLVMConstSExt(ref, type.ref)

        return VectorValue(ref)
    }

    public fun zext(type: IntType): VectorValue {
        require(isConstant())
        require(getType().getTypeKind() == TypeKind.Integer)

        val ref = LLVM.LLVMConstZExt(ref, type.ref)

        return VectorValue(ref)
    }
    //endregion Core::Values::Constants::ConstantExpressions
}
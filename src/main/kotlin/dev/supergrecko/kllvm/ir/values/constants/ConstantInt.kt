package dev.supergrecko.kllvm.ir.values.constants

import dev.supergrecko.kllvm.internal.contracts.Unreachable
import dev.supergrecko.kllvm.internal.util.toLLVMBool
import dev.supergrecko.kllvm.ir.Value
import dev.supergrecko.kllvm.ir.instructions.IntPredicate
import dev.supergrecko.kllvm.ir.types.IntType
import dev.supergrecko.kllvm.ir.values.Constant
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public class ConstantInt internal constructor() : Value(), Constant {
    public constructor(llvmValue: LLVMValueRef) : this() {
        ref = llvmValue
    }

    /**
     * Create a new integer value of types [type]
     *
     * This creates a new integer from [type] with [value]. You can decide if
     * this is signed with [signExtend].
     *
     * @see LLVM.LLVMConstInt
     */
    public constructor(
        type: IntType,
        value: Long,
        signExtend: Boolean
    ) : this() {
        ref = LLVM.LLVMConstInt(type.ref, value, signExtend.toLLVMBool())
    }

    /**
     * Create a constant integer of arbitrary precision
     *
     * TODO: Find out [words] actually is ... and how to properly use this
     *
     * @see LLVM.LLVMConstIntOfArbitraryPrecision
     */
    public constructor(type: IntType, words: List<Long>) : this() {
        ref = LLVM.LLVMConstIntOfArbitraryPrecision(
            type.ref,
            words.size,
            words.toLongArray()
        )
    }

    //region Core::Values::Constants::ScalarConstants
    /**
     * @see LLVM.LLVMConstIntGetZExtValue
     */
    public fun getUnsignedValue(): Long {
        return LLVM.LLVMConstIntGetZExtValue(ref)
    }

    /**
     * @see LLVM.LLVMConstIntGetSExtValue
     */
    public fun getSignedValue(): Long {
        return LLVM.LLVMConstIntGetSExtValue(ref)
    }
    //endregion  Core::Values::Constants::ScalarConstants

    //region Core::Values::Constants::ConstantExpressions
    /**
     * Negate the constant value
     *
     * This value is not modified, but it returns a new value with the result of
     * the operation.
     *
     * LLVM doesn't actually have a neg instruction, but it's implemented using
     * subtraction. It subtracts the value of max value of the types of the value
     *
     * NUW and NSW stand for "No Unsigned Wrap" and "No Signed Wrap",
     * respectively. If [hasNUW] [hasNSW] are present, the result
     * value of the add is a poison value if unsigned and/or signed overflow,
     * respectively, occurs.
     *
     * @see LLVM.LLVMConstNeg
     */
    public fun neg(hasNUW: Boolean = false, hasNSW: Boolean = false): ConstantInt {
        require(isConstant())
        require(!(hasNSW && hasNSW)) { "Cannot negate with both NSW and NUW" }

        val ref = when (true) {
            hasNSW -> LLVM.LLVMConstNSWNeg(ref)
            hasNUW -> LLVM.LLVMConstNUWNeg(ref)
            else -> LLVM.LLVMConstNeg(ref)
        }

        return ConstantInt(ref)
    }

    /**
     * Invert the integer value using XOR
     *
     * This value is not modified, but it returns a new value with the result of
     * the operation.
     *
     * @see LLVM.LLVMConstNot
     */
    public fun not(): ConstantInt {
        require(isConstant())

        val ref = LLVM.LLVMConstNot(ref)

        return ConstantInt(ref)
    }

    /**
     * Add another value to this integer
     *
     * This value is not modified, but it returns a new value with the result of
     * the operation.
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
        v: ConstantInt,
        hasNUW: Boolean = false,
        hasNSW: Boolean = false
    ): ConstantInt {
        require(isConstant() && v.isConstant())
        require(!(hasNSW && hasNSW)) { "Cannot add with both NSW and NUW" }

        val ref = when (true) {
            hasNSW -> LLVM.LLVMConstNSWAdd(ref, v.ref)
            hasNUW -> LLVM.LLVMConstNUWAdd(ref, v.ref)
            else -> LLVM.LLVMConstAdd(ref, v.ref)
        }

        return ConstantInt(ref)
    }

    /**
     * Subtract another value from this integer
     *
     * This value is not modified, but it returns a new value with the result of
     * the operation.
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
        v: ConstantInt,
        hasNUW: Boolean = false,
        hasNSW: Boolean = false
    ): ConstantInt {
        require(isConstant() && v.isConstant())
        require(!(hasNSW && hasNSW)) { "Cannot sub with both NSW and NUW" }

        val ref = when (true) {
            hasNSW -> LLVM.LLVMConstNSWSub(ref, v.ref)
            hasNUW -> LLVM.LLVMConstNUWSub(ref, v.ref)
            else -> LLVM.LLVMConstSub(ref, v.ref)
        }

        return ConstantInt(ref)
    }

    /**
     * Multiply another value with this integer
     *
     * This value is not modified, but it returns a new value with the result of
     * the operation.
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
        v: ConstantInt,
        hasNUW: Boolean = false,
        hasNSW: Boolean = false
    ): ConstantInt {
        require(isConstant() && v.isConstant())
        require(!(hasNSW && hasNSW)) { "Cannot sub with both NSW and NUW" }

        val ref = when (true) {
            hasNSW -> LLVM.LLVMConstNSWMul(ref, v.ref)
            hasNUW -> LLVM.LLVMConstNUWMul(ref, v.ref)
            else -> LLVM.LLVMConstMul(ref, v.ref)
        }

        return ConstantInt(ref)
    }

    /**
     * Perform division with another integer
     *
     * Division by zero is undefined behavior. For vectors, if any element of
     * the divisor is zero, the operation has undefined behavior. Overflow also
     * leads to undefined behavior; this is a rare case, but can occur,
     * for example, by doing a 32-bit division of -2147483648 by -1.
     *
     * If [unsigned] is present, UDiv/ExactUDiv will be used.
     *
     * If the [exact] arg is present, the result value of the sdiv/udiv is a
     * poison value if the result would be rounded.
     *
     * TODO: Find a way to determine if types is unsigned
     */
    public fun div(v: ConstantInt, exact: Boolean, unsigned: Boolean): ConstantInt {
        require(isConstant())

        val ref = when (true) {
            unsigned && exact -> LLVM.LLVMConstExactUDiv(ref, v.ref)
            !unsigned && exact -> LLVM.LLVMConstExactSDiv(ref, v.ref)
            unsigned && !exact -> LLVM.LLVMConstUDiv(ref, v.ref)
            !unsigned && !exact -> LLVM.LLVMConstSDiv(ref, v.ref)
            else -> throw Unreachable()
        }

        return ConstantInt(ref)
    }

    /**
     * Get the remainder from the unsigned division of this and another integer
     *
     * Taking the remainder of a division by zero is undefined behavior.
     *
     * If [unsigned] is present, URem will be used
     */
    public fun rem(v: ConstantInt, unsigned: Boolean): ConstantInt {
        require(isConstant() && v.isConstant())

        val ref = if (unsigned) {
            LLVM.LLVMConstURem(ref, v.ref)
        } else {
            LLVM.LLVMConstSRem(ref, v.ref)
        }

        return ConstantInt(ref)
    }

    public fun and(v: ConstantInt): ConstantInt {
        require(isConstant() && v.isConstant())

        val ref = LLVM.LLVMConstAnd(ref, v.ref)

        return ConstantInt(ref)
    }

    public fun or(v: ConstantInt): ConstantInt {
        require(isConstant() && v.isConstant())

        val ref = LLVM.LLVMConstOr(ref, v.ref)

        return ConstantInt(ref)
    }

    public fun xor(v: ConstantInt): ConstantInt {
        require(isConstant() && v.isConstant())

        val ref = LLVM.LLVMConstXor(ref, v.ref)

        return ConstantInt(ref)
    }

    public fun cmp(predicate: IntPredicate, v: ConstantInt): ConstantInt {
        require(isConstant() && v.isConstant())

        val ref = LLVM.LLVMConstICmp(predicate.value, ref, v.ref)

        return ConstantInt(ref)
    }

    public fun shl(v: ConstantInt): ConstantInt {
        require(isConstant() && v.isConstant())

        val ref = LLVM.LLVMConstShl(ref, v.ref)

        return ConstantInt(ref)
    }

    public fun lshr(v: ConstantInt): ConstantInt {
        require(isConstant() && v.isConstant())

        val ref = LLVM.LLVMConstLShr(ref, v.ref)

        return ConstantInt(ref)
    }

    public fun ashr(v: ConstantInt): ConstantInt {
        require(isConstant() && v.isConstant())

        val ref = LLVM.LLVMConstAShr(ref, v.ref)

        return ConstantInt(ref)
    }

    public fun trunc(type: IntType): ConstantInt {
        require(isConstant())

        val ref = LLVM.LLVMConstTrunc(ref, type.ref)

        return ConstantInt(ref)
    }

    public fun sext(type: IntType): ConstantInt {
        require(isConstant())

        val ref = LLVM.LLVMConstSExt(ref, type.ref)

        return ConstantInt(ref)
    }

    public fun zext(type: IntType): ConstantInt {
        require(isConstant())

        val ref = LLVM.LLVMConstZExt(ref, type.ref)

        return ConstantInt(ref)
    }
    //endregion Core::Values::Constants::ConstantExpressions
}

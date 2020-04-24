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
        val ref = LLVM.LLVMConstNot(ref)

        return ConstantInt(ref)
    }

    /**
     * Perform addition for the two operands
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
        rhs: ConstantInt,
        hasNUW: Boolean = false,
        hasNSW: Boolean = false
    ): ConstantInt {
        require(!(hasNSW && hasNSW)) { "Cannot add with both NSW and NUW" }

        val ref = when (true) {
            hasNSW -> LLVM.LLVMConstNSWAdd(ref, rhs.ref)
            hasNUW -> LLVM.LLVMConstNUWAdd(ref, rhs.ref)
            else -> LLVM.LLVMConstAdd(ref, rhs.ref)
        }

        return ConstantInt(ref)
    }

    /**
     * Perform subtraction for the two operands
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
        rhs: ConstantInt,
        hasNUW: Boolean = false,
        hasNSW: Boolean = false
    ): ConstantInt {
        require(!(hasNSW && hasNSW)) { "Cannot sub with both NSW and NUW" }

        val ref = when (true) {
            hasNSW -> LLVM.LLVMConstNSWSub(ref, rhs.ref)
            hasNUW -> LLVM.LLVMConstNUWSub(ref, rhs.ref)
            else -> LLVM.LLVMConstSub(ref, rhs.ref)
        }

        return ConstantInt(ref)
    }

    /**
     * Perform multiplication for the two operands
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
        rhs: ConstantInt,
        hasNUW: Boolean = false,
        hasNSW: Boolean = false
    ): ConstantInt {
        require(!(hasNSW && hasNSW)) { "Cannot sub with both NSW and NUW" }

        val ref = when (true) {
            hasNSW -> LLVM.LLVMConstNSWMul(ref, rhs.ref)
            hasNUW -> LLVM.LLVMConstNUWMul(ref, rhs.ref)
            else -> LLVM.LLVMConstMul(ref, rhs.ref)
        }

        return ConstantInt(ref)
    }

    /**
     * Perform division for the two operands
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
    public fun div(rhs: ConstantInt, exact: Boolean, unsigned: Boolean): ConstantInt {
        val ref = when (true) {
            unsigned && exact -> LLVM.LLVMConstExactUDiv(ref, rhs.ref)
            !unsigned && exact -> LLVM.LLVMConstExactSDiv(ref, rhs.ref)
            unsigned && !exact -> LLVM.LLVMConstUDiv(ref, rhs.ref)
            !unsigned && !exact -> LLVM.LLVMConstSDiv(ref, rhs.ref)
            else -> throw Unreachable()
        }

        return ConstantInt(ref)
    }

    /**
     * Get the remainder from the unsigned division for the two operands
     *
     * Taking the remainder of a division by zero is undefined behavior.
     *
     * If [unsigned] is present, URem will be used
     */
    public fun rem(rhs: ConstantInt, unsigned: Boolean): ConstantInt {
        val ref = if (unsigned) {
            LLVM.LLVMConstURem(ref, rhs.ref)
        } else {
            LLVM.LLVMConstSRem(ref, rhs.ref)
        }

        return ConstantInt(ref)
    }

    /**
     * Perform bitwise logical and for the two operands
     *
     * The truth table used for the 'and' instruction is:
     *
     * In0	In1	Out
     * 0	0	0
     * 0	1	0
     * 1	0	0
     * 1	1	1
     */
    public fun and(rhs: ConstantInt): ConstantInt {
        val ref = LLVM.LLVMConstAnd(ref, rhs.ref)

        return ConstantInt(ref)
    }

    /**
     * Perform bitwise logical or for the two operands
     *
     * The truth table used for the 'or' instruction is:
     *
     * In0	In1	Out
     * 0	0	0
     * 0	1	1
     * 1	0	1
     * 1	1	1
     */
    public fun or(rhs: ConstantInt): ConstantInt {
        val ref = LLVM.LLVMConstOr(ref, rhs.ref)

        return ConstantInt(ref)
    }

    /**
     * Perform bitwise logical xor for the two operands
     *
     * The truth table used for the 'xor' instruction is:
     *
     * In0	In1	Out
     * 0	0	0
     * 0	1	1
     * 1	0	1
     * 1	1	0
     */
    public fun xor(rhs: ConstantInt): ConstantInt {
        val ref = LLVM.LLVMConstXor(ref, rhs.ref)

        return ConstantInt(ref)
    }

    /**
     * Perform logical comparison for the two operands
     *
     * This method receives a [predicate] which determines which logical
     * comparison method shall be used for the comparison.
     */
    public fun cmp(predicate: IntPredicate, rhs: ConstantInt): ConstantInt {
        val ref = LLVM.LLVMConstICmp(predicate.value, ref, rhs.ref)

        return ConstantInt(ref)
    }

    /**
     * Shift the operand to the left [bits] number of bits
     *
     * LLVM-C does not support NUW/NSW attributes for this operation
     */
    public fun shl(bits: ConstantInt): ConstantInt {
        val ref = LLVM.LLVMConstShl(ref, bits.ref)

        return ConstantInt(ref)
    }

    /**
     * Logically shift the operand to the right [bits] number of bits with
     * zero fill
     *
     * LLVM-C does not support NUW/NSW attributes for this operation
     */
    public fun lshr(bits: ConstantInt): ConstantInt {
        val ref = LLVM.LLVMConstLShr(ref, bits.ref)

        return ConstantInt(ref)
    }

    /**
     * Arithmetically shift the operand to the right [bits] number with sign
     * extension
     *
     * LLVM-C does nt support the 'exact' attribute for this operation
     */
    public fun ashr(bits: ConstantInt): ConstantInt {
        val ref = LLVM.LLVMConstAShr(ref, bits.ref)

        return ConstantInt(ref)
    }

    /**
     * Truncates this operand to the type [type]
     *
     * The bit size of this must be larger than the bit size of [type]. Equal
     * sizes are not allowed
     */
    public fun trunc(type: IntType): ConstantInt {
        val selfWidth = getType().asIntType().getTypeWidth()
        val destWidth = type.getTypeWidth()

        require(selfWidth > destWidth)

        val ref = LLVM.LLVMConstTrunc(ref, type.ref)

        return ConstantInt(ref)
    }

    /**
     * Sign extend this value to type [type]
     *
     * The bit size of this must be tinier than the bit size of the
     * destination type
     */
    public fun sext(type: IntType): ConstantInt {
        val selfWidth = getType().asIntType().getTypeWidth()
        val destWidth = type.getTypeWidth()

        require(selfWidth < destWidth)

        val ref = LLVM.LLVMConstSExt(ref, type.ref)

        return ConstantInt(ref)
    }

    /**
     * Zero extend this value to type [type]
     *
     * The bit size of this must be tinier than the bit size of the
     * destination type
     */
    public fun zext(type: IntType): ConstantInt {
        val selfWidth = getType().asIntType().getTypeWidth()
        val destWidth = type.getTypeWidth()

        require(selfWidth < destWidth)

        val ref = LLVM.LLVMConstZExt(ref, type.ref)

        return ConstantInt(ref)
    }
    //endregion Core::Values::Constants::ConstantExpressions
}

package dev.supergrecko.vexe.llvm.ir.values.constants

import dev.supergrecko.vexe.llvm.internal.contracts.Unreachable
import dev.supergrecko.vexe.llvm.internal.util.toLLVMBool
import dev.supergrecko.vexe.llvm.ir.Value
import dev.supergrecko.vexe.llvm.ir.instructions.IntPredicate
import dev.supergrecko.vexe.llvm.ir.types.FloatType
import dev.supergrecko.vexe.llvm.ir.types.IntType
import dev.supergrecko.vexe.llvm.ir.types.PointerType
import dev.supergrecko.vexe.llvm.ir.values.ConstantValue
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public class ConstantInt internal constructor() : Value(), ConstantValue {
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
        signExtend: Boolean = true
    ) : this() {
        ref = LLVM.LLVMConstInt(type.ref, value, signExtend.toLLVMBool())
    }

    public constructor(
        type: IntType,
        value: Int,
        signExtend: Boolean = true
    ) : this(type, value.toLong(), signExtend)

    /**
     * Create a constant integer of arbitrary precision
     *
     * @see LLVM.LLVMConstIntOfArbitraryPrecision
     *
     * TODO: Find out how the words constructor actually works
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
     * Get the zero extended (unsigned) value of this Constant
     *
     * @see LLVM.LLVMConstIntGetZExtValue
     */
    public fun getUnsignedValue(): Long {
        return LLVM.LLVMConstIntGetZExtValue(ref)
    }

    /**
     * Get the sign extended (signed) value of this Constant
     *
     * @see LLVM.LLVMConstIntGetSExtValue
     */
    public fun getSignedValue(): Long {
        return LLVM.LLVMConstIntGetSExtValue(ref)
    }
    //endregion  Core::Values::Constants::ScalarConstants

    //region Core::Values::Constants::ConstantExpressions
    /**
     * Negate this operand
     *
     * LLVM doesn't actually have a neg instruction, but it's implemented using
     * subtraction. It subtracts the value of max value of the types of the
     * value
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
    ): ConstantInt {
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
     * @see LLVM.LLVMConstNot
     */
    public fun not(): ConstantInt {
        val ref = LLVM.LLVMConstNot(ref)

        return ConstantInt(ref)
    }

    /**
     * Perform addition for the two operands
     *
     * If the sum has unsigned overflow, the result returned is the
     * mathematical result modulo 2^n, where n is the bit width of the result.
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
     * If the sum has unsigned overflow, the result returned is the
     * mathematical result modulo 2n, where n is the bit width of the result.
     *
     * NUW and NSW stand for "No Unsigned Wrap" and "No Signed Wrap",
     * respectively. If [hasNUW] [hasNSW] are present, the result
     * value of the add is a poison value if unsigned and/or signed overflow,
     * respectively, occurs.
     *
     * @see LLVM.LLVMConstSub
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
     * If the sum has unsigned overflow, the result returned is the
     * mathematical result modulo 2n, where n is the bit width of the result.
     *
     * NUW and NSW stand for "No Unsigned Wrap" and "No Signed Wrap",
     * respectively. If [hasNUW] [hasNSW] are present, the result
     * value of the add is a poison value if unsigned and/or signed overflow,
     * respectively, occurs.
     *
     * @see LLVM.LLVMConstMul
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
     *
     * @see LLVM.LLVMConstUDiv
     * @see LLVM.LLVMConstSDiv
     */
    public fun div(
        rhs: ConstantInt,
        exact: Boolean,
        unsigned: Boolean
    ): ConstantInt {
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
     * Perform division with another unsigned integer vector
     *
     * Division by zero is undefined behavior. If any element of
     * the divisor is zero, the operation has undefined behavior
     *
     * If the [exact] arg is present, the result value of the udiv is a poison
     * value if %op1 is not a multiple of %op2.
     * eg "((a udiv exact b) mul b) == a".
     *
     * @see LLVM.LLVMConstUDiv
     */
    public fun udiv(
        rhs: ConstantInt,
        exact: Boolean
    ): ConstantInt = div(rhs, exact, true)

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
     * @see LLVM.LLVMConstSDiv
     */
    public fun sdiv(
        rhs: ConstantInt,
        exact: Boolean
    ): ConstantInt = div(rhs, exact, false)

    /**
     * Get the remainder from the division of the two operands
     *
     * Taking the remainder of a division by zero is undefined behavior.
     *
     * If [unsigned] is present, URem will be used'
     *
     * @see LLVM.LLVMSRem
     * @see LLVM.LLVMURem
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
     * Get the remainder from the division of the two operands
     *
     * Taking the remainder of a division by zero is undefined behavior.
     *
     * @see LLVM.LLVMURem
     */
    public fun urem(rhs: ConstantInt): ConstantInt = rem(rhs, true)

    /**
     * Get the remainder from the division of the two operands
     *
     * Taking the remainder of a division by zero is undefined behavior.
     *
     * @see LLVM.LLVMSRem
     */
    public fun srem(rhs: ConstantInt): ConstantInt = rem(rhs, false)

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
     *
     * @see LLVM.LLVMConstAdd
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
     *
     * @see LLVM.LLVMConstOr
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
     *
     * @see LLVM.LLVMConstXor
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
     *
     * @see LLVM.LLVMConstICmp
     */
    public fun cmp(predicate: IntPredicate, rhs: ConstantInt): ConstantInt {
        val ref = LLVM.LLVMConstICmp(predicate.value, ref, rhs.ref)

        return ConstantInt(ref)
    }

    /**
     * Shift the operand to the left [bits] number of bits
     *
     * LLVM-C does not support NUW/NSW attributes for this operation
     *
     * @see LLVM.LLVMConstShl
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
     *
     * @see LLVM.LLVMConstLShr
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
     *
     * @see LLVM.LLVMConstAShr
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
     *
     * @see LLVM.LLVMConstTrunc
     */
    public fun trunc(type: IntType): ConstantInt {
        val selfWidth = getType().asIntType().getTypeWidth()
        val destWidth = type.getTypeWidth()

        require(selfWidth > destWidth)

        val ref = LLVM.LLVMConstTrunc(ref, type.ref)

        return ConstantInt(ref)
    }

    /**
     * Extend this value to type [type]
     *
     * The bit size of this must be tinier than the bit size of the
     * destination type
     *
     * @see LLVM.LLVMConstSExt
     * @see LLVM.LLVMConstZExt
     */
    public fun ext(type: IntType, signExtend: Boolean): ConstantInt {
        val selfWidth = getType().asIntType().getTypeWidth()
        val destWidth = type.getTypeWidth()

        require(selfWidth < destWidth)

        val ref = if (signExtend) {
            LLVM.LLVMConstSExt(ref, type.ref)
        } else {
            LLVM.LLVMConstZExt(ref, type.ref)
        }

        return ConstantInt(ref)
    }

    /**
     * Sign extend this value to type [type]
     *
     * The bit size of this must be tinier than the bit size of the
     * destination type
     *
     * @see LLVM.LLVMConstSExt
     */
    public fun sext(type: IntType): ConstantInt = ext(type, true)

    /**
     * Zero extend this value to type [type]
     *
     * The bit size of this must be tinier than the bit size of the
     * destination type
     *
     * @see LLVM.LLVMConstZExt
     */
    public fun zext(type: IntType): ConstantInt = ext(type, false)

    /**
     * Converstion to float type
     *
     * @see LLVM.LLVMConstSIToFP
     * @see LLVM.LLVMConstUIToFP
     */
    public fun tofp(type: FloatType, signExtend: Boolean): ConstantFloat {
        val ref = if (signExtend) {
            LLVM.LLVMConstSIToFP(ref, type.ref)
        } else {
            LLVM.LLVMConstUIToFP(ref, type.ref)
        }

        return ConstantFloat(ref)
    }

    /**
     * Conversion to float type using this as unsigned
     *
     * @see LLVM.LLVMConstUIToFP
     */
    public fun uitofp(type: FloatType): ConstantFloat = tofp(type, false)

    /**
     * Conversion to float type using this as signed
     *
     * @see LLVM.LLVMConstSIToFP
     */
    public fun sitofp(type: FloatType): ConstantFloat = tofp(type, false)

    /**
     * Conversion to integer pointer
     *
     * @see LLVM.LLVMConstIntToPtr
     */
    public fun ptrcast(type: PointerType): ConstantPointer {
        val ref = LLVM.LLVMConstIntToPtr(ref, type.ref)

        return ConstantPointer(ref)
    }

    /**
     * Cast to another integer type
     *
     * Casting to self has no effect
     *
     * @see LLVM.LLVMConstIntCast
     */
    public fun intcast(type: IntType, signExtend: Boolean): ConstantInt {
        val ref = LLVM.LLVMConstIntCast(ref, type.ref, signExtend.toLLVMBool())

        return ConstantInt(ref)
    }

    /**
     * Perform a select based on this current value
     *
     * This instruction only works on integers of size 1
     *
     * @see LLVM.LLVMConstSelect
     */
    public fun select(ifTrue: Value, ifFalse: Value): Value {
        require(getType().asIntType().getTypeWidth() == 1)

        val ref = LLVM.LLVMConstSelect(ref, ifTrue.ref, ifFalse.ref)

        return Value(ref)
    }
    //endregion Core::Values::Constants::ConstantExpressions
}

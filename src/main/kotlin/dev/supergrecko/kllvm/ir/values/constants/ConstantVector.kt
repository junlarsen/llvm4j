package dev.supergrecko.kllvm.ir.values.constants

import dev.supergrecko.kllvm.internal.contracts.Unreachable
import dev.supergrecko.kllvm.ir.TypeKind
import dev.supergrecko.kllvm.ir.Value
import dev.supergrecko.kllvm.ir.instructions.IntPredicate
import dev.supergrecko.kllvm.ir.types.IntType
import dev.supergrecko.kllvm.ir.values.Constant
import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public class ConstantVector internal constructor() : Value(), Constant {
    public constructor(llvmValue: LLVMValueRef) : this() {
        ref = llvmValue
    }

    /**
     * @see LLVM.LLVMConstVector
     */
    public constructor(values: List<Value>) : this() {
        val ptr = ArrayList(values.map { it.ref }).toTypedArray()

        ref = LLVM.LLVMConstVector(PointerPointer(*ptr), ptr.size)
    }

    /**
     * Get an element at specified [index] as a constant
     *
     * This is shared with [ConstantArray], [ConstantVector], [ConstantStruct]
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
    ): ConstantVector {
        require(getType().getTypeKind() == TypeKind.Integer)
        require(!(hasNSW && hasNSW)) { "Cannot negate with both NSW and NUW" }

        val ref = when (true) {
            hasNSW -> LLVM.LLVMConstNSWNeg(ref)
            hasNUW -> LLVM.LLVMConstNUWNeg(ref)
            else -> LLVM.LLVMConstNeg(ref)
        }

        return ConstantVector(ref)
    }

    /**
     * Invert each integer value using XOR
     *
     * This in short performs the same action as [neg] but instead of using
     * subtraction it uses a bitwise XOR where B is always true.
     *
     * @see LLVM.LLVMConstNot
     */
    public fun not(): ConstantVector {
        require(getType().getTypeKind() == TypeKind.Integer)

        val ref = LLVM.LLVMConstNot(ref)

        return ConstantVector(ref)
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
        rhs: ConstantInt,
        hasNUW: Boolean = false,
        hasNSW: Boolean = false
    ): ConstantVector {
        require(getType().getTypeKind() == TypeKind.Integer)
        require(rhs.getType().getTypeKind() == TypeKind.Integer)
        require(!(hasNSW && hasNSW)) { "Cannot add with both NSW and NUW" }

        val ref = when (true) {
            hasNSW -> LLVM.LLVMConstNSWAdd(ref, rhs.ref)
            hasNUW -> LLVM.LLVMConstNUWAdd(ref, rhs.ref)
            else -> LLVM.LLVMConstAdd(ref, rhs.ref)
        }

        return ConstantVector(ref)
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
        rhs: ConstantInt,
        hasNUW: Boolean = false,
        hasNSW: Boolean = false
    ): ConstantVector {
        require(getType().getTypeKind() == TypeKind.Integer)
        require(rhs.getType().getTypeKind() == TypeKind.Integer)
        require(!(hasNSW && hasNSW)) { "Cannot sub with both NSW and NUW" }

        val ref = when (true) {
            hasNSW -> LLVM.LLVMConstNSWSub(ref, rhs.ref)
            hasNUW -> LLVM.LLVMConstNUWSub(ref, rhs.ref)
            else -> LLVM.LLVMConstSub(ref, rhs.ref)
        }

        return ConstantVector(ref)
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
        rhs: ConstantInt,
        hasNUW: Boolean = false,
        hasNSW: Boolean = false
    ): ConstantVector {
        require(getType().getTypeKind() == TypeKind.Integer)
        require(rhs.getType().getTypeKind() == TypeKind.Integer)
        require(!(hasNSW && hasNSW)) { "Cannot sub with both NSW and NUW" }

        val ref = when (true) {
            hasNSW -> LLVM.LLVMConstNSWMul(ref, rhs.ref)
            hasNUW -> LLVM.LLVMConstNUWMul(ref, rhs.ref)
            else -> LLVM.LLVMConstMul(ref, rhs.ref)
        }

        return ConstantVector(ref)
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
    public fun div(
        rhs: ConstantVector,
        exact: Boolean,
        unsigned: Boolean
    ): ConstantVector {
        require(getType().getTypeKind() == TypeKind.Integer)
        require(rhs.getType().getTypeKind() == TypeKind.Integer)

        val ref = when (true) {
            unsigned && exact -> LLVM.LLVMConstExactUDiv(ref, rhs.ref)
            !unsigned && exact -> LLVM.LLVMConstExactSDiv(ref, rhs.ref)
            unsigned && !exact -> LLVM.LLVMConstUDiv(ref, rhs.ref)
            !unsigned && !exact -> LLVM.LLVMConstSDiv(ref, rhs.ref)
            else -> throw Unreachable()
        }

        return ConstantVector(ref)
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
     * TODO: Find a way to determine if types is unsigned
     */
    public fun sdiv(
        rhs: ConstantVector,
        exact: Boolean
    ): ConstantVector = div(rhs, exact, false)

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
     * TODO: Find a way to determine if types is unsigned
     */
    public fun udiv(
        rhs: ConstantVector,
        exact: Boolean
    ): ConstantVector = div(rhs, exact, true)

    /**
     * Get the remainder from the unsigned division for the two operands
     *
     * Taking the remainder of a division by zero is undefined behavior.
     *
     * If [unsigned] is present, URem will be used
     */
    public fun rem(rhs: ConstantVector, unsigned: Boolean): ConstantVector {
        require(getType().getTypeKind() == TypeKind.Integer)
        require(rhs.getType().getTypeKind() == TypeKind.Integer)

        val ref = if (unsigned) {
            LLVM.LLVMConstURem(ref, rhs.ref)
        } else {
            LLVM.LLVMConstSRem(ref, rhs.ref)
        }

        return ConstantVector(ref)
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
    public fun and(rhs: ConstantVector): ConstantVector {
        require(getType().getTypeKind() == TypeKind.Integer)
        require(rhs.getType().getTypeKind() == TypeKind.Integer)

        val ref = LLVM.LLVMConstAnd(ref, rhs.ref)

        return ConstantVector(ref)
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
    public fun or(rhs: ConstantVector): ConstantVector {
        require(getType().getTypeKind() == TypeKind.Integer)
        require(rhs.getType().getTypeKind() == TypeKind.Integer)

        val ref = LLVM.LLVMConstOr(ref, rhs.ref)

        return ConstantVector(ref)
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
    public fun xor(rhs: ConstantVector): ConstantVector {
        require(getType().getTypeKind() == TypeKind.Integer)
        require(rhs.getType().getTypeKind() == TypeKind.Integer)

        val ref = LLVM.LLVMConstXor(ref, rhs.ref)

        return ConstantVector(ref)
    }

    /**
     * Perform logical comparison for the two operands
     *
     * This method receives a [predicate] which determines which logical
     * comparison method shall be used for the comparison.
     */
    public fun cmp(
        predicate: IntPredicate,
        rhs: ConstantVector
    ): ConstantVector {
        require(getType().getTypeKind() == TypeKind.Integer)
        require(rhs.getType().getTypeKind() == TypeKind.Integer)

        val ref = LLVM.LLVMConstICmp(predicate.value, ref, rhs.ref)

        return ConstantVector(ref)
    }

    /**
     * Shift the operand to the left [bits] number of bits
     *
     * LLVM-C does not support NUW/NSW attributes for this operation
     */
    public fun shl(rhs: ConstantVector): ConstantVector {
        require(getType().getTypeKind() == TypeKind.Integer)
        require(rhs.getType().getTypeKind() == TypeKind.Integer)

        val ref = LLVM.LLVMConstShl(ref, rhs.ref)

        return ConstantVector(ref)
    }

    /**
     * Logically shift the operand to the right [bits] number of bits with
     * zero fill
     *
     * LLVM-C does not support NUW/NSW attributes for this operation
     */
    public fun lshr(bits: ConstantVector): ConstantVector {
        require(isConstant() && bits.isConstant())
        require(getType().getTypeKind() == TypeKind.Integer)
        require(bits.getType().getTypeKind() == TypeKind.Integer)

        val ref = LLVM.LLVMConstLShr(ref, bits.ref)

        return ConstantVector(ref)
    }

    /**
     * Arithmetically shift the operand to the right [bits] number with sign
     * extension
     *
     * LLVM-C does nt support the 'exact' attribute for this operation
     */
    public fun ashr(bits: ConstantVector): ConstantVector {
        require(isConstant() && bits.isConstant())
        require(getType().getTypeKind() == TypeKind.Integer)
        require(bits.getType().getTypeKind() == TypeKind.Integer)

        val ref = LLVM.LLVMConstAShr(ref, bits.ref)

        return ConstantVector(ref)
    }

    /**
     * Truncates this operand to the type [type]
     *
     * The bit size of this must be larger than the bit size of [type]. Equal
     * sizes are not allowed
     */
    public fun trunc(type: IntType): ConstantVector {
        require(isConstant())
        require(getType().getTypeKind() == TypeKind.Integer)

        val ref = LLVM.LLVMConstTrunc(ref, type.ref)

        return ConstantVector(ref)
    }

    /**
     * Sign extend this value to type [type]
     *
     * The bit size of this must be tinier than the bit size of the
     * destination type
     */
    public fun sext(type: IntType): ConstantVector {
        require(isConstant())
        require(getType().getTypeKind() == TypeKind.Integer)

        val ref = LLVM.LLVMConstSExt(ref, type.ref)

        return ConstantVector(ref)
    }

    /**
     * Zero extend this value to type [type]
     *
     * The bit size of this must be tinier than the bit size of the
     * destination type
     */
    public fun zext(type: IntType): ConstantVector {
        require(isConstant())
        require(getType().getTypeKind() == TypeKind.Integer)

        val ref = LLVM.LLVMConstZExt(ref, type.ref)

        return ConstantVector(ref)
    }
    //endregion Core::Values::Constants::ConstantExpressions
}

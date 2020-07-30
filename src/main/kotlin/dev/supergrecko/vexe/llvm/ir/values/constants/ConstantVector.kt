package dev.supergrecko.vexe.llvm.ir.values.constants

import dev.supergrecko.vexe.llvm.internal.contracts.Unreachable
import dev.supergrecko.vexe.llvm.internal.util.toLLVMBool
import dev.supergrecko.vexe.llvm.ir.IntPredicate
import dev.supergrecko.vexe.llvm.ir.Value
import dev.supergrecko.vexe.llvm.ir.types.FloatType
import dev.supergrecko.vexe.llvm.ir.types.IntType
import dev.supergrecko.vexe.llvm.ir.values.ConstantValue
import dev.supergrecko.vexe.llvm.ir.values.traits.CompositeValue
import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public class ConstantVector internal constructor() : ConstantValue(),
    CompositeValue {
    public constructor(llvmRef: LLVMValueRef) : this() {
        ref = llvmRef
    }

    //region Core::Values::Constants::ConstantExpressions
    /**
     * Create a new vector of a list of values
     *
     * @see LLVM.LLVMConstVector
     */
    public constructor(values: List<Value>) : this() {
        val ptr = PointerPointer(*values.map { it.ref }.toTypedArray())

        ref = LLVM.LLVMConstVector(ptr, values.size)
    }

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
    public fun getNeg(
        hasNUW: Boolean = false,
        hasNSW: Boolean = false
    ): ConstantVector {
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
     * This in short performs the same action as [getNeg] but instead of using
     * subtraction it uses a bitwise XOR where B is always true.
     *
     * @see LLVM.LLVMConstNot
     */
    public fun getNot(): ConstantVector {
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
    public fun getAdd(
        rhs: ConstantVector,
        hasNUW: Boolean = false,
        hasNSW: Boolean = false
    ): ConstantVector {
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
     *
     * @see LLVM.LLVMConstSub
     */
    public fun getSub(
        rhs: ConstantVector,
        hasNUW: Boolean = false,
        hasNSW: Boolean = false
    ): ConstantVector {
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
     *
     * @see LLVM.LLVMConstMul
     */
    public fun getMul(
        rhs: ConstantVector,
        hasNUW: Boolean = false,
        hasNSW: Boolean = false
    ): ConstantVector {
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
     * @see LLVM.LLVMConstUDiv
     * @see LLVM.LLVMConstSDiv
     */
    public fun getDiv(
        rhs: ConstantVector,
        exact: Boolean,
        unsigned: Boolean
    ): ConstantVector {
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
     * @see LLVM.LLVMConstSDiv
     */
    public fun getSDiv(
        rhs: ConstantVector,
        exact: Boolean
    ): ConstantVector = getDiv(rhs, exact, false)

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
    public fun getUDiv(
        rhs: ConstantVector,
        exact: Boolean
    ): ConstantVector = getDiv(rhs, exact, true)

    /**
     * Get the remainder from the unsigned division for the two operands
     *
     * Taking the remainder of a division by zero is undefined behavior.
     *
     * If [unsigned] is present, URem will be used
     *
     * @see LLVM.LLVMConstSRem
     * @see LLVM.LLVMConstURem
     */
    public fun getRem(rhs: ConstantVector, unsigned: Boolean): ConstantVector {
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
     *
     * @see LLVM.LLVMConstAnd
     */
    public fun getAnd(rhs: ConstantVector): ConstantVector {
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
     *
     * @see LLVM.LLVMConstOr
     */
    public fun getOr(rhs: ConstantVector): ConstantVector {
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
     *
     * @see LLVM.LLVMConstXor
     */
    public fun getXor(rhs: ConstantVector): ConstantVector {
        val ref = LLVM.LLVMConstXor(ref, rhs.ref)

        return ConstantVector(ref)
    }

    /**
     * Perform logical comparison for the two operands
     *
     * This method receives a [predicate] which determines which logical
     * comparison method shall be used for the comparison.
     *
     * @see LLVM.LLVMConstICmp
     */
    public fun getICmp(
        predicate: IntPredicate,
        rhs: ConstantVector
    ): ConstantVector {
        val ref = LLVM.LLVMConstICmp(predicate.value, ref, rhs.ref)

        return ConstantVector(ref)
    }

    /**
     * Shift the operand to the left [rhs] number of bits
     *
     * LLVM-C does not support NUW/NSW attributes for this operation
     *
     * @see LLVM.LLVMConstShl
     */
    public fun getShl(rhs: ConstantVector): ConstantVector {
        val ref = LLVM.LLVMConstShl(ref, rhs.ref)

        return ConstantVector(ref)
    }

    /**
     * Logically shift the operand to the right [bits] number of bits with
     * zero fill
     *
     * LLVM-C does not support NUW/NSW attributes for this operation
     *
     * @see LLVM.LLVMConstLShr
     */
    public fun getLShr(bits: ConstantVector): ConstantVector {
        val ref = LLVM.LLVMConstLShr(ref, bits.ref)

        return ConstantVector(ref)
    }

    /**
     * Arithmetically shift the operand to the right [bits] number with sign
     * extension
     *
     * LLVM-C does nt support the 'exact' attribute for this operation
     *
     * @see LLVM.LLVMConstAShr
     */
    public fun getAShr(bits: ConstantVector): ConstantVector {
        val ref = LLVM.LLVMConstAShr(ref, bits.ref)

        return ConstantVector(ref)
    }

    /**
     * Truncates this operand to the type [type]
     *
     * The bit size of this must be larger than the bit size of [type]. Equal
     * sizes are not allowed
     *
     * @see LLVM.LLVMConstTrunc
     */
    public fun getTrunc(type: IntType): ConstantVector {
        val ref = LLVM.LLVMConstTrunc(ref, type.ref)

        return ConstantVector(ref)
    }

    /**
     * Sign extend this value to type [type]
     *
     * The bit size of this must be tinier than the bit size of the
     * destination type
     *
     * @see LLVM.LLVMConstSExt
     */
    public fun getSExt(type: IntType): ConstantVector {
        val ref = LLVM.LLVMConstSExt(ref, type.ref)

        return ConstantVector(ref)
    }

    /**
     * Zero extend this value to type [type]
     *
     * The bit size of this must be tinier than the bit size of the
     * destination type
     *
     * @see LLVM.LLVMConstZExt
     */
    public fun getZExt(type: IntType): ConstantVector {
        val ref = LLVM.LLVMConstZExt(ref, type.ref)

        return ConstantVector(ref)
    }

    /**
     * Cast to another integer type
     *
     * @see LLVM.LLVMConstIntCast
     */
    public fun getIntCast(type: IntType, signExtend: Boolean): ConstantVector {
        val ref = LLVM.LLVMConstIntCast(ref, type.ref, signExtend.toLLVMBool())

        return ConstantVector(ref)
    }

    /**
     * Cast to another float type
     *
     * @see LLVM.LLVMConstFPCast
     */
    public fun getFPCast(type: FloatType): ConstantVector {
        val ref = LLVM.LLVMConstFPCast(ref, type.ref)

        return ConstantVector(ref)
    }

    /**
     * Perform a select based on this current value
     *
     * This instruction only works on integers of size 1
     *
     * @see LLVM.LLVMConstSelect
     */
    public fun getSelect(ifTrue: Value, ifFalse: Value): ConstantVector {
        val ref = LLVM.LLVMConstSelect(ref, ifTrue.ref, ifFalse.ref)

        return ConstantVector(ref)
    }

    /**
     * Extract an element from a vector at specified [index]
     *
     * @see LLVM.LLVMConstExtractElement
     */
    public fun getExtractElement(index: ConstantInt): Value {
        val ref = LLVM.LLVMConstExtractElement(ref, index.ref)

        return Value(ref)
    }

    /**
     * Insert an element at [index] in this vector
     *
     * The [value] must be of the same type as what this vector holds.
     */
    public fun getInsertElement(
        index: ConstantInt,
        value: Value
    ): ConstantVector {
        // LLVM has InsertElement(this, value, index) which is why args are
        // swapped
        val ref = LLVM.LLVMConstInsertElement(ref, value.ref, index.ref)

        return ConstantVector(ref)
    }

    /**
     * Construct a permutation of both vectors
     *
     * This returns a vector with the same element type as the input length
     * that is the same as the shuffle mask.
     */
    public fun getShuffleVector(
        other: ConstantVector,
        mask: ConstantVector
    ): ConstantVector {
        val ref = LLVM.LLVMConstShuffleVector(ref, other.ref, mask.ref)

        return ConstantVector(ref)
    }
    //endregion Core::Values::Constants::ConstantExpressions
}

package dev.supergrecko.kllvm.core.values

import dev.supergrecko.kllvm.core.typedefs.Value
import dev.supergrecko.kllvm.core.types.IntType
import dev.supergrecko.kllvm.utils.toInt
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public class IntValue internal constructor() : Value() {
    public constructor(llvmValue: LLVMValueRef) : this() {
        ref = llvmValue
    }

    /**
     * Create a new integer value of type [type]
     *
     * This creates a new integer from [type] with [value]. You can decide if
     * this is signed with [signExtend].
     *
     * @see [LLVM.LLVMConstInt]
     */
    public constructor(
        type: IntType,
        value: Long,
        signExtend: Boolean
    ) : this() {
        ref = LLVM.LLVMConstInt(type.ref, value, signExtend.toInt())
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

    //region Core::Values::Constants::ConstantExpressions
    /**
     * Negate the constant value
     *
     * This value is not modified, but it returns a new value with the result of
     * the operation.
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
    public fun neg(hasNUW: Boolean = false, hasNSW: Boolean = false): IntValue {
        require(isConstant())
        require(!(hasNSW && hasNSW)) { "Cannot negate with both NSW and NUW" }

        val ref = when (true) {
            hasNSW -> LLVM.LLVMConstNSWNeg(ref)
            hasNUW -> LLVM.LLVMConstNUWNeg(ref)
            else -> LLVM.LLVMConstNeg(ref)
        }

        return IntValue(ref)
    }

    /**
     * Invert the integer value using XOR
     *
     * This value is not modified, but it returns a new value with the result of
     * the operation.
     *
     * This in short performs the same action as [neg] but instead of using
     * subtraction it uses a bitwise XOR where B is always true.
     *
     * @see LLVM.LLVMConstNot
     */
    public fun not(): IntValue {
        require(isConstant())

        val ref = LLVM.LLVMConstNot(ref)

        return IntValue(ref)
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
        v: IntValue,
        hasNUW: Boolean = false,
        hasNSW: Boolean = false
    ): IntValue {
        require(isConstant() && v.isConstant())
        require(!(hasNSW && hasNSW)) { "Cannot add with both NSW and NUW" }

        val ref = when (true) {
            hasNSW -> LLVM.LLVMConstNSWAdd(ref, v.ref)
            hasNUW -> LLVM.LLVMConstNUWAdd(ref, v.ref)
            else -> LLVM.LLVMConstAdd(ref, v.ref)
        }

        return IntValue(ref)
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
        v: IntValue,
        hasNUW: Boolean = false,
        hasNSW: Boolean = false
    ): IntValue {
        require(isConstant() && v.isConstant())
        require(!(hasNSW && hasNSW)) { "Cannot sub with both NSW and NUW" }

        val ref = when (true) {
            hasNSW -> LLVM.LLVMConstNSWSub(ref, v.ref)
            hasNUW -> LLVM.LLVMConstNUWSub(ref, v.ref)
            else -> LLVM.LLVMConstSub(ref, v.ref)
        }

        return IntValue(ref)
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
        v: IntValue,
        hasNUW: Boolean = false,
        hasNSW: Boolean = false
    ): IntValue {
        require(isConstant() && v.isConstant())
        require(!(hasNSW && hasNSW)) { "Cannot sub with both NSW and NUW" }

        val ref = when (true) {
            hasNSW -> LLVM.LLVMConstNSWMul(ref, v.ref)
            hasNUW -> LLVM.LLVMConstNUWMul(ref, v.ref)
            else -> LLVM.LLVMConstMul(ref, v.ref)
        }

        return IntValue(ref)
    }
    //endregion Core::Values::Constants::ConstantExpressions
}

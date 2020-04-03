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
     * @see [LLVM.LLVMConstIntOfArbitraryPrecision]
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
     * Negate the value
     *
     * @see [LLVM.LLVMConstNeg]
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
     * @see [LLVM.LLVMConstNot]
     */
    public fun not(): IntValue {
        require(isConstant())

        val ref = LLVM.LLVMConstNot(ref)

        return IntValue(ref)
    }

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
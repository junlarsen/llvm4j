package dev.supergrecko.vexe.llvm.ir.values.constants

import dev.supergrecko.vexe.llvm.internal.util.fromLLVMBool
import dev.supergrecko.vexe.llvm.ir.RealPredicate
import dev.supergrecko.vexe.llvm.ir.Value
import dev.supergrecko.vexe.llvm.ir.types.FloatType
import dev.supergrecko.vexe.llvm.ir.types.IntType
import dev.supergrecko.vexe.llvm.ir.values.ConstantValue
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public class ConstantFloat internal constructor() : ConstantValue() {
    public constructor(llvmRef: LLVMValueRef) : this() {
        ref = llvmRef
    }

    //region Core::Values::Constants::ScalarConstants
    /**
     * Create a new constant float of a [type] with the provided [value]
     *
     * @see LLVM.LLVMConstReal
     */
    public constructor(type: FloatType, value: Double) : this() {
        ref = LLVM.LLVMConstReal(type.ref, value)
    }

    /**
     * Obtains the double value for a floating point const value
     *
     * @see LLVM.LLVMConstRealGetDouble
     */
    public fun getDouble(): Double {
        val buf = IntArray(1)

        return LLVM.LLVMConstRealGetDouble(ref, buf)
    }

    /**
     * Determine whether [getDouble] will lose precision when converting
     */
    public fun getDoubleLosesPrecision(): Boolean {
        val buf = IntArray(1)

        LLVM.LLVMConstRealGetDouble(ref, buf)

        return buf.first().fromLLVMBool()
    }
    //endregion Core::Values::Constants::ScalarConstants

    //region Core::Values::Constants::ConstantExpressions
    /**
     * Negate this operand
     *
     * LLVM doesn't actually have a neg instruction, but it's implemented using
     * subtraction. It subtracts the value of max value of the types of the
     * value
     *
     * @see LLVM.LLVMConstFNeg
     */
    public fun getNeg(): ConstantFloat {
        val ref = LLVM.LLVMConstFNeg(ref)

        return ConstantFloat(ref)
    }

    /**
     * Perform addition for the two operands
     *
     * If the sum has unsigned overflow, the result returned is the
     * mathematical result modulo 2^n, where n is the bit width of the result.
     *
     * @see LLVM.LLVMConstFAdd
     */
    public fun getAdd(rhs: ConstantFloat): ConstantFloat {
        val ref = LLVM.LLVMConstFAdd(ref, rhs.ref)

        return ConstantFloat(ref)
    }

    /**
     * Subtract another float from this float
     *
     * If the sum has unsigned overflow, the result returned is the
     * mathematical result modulo 2n, where n is the bit width of the result.
     *
     * @see LLVM.LLVMConstFSub
     */
    public fun getSub(rhs: ConstantFloat): ConstantFloat {
        val ref = LLVM.LLVMConstFSub(ref, rhs.ref)

        return ConstantFloat(ref)
    }

    /**
     * Perform multiplication for the two operands
     *
     * @see LLVM.LLVMConstFMul
     */
    public fun getMul(rhs: ConstantFloat): ConstantFloat {
        val ref = LLVM.LLVMConstFMul(ref, rhs.ref)

        return ConstantFloat(ref)
    }

    /**
     * Perform division for the two operands
     *
     * @see LLVM.LLVMConstFDiv
     */
    public fun getDiv(rhs: ConstantFloat): ConstantFloat {
        val ref = LLVM.LLVMConstFDiv(ref, rhs.ref)

        return ConstantFloat(ref)
    }

    /**
     * Get the remainder from the division of the two operands
     *
     * @see LLVM.LLVMFRem
     */
    public fun getRem(rhs: ConstantFloat): ConstantFloat {
        val ref = LLVM.LLVMConstFRem(ref, rhs.ref)

        return ConstantFloat(ref)
    }

    /**
     * Perform logical comparison for the two operands
     *
     * This method receives a [predicate] which determines which logical
     * comparison method shall be used for the comparison.
     *
     * @see LLVM.LLVMConstFCmp
     */
    public fun getFCmp(
        predicate: RealPredicate,
        rhs: ConstantFloat
    ): ConstantFloat {
        val ref = LLVM.LLVMConstFCmp(predicate.value, ref, rhs.ref)

        return ConstantFloat(ref)
    }

    /**
     * Truncates this operand to the type [type]
     *
     * The bit size of this must be larger than the bit size of [type]. Equal
     * sizes are not allowed
     *
     * @see LLVM.LLVMConstFPTrunc
     */
    public fun getTrunc(type: FloatType): ConstantFloat {
        val ref = LLVM.LLVMConstFPTrunc(ref, type.ref)

        return ConstantFloat(ref)
    }

    /**
     * Extend this value to type [type]
     *
     * The bit size of this must be tinier than the bit size of the
     * destination type
     *
     * @see LLVM.LLVMConstFPExt
     */
    public fun getExt(type: FloatType): ConstantFloat {
        val ref = LLVM.LLVMConstFPExt(ref, type.ref)

        return ConstantFloat(ref)
    }

    /**
     * Conversion to signed integer type
     *
     * @see LLVM.LLVMConstFPToSI
     */
    public fun getFPToSI(type: IntType): ConstantInt {
        val ref = LLVM.LLVMConstFPToSI(ref, type.ref)

        return ConstantInt(ref)
    }

    /**
     * Conversion to unsigned integer type
     *
     * @see LLVM.LLVMConstFPToUI
     */
    public fun getFPToUI(type: IntType): ConstantInt {
        val ref = LLVM.LLVMConstFPToUI(ref, type.ref)

        return ConstantInt(ref)
    }

    /**
     * Cast to another float type
     *
     * @see LLVM.LLVMConstFPCast
     */
    public fun getFPCast(type: FloatType): ConstantFloat {
        val ref = LLVM.LLVMConstFPCast(ref, type.ref)

        return ConstantFloat(ref)
    }
    //endregion Core::Values::Constants::ConstantExpressions
}

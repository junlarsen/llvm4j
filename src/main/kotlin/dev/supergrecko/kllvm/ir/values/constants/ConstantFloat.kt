package dev.supergrecko.kllvm.ir.values.constants

import dev.supergrecko.kllvm.internal.util.toBoolean
import dev.supergrecko.kllvm.ir.Value
import dev.supergrecko.kllvm.ir.types.FloatType
import dev.supergrecko.kllvm.ir.values.Constant
import org.bytedeco.javacpp.IntPointer
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public class ConstantFloat internal constructor() : Value(), Constant {
    public constructor(llvmValue: LLVMValueRef) : this() {
        ref = llvmValue
    }

    public constructor(type: FloatType, value: Double) : this() {
        ref = LLVM.LLVMConstReal(type.ref, value)
    }

    //region Core::Values::Constants::ScalarConstants
    /**
     * Obtains the double value for a floating point const value
     *
     * The returned [Pair] contains the obtained value and whether precision was lost or not.
     */
    public fun getDouble(): Pair<Double, Boolean> {
        val ptr = IntPointer()
        val double = LLVM.LLVMConstRealGetDouble(ref, ptr)

        return (double) to (ptr.get().toBoolean())
    }
    //endregion Core::Values::Constants::ScalarConstants

    //region Core::Values::Constants::ConstantExpressions
    /**
     * Negate this float
     *
     * This value is not modified, but it returns a new value with the result of
     * the operation.
     */
    public fun neg(): ConstantFloat {
        require(isConstant())

        val ref = LLVM.LLVMConstFNeg(ref)

        return ConstantFloat(ref)
    }

    /**
     * Add another float to this float
     *
     * This value is not modified, but it returns a new value with the result of
     * the operation.
     */
    public fun add(v: ConstantFloat): ConstantFloat {
        require(isConstant())
        require(getType().getTypeKind() == v.getType().getTypeKind())

        val ref = LLVM.LLVMConstFAdd(ref, v.ref)

        return ConstantFloat(ref)
    }

    /**
     * Subtract another float from this float
     *
     * This value is not modified, but it returns a new value with the result of
     * the operation.
     */
    public fun sub(v: ConstantFloat): ConstantFloat {
        require(isConstant())
        require(getType().getTypeKind() == v.getType().getTypeKind())

        val ref = LLVM.LLVMConstFSub(ref, v.ref)

        return ConstantFloat(ref)
    }

    /**
     * Multiply this float with another float
     *
     * This value is not modified, but it returns a new value with the result of
     * the operation.
     */
    public fun mul(v: ConstantFloat): ConstantFloat {
        require(isConstant())
        require(getType().getTypeKind() == v.getType().getTypeKind())

        val ref = LLVM.LLVMConstMul(ref, v.ref)

        return ConstantFloat(ref)
    }

    /**
     * Perform division with another float
     *
     * TODO: Find a way to return something more exact than Value
     */
    public fun div(v: ConstantFloat): ConstantFloat {
        require(isConstant())
        require(getType().getTypeKind() == v.getType().getTypeKind())

        val ref = LLVM.LLVMConstFDiv(ref, v.ref)

        return ConstantFloat(ref)
    }
    //endregion Core::Values::Constants::ConstantExpressions
}

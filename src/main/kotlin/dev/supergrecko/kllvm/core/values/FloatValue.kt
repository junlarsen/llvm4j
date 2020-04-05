package dev.supergrecko.kllvm.core.values

import dev.supergrecko.kllvm.core.typedefs.Value
import dev.supergrecko.kllvm.core.types.FloatType
import dev.supergrecko.kllvm.utils.toBoolean
import org.bytedeco.javacpp.IntPointer
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public class FloatValue internal constructor() : Value() {
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
    public fun neg(): FloatValue {
        require(isConstant())

        val ref = LLVM.LLVMConstFNeg(ref)

        return FloatValue(ref)
    }

    /**
     * Add another float to this float
     *
     * This value is not modified, but it returns a new value with the result of
     * the operation.
     */
    public fun add(v: FloatValue): FloatValue {
        require(isConstant())
        require(getType().getTypeKind() == v.getType().getTypeKind())

        val ref = LLVM.LLVMConstFAdd(ref, v.ref)

        return FloatValue(ref)
    }

    /**
     * Subtract another float from this float
     *
     * This value is not modified, but it returns a new value with the result of
     * the operation.
     */
    public fun sub(v: FloatValue): FloatValue {
        require(isConstant())
        require(getType().getTypeKind() == v.getType().getTypeKind())

        val ref = LLVM.LLVMConstFSub(ref, v.ref)

        return FloatValue(ref)
    }

    /**
     * Multiply this float with another float
     *
     * This value is not modified, but it returns a new value with the result of
     * the operation.
     */
    public fun mul(v: FloatValue): FloatValue {
        require(isConstant())
        require(getType().getTypeKind() == v.getType().getTypeKind())

        val ref = LLVM.LLVMConstMul(ref, v.ref)

        return FloatValue(ref)
    }

    /**
     * Perform division with another float
     *
     * TODO: Find a way to return something more exact than Value
     */
    public fun div(v: FloatValue): Value {
        require(isConstant())
        require(getType().getTypeKind() == v.getType().getTypeKind())

        val ref = LLVM.LLVMConstFDiv(ref, v.ref)

        return Value(ref)
    }
    //endregion Core::Values::Constants::ConstantExpressions
}
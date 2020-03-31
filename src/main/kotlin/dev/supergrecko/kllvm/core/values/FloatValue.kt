package dev.supergrecko.kllvm.core.values

import dev.supergrecko.kllvm.core.typedefs.Value
import dev.supergrecko.kllvm.utils.toBoolean
import org.bytedeco.javacpp.IntPointer
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public class FloatValue(llvmValue: LLVMValueRef) : Value(llvmValue) {
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
}
package dev.supergrecko.kllvm.core.values

import dev.supergrecko.kllvm.core.typedefs.Value
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public class IntValue(llvmValue: LLVMValueRef) : Value(llvmValue) {
    //region Core::Values::Constants::ScalarConstants
    public fun getIntZeroExtended(): Long {
        return LLVM.LLVMConstIntGetZExtValue(llvmValue)
    }

    public fun getIntSignExtended(): Long {
        return LLVM.LLVMConstIntGetSExtValue(llvmValue)
    }
    //endregion Core::Values::Constants::ScalarConstants
}
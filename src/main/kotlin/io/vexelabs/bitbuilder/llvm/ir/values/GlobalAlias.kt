package io.vexelabs.bitbuilder.llvm.ir.values

import io.vexelabs.bitbuilder.llvm.ir.Value
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public class GlobalAlias internal constructor() : GlobalValue() {
    public constructor(llvmRef: LLVMValueRef) : this() {
        ref = llvmRef
    }

    /**
     * Get the value this alias is an alias for
     *
     * @see LLVM.LLVMAliasGetAliasee
     */
    public fun getAliasOf(): Value {
        val value = LLVM.LLVMAliasGetAliasee(ref)

        return Value(value)
    }

    /**
     * Set the value this aliases
     *
     * @see LLVM.LLVMAliasSetAliasee
     */
    public fun setAliasOf(value: Value) {
        LLVM.LLVMAliasSetAliasee(ref, value.ref)
    }
}

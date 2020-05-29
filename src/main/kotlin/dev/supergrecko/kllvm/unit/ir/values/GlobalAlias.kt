package dev.supergrecko.kllvm.unit.ir.values

import dev.supergrecko.kllvm.unit.ir.Value
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public class GlobalAlias internal constructor() : GlobalValue() {
    /**
     * Construct a new Type from an LLVM pointer reference
     */
    public constructor(value: LLVMValueRef) : this() {
        ref = value
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

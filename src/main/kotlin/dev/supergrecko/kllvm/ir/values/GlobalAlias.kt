package dev.supergrecko.kllvm.ir.values

import dev.supergrecko.kllvm.ir.Value
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public class GlobalAlias internal constructor(): GlobalValue() {
    /**
     * Construct a new Type from an LLVM pointer reference
     */
    public constructor(value: LLVMValueRef) : this() {
        ref = value
    }

    /**
     * Manage the value this alias is an alias for
     *
     * @see LLVM.LLVMAliasGetAliasee
     * @see LLVM.LLVMAliasSetAliasee
     */
    public var aliasOf: Value
        get() {
            val value = LLVM.LLVMAliasGetAliasee(ref)

            return Value(value)
        }
        set(value) = LLVM.LLVMAliasSetAliasee(ref, value.ref)
}
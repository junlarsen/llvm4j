package dev.supergrecko.vexe.llvm.ir

import dev.supergrecko.vexe.llvm.internal.contracts.ContainsReference
import dev.supergrecko.vexe.llvm.internal.contracts.PointerIterator
import org.bytedeco.llvm.LLVM.LLVMUseRef
import org.bytedeco.llvm.global.LLVM

public class Use internal constructor() : ContainsReference<LLVMUseRef> {
    public override lateinit var ref: LLVMUseRef
        internal set

    /**
     * Construct a new Type from an LLVM pointer reference
     */
    public constructor(use: LLVMUseRef) : this() {
        ref = use
    }

    //region Core::Values::Usage
    /**
     * Get the next [Use] in the iterator
     *
     * @see LLVM.LLVMGetNextUse
     */
    public fun getNextUse(): Use? {
        val use = LLVM.LLVMGetNextUse(ref)

        return if (use != null) {
            Use(use)
        } else {
            null
        }
    }

    /**
     * Get the llvm::User from this use
     *
     * @see LLVM.LLVMGetUser
     */
    public fun getUser(): User {
        val user = LLVM.LLVMGetUser(ref)

        return User(user)
    }

    /**
     * Get the value this usage points to
     *
     * @see LLVM.LLVMGetUsedValue
     */
    public fun getUsedValue(): Value {
        val value = LLVM.LLVMGetUsedValue(ref)

        return Value(value)
    }
    //endregion Core::Values::Usage

    /**
     * Class to perform iteration over targets
     *
     * @see [PointerIterator]
     */
    public class Iterator(ref: LLVMUseRef) :
        PointerIterator<Use, LLVMUseRef>(
            start = ref,
            yieldNext = { LLVM.LLVMGetNextUse(it) },
            apply = { Use(it) }
        )
}

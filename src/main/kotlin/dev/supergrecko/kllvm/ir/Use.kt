package dev.supergrecko.kllvm.ir

import dev.supergrecko.kllvm.internal.contracts.LLVMIterable
import dev.supergrecko.kllvm.internal.contracts.NextIterator
import dev.supergrecko.kllvm.internal.util.wrap
import org.bytedeco.llvm.LLVM.LLVMUseRef
import org.bytedeco.llvm.global.LLVM

public class Use internal constructor() : LLVMIterable<Use> {
    internal lateinit var ref: LLVMUseRef

    /**
     * Construct a new Type from an LLVM pointer reference
     */
    public constructor(use: LLVMUseRef) : this() {
        ref = use
    }

    //region LLVMIterator
    public override fun iter(): UseIterator {
        return UseIterator()
    }

    public inner class UseIterator : NextIterator<Use> {
        /**
         * Get the next usage in the iterator
         *
         * This should be used with [Value.getFirstUse] as this continues the
         * underlying C++ iterator.
         */
        override fun next(): Use? {
            val use = LLVM.LLVMGetNextUse(ref)

            return wrap(use) { Use(it) }
        }
    }
    //endregion LLVMIterator

    //region Core::Values::Usage
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
}

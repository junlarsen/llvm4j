package io.vexelabs.bitbuilder.llvm.ir

import io.vexelabs.bitbuilder.llvm.internal.contracts.ContainsReference
import io.vexelabs.bitbuilder.llvm.internal.contracts.PointerIterator
import org.bytedeco.llvm.LLVM.LLVMUseRef
import org.bytedeco.llvm.global.LLVM

/**
 * Interface to llvm::Use
 *
 * This is notionally a two-dimensional linked list. It supports traversing
 * all of the uses for a particular value definition. It also supports
 * jumping directly to the used value when we arrive from the [User]'s
 * operands, and jumping directly to the [User] when we arrive from the
 * [Value]'s uses
 *
 * @see LLVMUseRef
 */
public class Use internal constructor() : ContainsReference<LLVMUseRef> {
    public override lateinit var ref: LLVMUseRef
        internal set

    public constructor(use: LLVMUseRef) : this() {
        ref = use
    }

    /**
     * Get the next [Use] in the iterator
     *
     * @see LLVM.LLVMGetNextUse
     */
    public fun getNextUse(): Use? {
        val use = LLVM.LLVMGetNextUse(ref)

        return use?.let { Use(it) }
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

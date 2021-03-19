package org.llvm4j.llvm4j

import org.bytedeco.llvm.LLVM.LLVMUseRef
import org.bytedeco.llvm.global.LLVM
import org.llvm4j.llvm4j.util.CorrespondsTo
import org.llvm4j.llvm4j.util.Owner
import org.llvm4j.optional.Option

/**
 * An edge between a [Value] and the [Value]s which uses this value.
 *
 * @see User
 *
 * TODO: Testing - Test once values are more usable (see LLVM test suite + asmparsers)
 * TODO: Research - Can [getUser] and [getUsedValue] return User instead of option?
 * TODO: Iterators - Use iterator
 *
 * @author Mats Larsen
 */
@CorrespondsTo("llvm::Use")
public class Use public constructor(ptr: LLVMUseRef) : Owner<LLVMUseRef> {
    public override val ref: LLVMUseRef = ptr

    public fun getUser(): Option<User> {
        val user = LLVM.LLVMGetUser(ref)

        return Option.of(user).map { User(it) }
    }

    public fun getUsedValue(): Option<Value> {
        val value = LLVM.LLVMGetUsedValue(ref)

        return Option.of(value).map { Value(it) }
    }
}

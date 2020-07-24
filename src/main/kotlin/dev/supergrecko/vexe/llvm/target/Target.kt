package dev.supergrecko.vexe.llvm.target

import dev.supergrecko.vexe.llvm.internal.contracts.ContainsReference
import dev.supergrecko.vexe.llvm.internal.contracts.PointerIterator
import dev.supergrecko.vexe.llvm.internal.util.fromLLVMBool
import org.bytedeco.javacpp.BytePointer
import org.bytedeco.llvm.LLVM.LLVMTargetRef
import org.bytedeco.llvm.global.LLVM

public class Target internal constructor() :
    ContainsReference<LLVMTargetRef> {
    public override lateinit var ref: LLVMTargetRef
        internal set

    public constructor(llvmRef: LLVMTargetRef) : this() {
        ref = llvmRef
    }

    //region Target
    /**
     * Get the next [Target] in the iterator
     *
     * @see LLVM.LLVMGetNextTarget
     */
    public fun getNextTarget(): Target? {
        val next = LLVM.LLVMGetNextTarget(ref)

        return next?.let { Target(it) }
    }

    /**
     * Get this target's name
     *
     * @see LLVM.LLVMGetTargetName
     */
    public fun getName(): String {
        return LLVM.LLVMGetTargetName(ref).string
    }

    /**
     * Get this target's description
     *
     * @see LLVM.LLVMGetTargetDescription
     */
    public fun getDescription(): String {
        return LLVM.LLVMGetTargetDescription(ref).string
    }

    /**
     * Does this target have a target machine?
     *
     * @see LLVM.LLVMTargetHasTargetMachine
     */
    public fun hasTargetMachine(): Boolean {
        return LLVM.LLVMTargetHasTargetMachine(ref).fromLLVMBool()
    }

    /**
     * Does this target have JIT support?
     *
     * @see LLVM.LLVMTargetHasJIT
     */
    public fun hasJIT(): Boolean {
        return LLVM.LLVMTargetHasJIT(ref).fromLLVMBool()
    }

    /**
     * Does this target have an asm backend?
     *
     * @see LLVM.LLVMTargetHasAsmBackend
     */
    public fun hasAsmBackend(): Boolean {
        return LLVM.LLVMTargetHasAsmBackend(ref).fromLLVMBool()
    }

    /**
     * Companion object for constructing targets which may fail upon
     * construction. We use this over secondary constructors solely because
     * they may fail.
     *
     * These constructors also have clashing overloads which would make
     * constructor overloading obsolete
     */
    public companion object {
        /**
         * Create a target from a name if it exists
         *
         * @see LLVM.LLVMGetTargetFromName
         */
        public fun createFromName(name: String): Target {
            val target = LLVM.LLVMGetTargetFromName(name)

            return if (target != null) {
                Target(target)
            } else {
                throw IllegalArgumentException(
                    "The specified target could " +
                            "not be found"
                )
            }
        }

        /**
         * Create a target from a target triple
         *
         * @see LLVM.LLVMGetTargetFromTriple
         */
        public fun createFromTriple(triple: String): Target {
            val out = LLVMTargetRef()
            val err = BytePointer(0L)
            val t = BytePointer(triple)
            val result = LLVM.LLVMGetTargetFromTriple(t, out, err)

            return if (result == 0) {
                Target(out)
            } else {
                throw RuntimeException(err.string)
            }
        }
    }
    //endregion Target

    /**
     * Class to perform iteration over targets
     *
     * @see [PointerIterator]
     */
    public class Iterator(ref: LLVMTargetRef) :
        PointerIterator<Target, LLVMTargetRef>(
            start = ref,
            yieldNext = { LLVM.LLVMGetNextTarget(it) },
            apply = { Target(it) }
        )
}

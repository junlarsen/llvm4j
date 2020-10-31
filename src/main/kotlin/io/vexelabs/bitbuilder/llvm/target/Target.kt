package io.vexelabs.bitbuilder.llvm.target

import io.vexelabs.bitbuilder.llvm.internal.contracts.ContainsReference
import io.vexelabs.bitbuilder.llvm.internal.contracts.PointerIterator
import io.vexelabs.bitbuilder.internal.fromLLVMBool
import io.vexelabs.bitbuilder.internal.resourceScope
import io.vexelabs.bitbuilder.internal.toResource
import org.bytedeco.javacpp.BytePointer
import org.bytedeco.llvm.LLVM.LLVMTargetRef
import org.bytedeco.llvm.global.LLVM

/**
 * Interface to llvm::Target
 *
 * A target is a backend LLVM may compile to. The target provides necessary
 * information for what runtime compilers are available.
 *
 * @see LLVMTargetRef
 */
public class Target internal constructor() :
    ContainsReference<LLVMTargetRef> {
    public override lateinit var ref: LLVMTargetRef
        internal set

    public constructor(llvmRef: LLVMTargetRef) : this() {
        ref = llvmRef
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
         * @throws IllegalArgumentException If the target could not be found
         *
         * @see LLVM.LLVMGetTargetFromName
         */
        public fun createFromName(name: String): Target {
            val target = LLVM.LLVMGetTargetFromName(name)

            return if (target != null) {
                Target(target)
            } else {
                throw IllegalArgumentException(
                    "The specified target could not be found"
                )
            }
        }

        /**
         * Create a target from a target triple
         *
         * @throws IllegalArgumentException If the target could not be found
         *
         * @see LLVM.LLVMGetTargetFromTriple
         */
        public fun createFromTriple(triple: String): Target {
            val buf = BytePointer(256).toResource()

            return resourceScope(buf) {
                val outTarget = LLVMTargetRef()
                val targetTriple = BytePointer(triple)
                val result = LLVM.LLVMGetTargetFromTriple(targetTriple, outTarget, it)

                if (result != 0) {
                    outTarget.deallocate()
                    throw IllegalArgumentException(it.string)
                }

                return@resourceScope Target(outTarget)
            }
        }
    }

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

package dev.supergrecko.vexe.llvm.ir

import dev.supergrecko.vexe.llvm.internal.contracts.Unreachable
import dev.supergrecko.vexe.llvm.internal.contracts.Validatable
import dev.supergrecko.vexe.llvm.internal.util.fromLLVMBool
import dev.supergrecko.vexe.llvm.internal.util.wrap
import dev.supergrecko.vexe.llvm.ir.values.traits.DebugLocationValue
import org.bytedeco.javacpp.SizeTPointer
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public open class Instruction internal constructor() : Value(),
    DebugLocationValue, Validatable {
    public override var valid = true

    public constructor(llvmRef: LLVMValueRef) : this() {
        ref = llvmRef
    }

    //region Core::Instructions
    /**
     * Determine if this instruction has metadata
     *
     * @see LLVM.LLVMHasMetadata
     */
    public fun hasMetadata(): Boolean {
        return LLVM.LLVMHasMetadata(ref).fromLLVMBool()
    }

    /**
     * Get the metadata for this instruction
     *
     * If the instruction does not have metadata, an exception will be thrown
     *
     * @see LLVM.LLVMGetMetadata
     */
    public fun getMetadata(kind: Int): Metadata {
        require(hasMetadata()) {
            "This instruction does not have any metadata attached"
        }

        val value = LLVM.LLVMGetMetadata(ref, kind)
        val md = LLVM.LLVMValueAsMetadata(value)

        return Metadata(md)
    }

    /**
     * Set the metadata for this instruction
     *
     * TODO: Find replacement for the context used in MetadataAsValue
     *
     * @see LLVM.LLVMSetMetadata
     */
    public fun setMetadata(kind: Int, metadata: Metadata) {
        val value = LLVM.LLVMMetadataAsValue(
            getContext().ref,
            metadata.ref
        )

        LLVM.LLVMSetMetadata(ref, kind, value)
    }

    /**
     * Get all the metadata for the instruction apart from debug location
     * metadata
     *
     * @see LLVM.LLVMInstructionGetAllMetadataOtherThanDebugLoc
     */
    public fun getAllMetadataExceptDebugLocations(): MetadataEntries {
        val size = SizeTPointer(0)
        val entries = LLVM.LLVMInstructionGetAllMetadataOtherThanDebugLoc(
            ref,
            size
        )

        return MetadataEntries(entries, size)
    }

    /**
     * Get the [BasicBlock] this instruction lives inside
     *
     * @see LLVM.LLVMGetInstructionParent
     */
    public fun getInstructionBlock(): BasicBlock? {
        val bb = LLVM.LLVMGetInstructionParent(ref)

        return wrap(bb) { BasicBlock(it) }
    }

    /**
     * Get the next instruction inside of the basic block this resides in
     *
     * If this is the last instruction in the block, then null is returned
     *
     * @see LLVM.LLVMGetNextInstruction
     */
    public fun getNextInstruction(): Instruction? {
        val inst = LLVM.LLVMGetNextInstruction(ref)

        return wrap(inst) { Instruction(it) }
    }

    /**
     * Get the first instruction inside of the basic block this resides in
     *
     * If this is the first instruction in the block, then null is returned
     *
     * @see LLVM.LLVMGetPreviousInstruction
     */
    public fun getPreviousInstruction(): Instruction? {
        val inst = LLVM.LLVMGetPreviousInstruction(ref)

        return wrap(inst) { Instruction(it) }
    }

    /**
     * Removes the instruction from the basic block it resides in
     *
     * @see LLVM.LLVMInstructionRemoveFromParent
     */
    public fun remove() {
        LLVM.LLVMInstructionRemoveFromParent(ref)
    }

    /**
     * Removes the instruction from the basic block it resides in and deletes
     * the reference
     *
     * @see LLVM.LLVMInstructionEraseFromParent
     */
    public fun delete() {
        valid = false

        LLVM.LLVMInstructionEraseFromParent(ref)
    }

    /**
     * Get the opcode for this instruction
     *
     * @see LLVM.LLVMGetInstructionOpcode
     */
    public fun getOpcode(): Opcode {
        val opcode = LLVM.LLVMGetInstructionOpcode(ref)

        return Opcode.values()
            .firstOrNull { it.value == opcode }
            ?: throw Unreachable()
    }

    /**
     * Clone the opcode
     *
     * The clone does not have a basic block attached and it does not have a
     * name either
     *
     * @see LLVM.LLVMInstructionClone
     */
    public fun clone(): Instruction {
        val clone = LLVM.LLVMInstructionClone(ref)

        return Instruction(clone)
    }

    /**
     * Determine if this instruction is a terminator instruction
     *
     * @see LLVM.LLVMIsATerminatorInst
     */
    public fun isTerminator(): Boolean {
        val inst = LLVM.LLVMIsATerminatorInst(ref)

        return inst != null
    }
    //endregion Core::Instructions

    //region Core::Instructions::Terminators
    /**
     * Get the number of successors that this terminator has
     *
     * @see LLVM.LLVMGetNumSuccessors
     */
    public fun getSuccessorCount(): Int {
        require(isTerminator()) {
            "This instruction is not a terminator"
        }

        return LLVM.LLVMGetNumSuccessors(ref)
    }

    /**
     * Get a successor at [index]
     *
     * @see LLVM.LLVMGetSuccessor
     */
    public fun getSuccessor(index: Int): BasicBlock? {
        require(isTerminator()) {
            "This instruction is not a terminator"
        }
        require(index < getSuccessorCount()) {
            "Out of bounds index. Index: $index, Count: ${getSuccessorCount()}"
        }

        val bb = LLVM.LLVMGetSuccessor(ref, index)

        return wrap(bb) { BasicBlock(it) }
    }

    /**
     * Set a successor at [index]
     *
     * @see LLVM.LLVMSetSuccessor
     */
    public fun setSuccessor(index: Int, block: BasicBlock) {
        require(isTerminator()) {
            "This instruction is not a terminator"
        }

        LLVM.LLVMSetSuccessor(ref, index, block.ref)
    }
    //endregion Core::Instructions::Terminators
}

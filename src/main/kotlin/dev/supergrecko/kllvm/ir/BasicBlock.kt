package dev.supergrecko.kllvm.ir

import dev.supergrecko.kllvm.internal.contracts.Iterator
import dev.supergrecko.kllvm.internal.contracts.LLVMIterable
import dev.supergrecko.kllvm.internal.contracts.NextIterator
import dev.supergrecko.kllvm.internal.contracts.PrevIterator
import dev.supergrecko.kllvm.internal.contracts.Validatable
import dev.supergrecko.kllvm.internal.util.wrap
import dev.supergrecko.kllvm.ir.instructions.Instruction
import dev.supergrecko.kllvm.ir.values.FunctionValue
import org.bytedeco.llvm.LLVM.LLVMBasicBlockRef
import org.bytedeco.llvm.global.LLVM

public class BasicBlock internal constructor() : Validatable,
    LLVMIterable<BasicBlock> {
    override var valid = true
    public lateinit var ref: LLVMBasicBlockRef

    /**
     * Construct a new Type from an LLVM pointer reference
     */
    public constructor(block: LLVMBasicBlockRef) : this() {
        ref = block
    }

    //region Core::BasicBlock
    /**
     * Create a new basic block without inserting it into a function
     *
     * @see LLVM.LLVMCreateBasicBlockInContext
     */
    public constructor(context: Context, name: String) : this() {
        ref = LLVM.LLVMCreateBasicBlockInContext(context.ref, name)
    }

    /**
     * Converts this value into a Value
     *
     * This is done by unwrapping the instance into a Value
     *
     * TODO: Research more about this cast
     *
     * @see LLVM.LLVMBasicBlockAsValue
     */
    public fun toValue(): Value {
        require(valid)

        val v = LLVM.LLVMBasicBlockAsValue(ref)

        return Value(v)
    }

    /**
     * Get the name of the basic block
     *
     * @see LLVM.LLVMGetBasicBlockName
     */
    public fun getName(): String {
        require(valid)

        return LLVM.LLVMGetBasicBlockName(ref).string
    }

    /**
     * Get the function this basic block resides in
     *
     * @see LLVM.LLVMGetBasicBlockParent
     */
    public fun getFunction(): FunctionValue {
        require(valid)

        val fn = LLVM.LLVMGetBasicBlockParent(ref)

        return FunctionValue(fn)
    }

    /**
     * Get the terminating instruction for this block, if it exists
     *
     * @see LLVM.LLVMGetBasicBlockTerminator
     */
    public fun getTerminator(): Instruction? {
        require(valid)

        val instr = LLVM.LLVMGetBasicBlockTerminator(ref)

        return wrap(instr) { Instruction(it) }
    }

    /**
     * Insert a basic block in front of this one in the function this block
     * resides in.
     *
     * @see LLVM.LLVMInsertBasicBlockInContext
     */
    public fun insertBefore(
        name: String,
        context: Context = Context.getGlobalContext()
    ): BasicBlock {
        require(valid)

        val bb = LLVM.LLVMInsertBasicBlockInContext(context.ref, ref, name)

        return BasicBlock(bb)
    }

    /**
     * Remove this block from the function it resides in and delete it
     *
     * @see LLVM.LLVMDeleteBasicBlock
     */
    public fun delete() {
        require(valid)

        LLVM.LLVMDeleteBasicBlock(ref)

        valid = false
    }

    /**
     * Removes this basic block from the function it resides in
     *
     * @see LLVM.LLVMRemoveBasicBlockFromParent
     */
    public fun remove() {
        LLVM.LLVMRemoveBasicBlockFromParent(ref)
    }

    /**
     * Move this basic block before the [other] block
     *
     * @see LLVM.LLVMMoveBasicBlockBefore
     */
    public fun moveBefore(other: BasicBlock) {
        require(valid && other.valid)

        LLVM.LLVMMoveBasicBlockBefore(ref, other.ref)
    }

    /**
     * Move this basic block after the [other] block
     *
     * @see LLVM.LLVMMoveBasicBlockAfter
     */
    public fun moveAfter(other: BasicBlock) {
        require(valid && other.valid)

        LLVM.LLVMMoveBasicBlockAfter(ref, other.ref)
    }

    /**
     * Get the first instruction in this block if it exists
     *
     * @see LLVM.LLVMGetFirstInstruction
     */
    public fun getFirstInstruction(): Instruction? {
        val instr = LLVM.LLVMGetFirstInstruction(ref)

        return wrap(instr) { Instruction(it) }
    }

    /**
     * Get the last instruction in this block if it exists
     *
     * @see LLVM.LLVMGetLastInstruction
     */
    public fun getLastInstruction(): Instruction? {
        val instr = LLVM.LLVMGetLastInstruction(ref)

        return wrap(instr) { Instruction(it) }
    }
    //endregion Core::BasicBlock

    //region LLVMIterator
    public override fun iter(): BasicBlockIterator {
        return BasicBlockIterator()
    }

    public inner class BasicBlockIterator : NextIterator<BasicBlock>,
        PrevIterator<BasicBlock> {
        /**
         * Get the next block in the iterator
         */
        public override fun next(): BasicBlock? {
            require(valid)

            val bb = LLVM.LLVMGetNextBasicBlock(ref)

            return wrap(bb) { BasicBlock(it) }
        }

        /**
         * Get the previous block in the iterator
         */
        public override fun prev(): BasicBlock? {
            require(valid)

            val bb = LLVM.LLVMGetPreviousBasicBlock(ref)

            return wrap(bb) { BasicBlock(it) }
        }
    }
    //endregion LLVMIterator
}

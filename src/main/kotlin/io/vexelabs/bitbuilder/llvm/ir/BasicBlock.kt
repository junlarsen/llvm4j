package io.vexelabs.bitbuilder.llvm.ir

import io.vexelabs.bitbuilder.llvm.internal.contracts.ContainsReference
import io.vexelabs.bitbuilder.llvm.internal.contracts.PointerIterator
import io.vexelabs.bitbuilder.llvm.internal.contracts.Validatable
import io.vexelabs.bitbuilder.llvm.ir.values.FunctionValue
import org.bytedeco.llvm.LLVM.LLVMBasicBlockRef
import org.bytedeco.llvm.global.LLVM

public class BasicBlock internal constructor() : Validatable,
    ContainsReference<LLVMBasicBlockRef> {
    override var valid: Boolean = true
    public override lateinit var ref: LLVMBasicBlockRef
        internal set

    public constructor(llvmRef: LLVMBasicBlockRef) : this() {
        ref = llvmRef
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
     * @see LLVM.LLVMBasicBlockAsValue
     */
    public fun toValue(): Value {
        require(valid) { "Cannot use deleted block" }

        val v = LLVM.LLVMBasicBlockAsValue(ref)

        return Value(v)
    }

    /**
     * Get the name of the basic block
     *
     * @see LLVM.LLVMGetBasicBlockName
     */
    public fun getName(): String {
        require(valid) { "Cannot use deleted block" }

        return LLVM.LLVMGetBasicBlockName(ref).string
    }

    /**
     * Get the function this basic block resides in
     *
     * @see LLVM.LLVMGetBasicBlockParent
     */
    public fun getFunction(): FunctionValue {
        require(valid) { "Cannot use deleted block" }

        val fn = LLVM.LLVMGetBasicBlockParent(ref)

        return FunctionValue(fn)
    }

    /**
     * Get the terminating instruction for this block, if it exists
     *
     * @see LLVM.LLVMGetBasicBlockTerminator
     */
    public fun getTerminator(): Instruction? {
        require(valid) { "Cannot use deleted block" }

        val instr = LLVM.LLVMGetBasicBlockTerminator(ref)

        return instr?.let { Instruction(it) }
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
        require(valid) { "Cannot use deleted block" }

        val bb = LLVM.LLVMInsertBasicBlockInContext(context.ref, ref, name)

        return BasicBlock(bb)
    }

    /**
     * Remove this block from the function it resides in and delete it
     *
     * @see LLVM.LLVMDeleteBasicBlock
     */
    public fun delete() {
        require(valid) { "Cannot use deleted block" }

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
        require(valid && other.valid) { "Cannot use deleted block" }

        LLVM.LLVMMoveBasicBlockBefore(ref, other.ref)
    }

    /**
     * Move this basic block after the [other] block
     *
     * @see LLVM.LLVMMoveBasicBlockAfter
     */
    public fun moveAfter(other: BasicBlock) {
        require(valid && other.valid) { "Cannot use deleted block" }

        LLVM.LLVMMoveBasicBlockAfter(ref, other.ref)
    }

    /**
     * Get the start of the instruction iterator
     *
     * @see PointerIterator
     */
    public fun getInstructionIterator(): Instruction.Iterator? {
        require(valid) { "Cannot use deleted block" }

        val instr = LLVM.LLVMGetFirstInstruction(ref)

        return instr?.let { Instruction.Iterator(it) }
    }
    //endregion Core::BasicBlock

    /**
     * Class to perform iteration over basic blocks
     *
     * @see [PointerIterator]
     */
    public class Iterator(ref: LLVMBasicBlockRef) :
        PointerIterator<BasicBlock, LLVMBasicBlockRef>(
            start = ref,
            yieldNext = { LLVM.LLVMGetNextBasicBlock(it) },
            apply = { BasicBlock(it) }
        )
}

package dev.supergrecko.kllvm.ir

import dev.supergrecko.kllvm.internal.util.wrap
import dev.supergrecko.kllvm.ir.instructions.Instruction
import dev.supergrecko.kllvm.ir.values.FunctionValue
import org.bytedeco.llvm.LLVM.LLVMBasicBlockRef
import org.bytedeco.llvm.global.LLVM

public class BasicBlock internal constructor() {
    public lateinit var ref: LLVMBasicBlockRef

    /**
     * Construct a new Type from an LLVM pointer reference
     */
    public constructor(block: LLVMBasicBlockRef) : this() {
        ref = block
    }

    //region Core::BasicBlock
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
        val v = LLVM.LLVMBasicBlockAsValue(ref)

        return Value(v)
    }

    /**
     * Get the name of the basic block
     *
     * @see LLVM.LLVMGetBasicBlockName
     */
    public fun getName(): String {
        return LLVM.LLVMGetBasicBlockName(ref).string
    }

    /**
     * Get the function this basic block resides in
     *
     * @see LLVM.LLVMGetBasicBlockParent
     */
    public fun getFunction(): FunctionValue {
        val fn = LLVM.LLVMGetBasicBlockParent(ref)

        return FunctionValue(fn)
    }

    /**
     * Get the terminating instruction for this block, if it exists
     *
     * @see LLVM.LLVMGetBasicBlockTerminator
     */
    public fun getTerminator(): Instruction? {
        val instr = LLVM.LLVMGetBasicBlockTerminator(ref)

        return wrap(instr) { Instruction(it) }
    }

    /**
     * Get the next block in the iterator
     *
     * Use with [BasicBlock.getNextBlock] and [BasicBlock.getPreviousBlock]
     * to move the iterator
     */
    public fun getNextBlock(): BasicBlock? {
        val bb = LLVM.LLVMGetNextBasicBlock(ref)

        return wrap(bb) { BasicBlock(it) }
    }

    /**
     * Get the previous block in the iterator
     *
     * Use with [BasicBlock.getNextBlock] and [BasicBlock.getPreviousBlock]
     * to move the iterator
     */
    public fun getPreviousBlock(): BasicBlock? {
        val bb = LLVM.LLVMGetPreviousBasicBlock(ref)

        return wrap(bb) { BasicBlock(it) }
    }
    //endregion Core::BasicBlock
}

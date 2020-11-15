package io.vexelabs.bitbuilder.llvm.ir.instructions

import io.vexelabs.bitbuilder.internal.toPointerPointer
import io.vexelabs.bitbuilder.llvm.ir.BasicBlock
import io.vexelabs.bitbuilder.llvm.ir.Instruction
import io.vexelabs.bitbuilder.llvm.ir.Value
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public class PhiInstruction internal constructor() : Instruction() {
    public constructor(llvmRef: LLVMValueRef) : this() {
        ref = llvmRef
    }

    /**
     * Add an incoming value to the phi node
     *
     * @see LLVM.LLVMAddIncoming
     */
    public fun addIncoming(values: List<Value>, blocks: List<BasicBlock>) {
        require(values.size == blocks.size)

        val valuePtr = values.map { it.ref }.toPointerPointer()
        val blockPtr = blocks.map { it.ref }.toPointerPointer()

        values.mapIndexed { i, v -> valuePtr.put(i.toLong(), v.ref) }
        blocks.mapIndexed { i, v -> blockPtr.put(i.toLong(), v.ref) }

        LLVM.LLVMAddIncoming(ref, valuePtr, blockPtr, values.size)

        valuePtr.deallocate()
        blockPtr.deallocate()
    }

    /**
     * Count how many incoming values this phi node has
     *
     * The block size is always equal to the value size
     *
     * @see LLVM.LLVMCountIncoming
     */
    public fun getIncomingCount(): Int {
        return LLVM.LLVMCountIncoming(ref)
    }

    /**
     * Get an incoming value at [index]
     *
     * @see LLVM.LLVMGetIncomingValue
     */
    public fun getIncomingValue(index: Int): Value? {
        val value = LLVM.LLVMGetIncomingValue(ref, index)

        return value?.let { Value(it) }
    }

    /**
     * Get an incoming block at [index]
     *
     * @see LLVM.LLVMGetIncomingBlock
     */
    public fun getIncomingBlock(index: Int): BasicBlock? {
        val block = LLVM.LLVMGetIncomingBlock(ref, index)

        return block?.let { BasicBlock(it) }
    }
}

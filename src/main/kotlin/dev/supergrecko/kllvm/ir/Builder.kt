package dev.supergrecko.kllvm.ir

import dev.supergrecko.kllvm.internal.contracts.Disposable
import dev.supergrecko.kllvm.internal.contracts.Validatable
import dev.supergrecko.kllvm.ir.instructions.Instruction
import dev.supergrecko.kllvm.ir.instructions.IntPredicate
import dev.supergrecko.kllvm.ir.instructions.Opcode
import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.llvm.LLVM.LLVMBuilderRef
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public class Builder public constructor(
    context: Context = Context.getGlobalContext()
) : AutoCloseable, Validatable, Disposable {
    public var ref: LLVMBuilderRef
    public override var valid: Boolean = true

    init {
        ref = LLVM.LLVMCreateBuilderInContext(context.ref)
    }

    /**
     * Construct a new Type from an LLVM pointer reference
     */
    public constructor(builder: LLVMBuilderRef) : this() {
        ref = builder
    }

    //region InstructionBuilders
    public fun buildRetVoid(): Value {
        val void = LLVM.LLVMBuildRetVoid(ref)

        return Value(void)
    }

    /**
     * LLVMPositionBuilder
     */
    public fun positionBefore(instruction: Instruction) {
        // TODO: Test
        LLVM.LLVMPositionBuilderBefore(ref, instruction.ref)
    }

    /**
     * LLVMPositionBuilderAtEnd
     */
    public fun positionAtEnd(basicBlock: BasicBlock) {
        LLVM.LLVMPositionBuilderAtEnd(ref, basicBlock.ref)
    }

    /**
     * LLVMGetInsertBlock
     */
    public fun getInsertBlock(): BasicBlock? {
        val ref = LLVM.LLVMGetInsertBlock(ref) ?: return null
        return BasicBlock(ref)
    }

    /**
     * LLVMClearInsertionPosition
     */
    public fun clearInsertPosition() {
        LLVM.LLVMClearInsertionPosition(ref)
    }

    /**
     * LLVMInsertIntoBuilderWithName
     */
    public fun insert(instruction: Instruction, name: String?) {
        // TODO: Test
        LLVM.LLVMInsertIntoBuilderWithName(
            ref,
            instruction.ref,
            name
        )
    }

    /**
     * Create a function call passing in [args] and binding the result into
     * variable [resultName]. Result discarded if no resultName supplied.
     * @see LLVM.LLVMBuildCall
     * TODO: Replace with CallInstruction when types is created
     */
    public fun buildCall(
        function: Value,
        args: List<Value>,
        resultName: String? = null
    ): Instruction {
        val argsPtr: PointerPointer<LLVMValueRef> =
            PointerPointer(*(args.map { it.ref }.toTypedArray()))
        val ref = LLVM.LLVMBuildCall(
            ref,
            function.ref,
            argsPtr,
            args.size,
            // This call segfaults when null string is supplied
            // the correct behaviour of calling without binding
            // the result to a name is invoked by passing a blank
            // string
            resultName ?: ""
        )
        return Instruction(
            ref
        )
    }

    public fun buildRet(value: Value): Instruction {
        val ret = LLVM.LLVMBuildRet(ref, value.ref)

        return Instruction(ret)
    }

    public fun buildAlloca(type: Type, name: String): Instruction {
        return Instruction(
            LLVM.LLVMBuildAlloca(ref, type.ref, name)
        )
    }

    public fun buildLoad(ptr: Value, name: String): Instruction {
        return Instruction(
            LLVM.LLVMBuildLoad(ref, ptr.ref, name)
        )
    }

    public fun buildStore(value: Value, toPointer: Value): Instruction {
        return Instruction(
            LLVM.LLVMBuildStore(ref, value.ref, toPointer.ref)
        )
    }

    public fun buildExtractValue(
        aggVal: Value,
        index: Int,
        name: String
    ): Instruction {
        return Instruction(
            LLVM.LLVMBuildExtractValue(ref, aggVal.ref, index, name)
        )
    }

    public fun buildStructGEP(
        pointer: Value,
        index: Int,
        name: String
    ): Instruction {
        return Instruction(
            LLVM.LLVMBuildStructGEP(ref, pointer.ref, index, name)
        )
    }

    /**
     * @see LLVM.LLVMBuildCondBr
     */
    public fun buildCondBr(
        condition: Value,
        ifTrue: BasicBlock,
        ifFalse: BasicBlock
    ): Instruction {
        return Instruction(LLVM.LLVMBuildCondBr(ref, condition.ref, ifTrue.ref, ifFalse.ref))
    }

    /**
     * @see LLVM.LLVMBuildNot
     */
    public fun buildNot(
        value: Value,
        name: String
    ) : Instruction {
        return Instruction(LLVM.LLVMBuildNot(ref, value.ref, name))
    }

    /**
     * @see LLVM.LLVMBuildBinOp
     */
    public fun buildBinOp(operator: Opcode, lhs: Value, rhs: Value, name: String): Instruction {
        return Instruction(LLVM.LLVMBuildBinOp(ref, operator.value, lhs.ref, rhs.ref, name))
    }

    /**
     * @see LLVM.LLVMBuildICmp
     */
    public fun buildICmp(predicate: IntPredicate, lhs: Value, rhs: Value, name: String): Instruction {
        return Instruction(LLVM.LLVMBuildICmp(ref, predicate.value, lhs.ref, rhs.ref, name))
    }

    //endregion InstructionBuilders

    override fun dispose() {
        require(valid) { "This builder has already been disposed." }

        valid = false

        LLVM.LLVMDisposeBuilder(ref)
    }

    override fun close() = dispose()
}

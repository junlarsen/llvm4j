package dev.supergrecko.kllvm.llvm.typedefs

import dev.supergrecko.kllvm.internal.contracts.Disposable
import dev.supergrecko.kllvm.internal.contracts.Validatable
import dev.supergrecko.kllvm.types.Type
import dev.supergrecko.kllvm.values.Value
import dev.supergrecko.kllvm.values.instructions.Instruction
import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.llvm.LLVM.LLVMBuilderRef
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public class Builder public constructor(context: Context = Context.getGlobalContext()) : AutoCloseable, Validatable, Disposable {
    internal var ref: LLVMBuilderRef
    public override var valid: Boolean = true

    init {
        ref = LLVM.LLVMCreateBuilderInContext(context.ref)
    }

    public constructor(builder: LLVMBuilderRef) : this() {
        ref = builder
    }

    public fun getUnderlyingRef(): LLVMBuilderRef {
        return ref
    }

    //region InstructionBuilders
    public fun buildRetVoid(): Value {
        return Value(
            LLVM.LLVMBuildRetVoid(
                ref
            )
        )
    }

    /**
     * LLVMPositionBuilder
     */
    public fun positionBefore(instruction: Instruction): Unit {
        // TODO: Test
        LLVM.LLVMPositionBuilderBefore(getUnderlyingRef(), instruction.ref)
    }

    /**
     * LLVMPositionBuilderAtEnd
     */
    public fun positionAtEnd(basicBlock: BasicBlock): Unit {
        LLVM.LLVMPositionBuilderAtEnd(getUnderlyingRef(), basicBlock.ref)
    }

    /**
     * LLVMGetInsertBlock
     */
    public fun getInsertBlock(): BasicBlock? {
        val ref = LLVM.LLVMGetInsertBlock(getUnderlyingRef()) ?: return null
        return BasicBlock(ref)
    }

    /**
     * LLVMClearInsertionPosition
     */
    public fun clearInsertPosition(): Unit =
        LLVM.LLVMClearInsertionPosition(getUnderlyingRef())

    /**
     * LLVMInsertIntoBuilderWithName
     */
    public fun insert(instruction: Instruction, name: String?): Unit {
        // TODO: Test
        LLVM.LLVMInsertIntoBuilderWithName(
            getUnderlyingRef(),
            instruction.getUnderlyingReference(),
            name
        )
    }

    /**
     * Create a function call passing in [args] and binding the result into
     * variable [resultName]. Result discarded if no resultName supplied.
     * @see LLVM.LLVMBuildCall
     * TODO: Replace with CallInstruction when type is created
     */
    public fun buildCall(
        function: Value,
        args: List<Value>,
        resultName: String? = null
    ): Instruction {
        val argsPtr: PointerPointer<LLVMValueRef> =
            PointerPointer(*(args.map { it.getUnderlyingReference() }
                .toTypedArray()))
        val ref = LLVM.LLVMBuildCall(
            getUnderlyingRef(),
            function.getUnderlyingReference(),
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
        return Instruction(
            LLVM.LLVMBuildRet(
                getUnderlyingRef(),
                value.getUnderlyingReference()
            )
        )
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

    public fun buildExtractValue(aggVal: Value, index: Int, name: String): Instruction {
        return Instruction(
            LLVM.LLVMBuildExtractValue(ref, aggVal.ref, index, name)
        )
    }

    public fun buildStructGEP(pointer: Value, index: Int, name: String): Instruction {
        return Instruction(
            LLVM.LLVMBuildStructGEP(ref, pointer.ref, index, name)
        )
    }

    //endregion InstructionBuilders

    override fun dispose() {
        require(valid) { "This builder has already been disposed." }

        valid = false

        LLVM.LLVMDisposeBuilder(ref)
    }

    override fun close() = dispose()
}


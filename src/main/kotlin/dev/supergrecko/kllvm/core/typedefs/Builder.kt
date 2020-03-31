package dev.supergrecko.kllvm.core.typedefs

import dev.supergrecko.kllvm.contracts.Disposable
import dev.supergrecko.kllvm.contracts.Validatable
import dev.supergrecko.kllvm.core.values.InstructionValue
import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.llvm.LLVM.LLVMBuilderRef
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public class Builder internal constructor(internal val llvmBuilder: LLVMBuilderRef) : AutoCloseable, Validatable, Disposable {
    public override var valid: Boolean = true

    public fun getUnderlyingRef(): LLVMBuilderRef {
        return llvmBuilder
    }

    //region InstructionBuilders
    public fun buildRetVoid(): Value {
        return Value(LLVM.LLVMBuildRetVoid(llvmBuilder))
    }

    /**
     * LLVMPositionBuilder
     */
    public fun positionBefore(instruction: InstructionValue): Unit {
        // TODO: Test
        LLVM.LLVMPositionBuilderBefore(getUnderlyingRef(), instruction.llvmValue)
    }

    /**
     * LLVMPositionBuilderAtEnd
     */
    public fun positionAtEnd(basicBlock: BasicBlock): Unit {
        LLVM.LLVMPositionBuilderAtEnd(getUnderlyingRef(), basicBlock.llvmBlock)
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
    public fun clearInsertPosition(): Unit = LLVM.LLVMClearInsertionPosition(getUnderlyingRef())

    /**
     * LLVMInsertIntoBuilderWithName
     */
    public fun insert(instruction: InstructionValue, name: String?): Unit {
        // TODO: Test
        LLVM.LLVMInsertIntoBuilderWithName(getUnderlyingRef(), instruction.getUnderlyingReference(), name)
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
    ) : InstructionValue {
        val argsPtr: PointerPointer<LLVMValueRef> =
            PointerPointer(*(args.map { it.getUnderlyingReference() }.toTypedArray()))
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
        return InstructionValue(ref)
    }

    public fun buildRet(value: Value): InstructionValue {
        return InstructionValue(
            LLVM.LLVMBuildRet(
                getUnderlyingRef(),
                value.getUnderlyingReference()))
    }
    //endregion InstructionBuilders

    override fun dispose() {
        require(valid) { "This builder has already been disposed." }

        valid = false

        LLVM.LLVMDisposeBuilder(llvmBuilder)
    }

    override fun close() = dispose()

    companion object {
        //region InstructionBuilders
        @JvmStatic
        fun create(ctx: Context = Context.getGlobalContext()): Builder {
            return Builder(LLVM.LLVMCreateBuilderInContext(ctx.llvmCtx))
        }

        //endregion InstructionBuilders
    }
}


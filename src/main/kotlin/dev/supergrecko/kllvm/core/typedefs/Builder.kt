package dev.supergrecko.kllvm.core.typedefs

import dev.supergrecko.kllvm.contracts.Disposable
import dev.supergrecko.kllvm.contracts.Validatable
import dev.supergrecko.kllvm.core.values.InstructionValue
import org.bytedeco.llvm.LLVM.LLVMBuilderRef
import org.bytedeco.llvm.global.LLVM

public class Builder internal constructor(internal val llvmBuilder: LLVMBuilderRef) : AutoCloseable, Validatable, Disposable {
    public override var valid: Boolean = true

    //region Core::Builders
    public fun getUnderlyingRef(): LLVMBuilderRef {
        return llvmBuilder
    }

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

    //endregion Core::Builders

    override fun dispose() {
        require(valid) { "This builder has already been disposed." }

        valid = false

        LLVM.LLVMDisposeBuilder(llvmBuilder)
    }

    override fun close() = dispose()

    companion object {
        //region Core::Builders
        @JvmStatic
        fun create(ctx: Context? = null): Builder {
            return Builder(LLVM.LLVMCreateBuilderInContext(ctx?.llvmCtx))
        }

        //endregion Core::Builders
    }
}

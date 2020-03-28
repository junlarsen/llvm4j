package dev.supergrecko.kllvm.core.typedefs

import dev.supergrecko.kllvm.contracts.Disposable
import dev.supergrecko.kllvm.contracts.Validatable
import org.bytedeco.llvm.LLVM.LLVMBuilderRef
import org.bytedeco.llvm.global.LLVM

public class Builder internal constructor(internal val llvmBuilder: LLVMBuilderRef) : AutoCloseable, Validatable, Disposable {
    public override var valid: Boolean = true

    override fun dispose() {
        require(valid) { "This builder has already been disposed." }

        valid = false

        LLVM.LLVMDisposeBuilder(llvmBuilder)
    }

    override fun close() = dispose()

    fun positionAtEnd(basicBlock: BasicBlock) {
        LLVM.LLVMPositionBuilderAtEnd(llvmBuilder, basicBlock.llvmBlock)
    }
}

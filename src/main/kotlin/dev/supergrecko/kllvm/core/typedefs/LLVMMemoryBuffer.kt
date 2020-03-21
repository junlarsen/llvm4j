package dev.supergrecko.kllvm.core.typedefs

import dev.supergrecko.kllvm.contracts.Disposable
import dev.supergrecko.kllvm.contracts.Validatable
import org.bytedeco.llvm.LLVM.LLVMMemoryBufferRef
import org.bytedeco.llvm.global.LLVM

public class LLVMMemoryBuffer internal constructor(internal val llvmBuffer: LLVMMemoryBufferRef) : AutoCloseable, Validatable, Disposable {
    public override var valid: Boolean = true

    override fun dispose() {
        require(valid) { "This buffer has already been disposed." }

        valid = false

        LLVM.LLVMDisposeMemoryBuffer(llvmBuffer)
    }

    override fun close() = dispose()
}

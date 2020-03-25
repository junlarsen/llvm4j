package dev.supergrecko.kllvm.core.typedefs

import dev.supergrecko.kllvm.contracts.Disposable
import dev.supergrecko.kllvm.contracts.Validatable
import org.bytedeco.llvm.LLVM.LLVMBinaryRef
import org.bytedeco.llvm.LLVM.LLVMPassManagerRef
import org.bytedeco.llvm.global.LLVM

public class Binary internal constructor(internal val llvmBinary: LLVMBinaryRef) : AutoCloseable, Validatable, Disposable {
    public override var valid: Boolean = true

    override fun dispose() {
        require(valid) { "This binary has already been disposed." }

        valid = false

        LLVM.LLVMDisposeBinary(llvmBinary)
    }

    override fun close() = dispose()
}

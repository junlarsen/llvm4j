package dev.supergrecko.kllvm.core.typedefs

import dev.supergrecko.kllvm.contracts.Disposable
import dev.supergrecko.kllvm.contracts.Validatable
import org.bytedeco.llvm.LLVM.LLVMBinaryRef
import org.bytedeco.llvm.global.LLVM

public class Binary internal constructor(binary: LLVMBinaryRef) : AutoCloseable,
    Validatable, Disposable {
    internal var ref: LLVMBinaryRef = binary

    public override var valid: Boolean = true

    override fun dispose() {
        require(valid) { "This binary has already been disposed." }

        valid = false

        LLVM.LLVMDisposeBinary(ref)
    }

    override fun close() = dispose()
}

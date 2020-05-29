package dev.supergrecko.kllvm.`object`

import dev.supergrecko.kllvm.unit.internal.contracts.Disposable
import dev.supergrecko.kllvm.unit.internal.contracts.Validatable
import org.bytedeco.llvm.LLVM.LLVMBinaryRef
import org.bytedeco.llvm.global.LLVM

public class Binary internal constructor() : AutoCloseable,
    Validatable, Disposable {
    internal lateinit var ref: LLVMBinaryRef
    public override var valid: Boolean = true

    public constructor(binary: LLVMBinaryRef) : this() {
        ref = binary
    }

    override fun dispose() {
        require(valid) { "This binary has already been disposed." }

        valid = false

        LLVM.LLVMDisposeBinary(ref)
    }

    override fun close() = dispose()
}

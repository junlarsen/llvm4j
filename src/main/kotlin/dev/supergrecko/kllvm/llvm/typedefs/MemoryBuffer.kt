package dev.supergrecko.kllvm.llvm.typedefs

import dev.supergrecko.kllvm.internal.contracts.Disposable
import dev.supergrecko.kllvm.internal.contracts.Validatable
import org.bytedeco.llvm.LLVM.LLVMMemoryBufferRef
import org.bytedeco.llvm.LLVM.LLVMModuleRef
import org.bytedeco.llvm.global.LLVM

public class MemoryBuffer internal constructor() :
    AutoCloseable, Validatable, Disposable {
    internal lateinit var ref: LLVMMemoryBufferRef
    public override var valid: Boolean = true

    public constructor(buffer: LLVMMemoryBufferRef) : this() {
        ref = buffer
    }

    //region BitReader
    public fun parse(context: Context = Context.getGlobalContext()): Module {
        val ptr = LLVMModuleRef()

        LLVM.LLVMParseBitcodeInContext2(context.ref, ref, ptr)

        return Module(ptr)
    }

    public fun getModule(context: Context = Context.getGlobalContext()): Module {
        val ptr = LLVMModuleRef()

        LLVM.LLVMGetBitcodeModuleInContext2(context.ref, ref, ptr)

        return Module(ptr)
    }
    //endregion BitReader

    override fun dispose() {
        require(valid) { "This buffer has already been disposed." }

        valid = false

        LLVM.LLVMDisposeMemoryBuffer(ref)
    }

    override fun close() = dispose()
}

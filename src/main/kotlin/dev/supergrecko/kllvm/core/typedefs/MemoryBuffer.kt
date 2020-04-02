package dev.supergrecko.kllvm.core.typedefs

import dev.supergrecko.kllvm.contracts.Disposable
import dev.supergrecko.kllvm.contracts.Validatable
import org.bytedeco.javacpp.BytePointer
import org.bytedeco.javacpp.Pointer
import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.llvm.LLVM.LLVMMemoryBufferRef
import org.bytedeco.llvm.LLVM.LLVMModuleRef
import org.bytedeco.llvm.global.LLVM
import java.io.File

public class MemoryBuffer internal constructor() :
    AutoCloseable, Validatable, Disposable {
    internal lateinit var ref: LLVMMemoryBufferRef
    public override var valid: Boolean = true

    public constructor(buffer: LLVMMemoryBufferRef) : this() {
        ref = buffer
    }

    //region MemoryBuffers
    public constructor(file: File) : this() {
        require(file.exists()) { "File does not exist" }

        val buf = LLVMMemoryBufferRef()
        val ptr = PointerPointer<LLVMMemoryBufferRef>(buf)

        // TODO: Solve segfault
        LLVM.LLVMCreateMemoryBufferWithContentsOfFile(file.absolutePath, ptr, BytePointer())

        val x = 100
        ref = ptr.get(LLVMMemoryBufferRef::class.java, 0)
    }

    public fun getStart(): Char {
        val s = LLVM.LLVMGetBufferStart(ref)

        return s.get(0).toChar()
    }

    public fun getSize(): Long {
        return LLVM.LLVMGetBufferSize(ref)
    }
    //endregion MemoryBuffers

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

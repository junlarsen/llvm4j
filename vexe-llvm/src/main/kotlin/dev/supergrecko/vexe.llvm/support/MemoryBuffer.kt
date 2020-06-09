package dev.supergrecko.vexe.llvm.support

import dev.supergrecko.vexe.llvm.internal.contracts.Disposable
import dev.supergrecko.vexe.llvm.internal.contracts.Validatable
import dev.supergrecko.vexe.llvm.ir.Context
import dev.supergrecko.vexe.llvm.ir.Module
import java.io.File
import org.bytedeco.javacpp.BytePointer
import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.llvm.LLVM.LLVMMemoryBufferRef
import org.bytedeco.llvm.LLVM.LLVMModuleRef
import org.bytedeco.llvm.global.LLVM

public class MemoryBuffer internal constructor() :
    AutoCloseable, Validatable, Disposable {
    internal lateinit var ref: LLVMMemoryBufferRef
    public override var valid: Boolean = true

    /**
     * Construct a new Type from an LLVM pointer reference
     */
    public constructor(buffer: LLVMMemoryBufferRef) : this() {
        ref = buffer
    }

    //region BitReader
    public fun parse(context: Context = Context.getGlobalContext()): Module {
        val ptr = LLVMModuleRef()

        LLVM.LLVMParseBitcodeInContext2(context.ref, ref, ptr)

        return Module(ptr)
    }

    public fun getModule(
        context: Context = Context.getGlobalContext()
    ): Module {
        val ptr = LLVMModuleRef()

        LLVM.LLVMGetBitcodeModuleInContext2(context.ref, ref, ptr)

        return Module(ptr)
    }
    //endregion BitReader

    //region MemoryBuffers
    /**
     * Loads file contents into a memory buffer
     *
     * @see LLVM.LLVMCreateMemoryBufferWithContentsOfFile
     */
    public constructor(file: File) : this() {
        require(file.exists()) { "File does not exist" }

        val ptr = PointerPointer<LLVMMemoryBufferRef>(1L)
        val outMessage = BytePointer()

        val res = LLVM.LLVMCreateMemoryBufferWithContentsOfFile(
            file.absolutePath,
            ptr,
            outMessage
        )

        if (res != 0) {
            throw RuntimeException("Error occurred while creating buffer from" +
                    " file. Provided LLVM Error: $outMessage")
        }

        ref = ptr.get(LLVMMemoryBufferRef::class.java, 0)
    }

    /**
     * Get the first char in the buffer
     *
     * @see LLVM.LLVMGetBufferStart
     *
     * TODO: How to advance and get the next characters?
     */
    public fun getStart(): Char {
        val s = LLVM.LLVMGetBufferStart(ref)

        return s.get(0).toChar()
    }

    /**
     * Get the size of the buffer
     *
     * @see LLVM.LLVMGetBufferSize
     *
     * TODO: Find a reliable, x-platform way to test this as different
     *   platforms return different sizes for values
     */
    public fun getSize(): Long {
        return LLVM.LLVMGetBufferSize(ref)
    }
    //endregion MemoryBuffers

    override fun dispose() {
        require(valid) { "This buffer has already been disposed." }

        valid = false

        LLVM.LLVMDisposeMemoryBuffer(ref)
    }

    override fun close() = dispose()
}

package dev.supergrecko.vexe.llvm.support

import dev.supergrecko.vexe.llvm.internal.contracts.Disposable
import dev.supergrecko.vexe.llvm.ir.Context
import dev.supergrecko.vexe.llvm.ir.Module
import org.bytedeco.javacpp.BytePointer
import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.llvm.LLVM.LLVMMemoryBufferRef
import org.bytedeco.llvm.LLVM.LLVMModuleRef
import org.bytedeco.llvm.global.LLVM
import java.io.File

public class MemoryBuffer internal constructor() :
    AutoCloseable, Disposable {
    public lateinit var ref: LLVMMemoryBufferRef
        internal set
    public override var valid: Boolean = true

    public constructor(llvmRef: LLVMMemoryBufferRef) : this() {
        ref = llvmRef
    }

    //region BitReader
    /**
     * Parse bitecode memory buffer into a LLVM module
     *
     * @see LLVM.LLVMParseBitcodeInContext2
     */
    public fun parse(context: Context = Context.getGlobalContext()): Module {
        val ptr = LLVMModuleRef()

        LLVM.LLVMParseBitcodeInContext2(context.ref, ref, ptr)

        return Module(ptr)
    }

    /**
     * Parse bitcode in memory buffer into a LLVM module
     *
     * TODO: What differentiates this from [parse]
     *
     * @see LLVM.LLVMGetBitcodeModuleInContext2
     */
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
            throw RuntimeException(
                "Error occurred while creating buffer from" +
                        " file. Provided LLVM Error: $outMessage"
            )
        }

        ref = ptr.get(LLVMMemoryBufferRef::class.java, 0)
    }

    /**
     * Get the first char in the buffer
     *
     * TODO: How to advance and get the next characters?
     *
     * @see LLVM.LLVMGetBufferStart
     */
    public fun getStart(): Char {
        val s = LLVM.LLVMGetBufferStart(ref)

        return s.get(0).toChar()
    }

    /**
     * Get the size of the buffer
     *
     * TODO: Find a reliable, x-platform way to test this as different
     *   platforms return different sizes for values
     *
     * @see LLVM.LLVMGetBufferSize
     */
    public fun getSize(): Long {
        return LLVM.LLVMGetBufferSize(ref)
    }
    //endregion MemoryBuffers

    override fun dispose() {
        require(valid) { "Cannot dispose object twice" }

        valid = false

        LLVM.LLVMDisposeMemoryBuffer(ref)
    }

    override fun close() = dispose()
}

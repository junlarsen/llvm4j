package dev.supergrecko.vexe.llvm.support

import dev.supergrecko.vexe.llvm.internal.contracts.Disposable
import dev.supergrecko.vexe.llvm.ir.Context
import dev.supergrecko.vexe.llvm.ir.Module
import java.io.File
import org.bytedeco.javacpp.BytePointer
import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.llvm.LLVM.LLVMMemoryBufferRef
import org.bytedeco.llvm.LLVM.LLVMModuleRef
import org.bytedeco.llvm.global.LLVM

public class MemoryBuffer internal constructor() : Disposable {
    public override var valid: Boolean = true
    public lateinit var ref: LLVMMemoryBufferRef
        internal set

    public constructor(llvmRef: LLVMMemoryBufferRef) : this() {
        ref = llvmRef
    }

    //region BitReader
    /**
     * Parse this memory buffer as if it was LLVM bit code into an LLVM Module
     *
     * The module will be parsed using the provided [intoContext]. If no
     * context is passed, it will use the global llvm context.
     *
     * LLVM-C provides us two ways to parse bit code into modules:
     * [LLVM.LLVMParseBitcode] and [LLVM.LLVMGetBitcodeModule].
     *
     * [LLVM.LLVMGetBitcodeModule] lazily parses (?) the buffer. If the
     * [parseLazy] parameter is present, then this will be used. Otherwise
     * the method will default to [LLVM.LLVMParseBitcode]
     *
     * @throws RuntimeException if there was an error parsing the module and
     * the parsing method was not lazy.
     */
    public fun getBitCodeModule(
        intoContext: Context = Context.getGlobalContext(),
        parseLazy: Boolean = true
    ) : Module {
        var error: BytePointer? = null
        val outModule = LLVMModuleRef()
        val result = if (parseLazy) {
            LLVM.LLVMGetBitcodeModuleInContext2(
                intoContext.ref,
                ref,
                outModule
            )
        } else {
            error = BytePointer(0L)
            LLVM.LLVMParseBitcodeInContext(
                intoContext.ref,
                ref,
                outModule,
                error
            )
        }

        if (result != 0) {
            outModule.deallocate()

            throw if (error == null) {
                RuntimeException("Failed to parse bit code")
            } else {
                RuntimeException("Failed to parse bit code: ${error.string}")
            }
        }

        return Module(outModule)
    }

    /**
     * Parse this memory buffer as if it was LLVM IR into an LLVM Module
     *
     * The module will be parsed using the provided [intoContext]. If no
     * context is passed, it will use the global llvm context.
     *
     * @throws RuntimeException if there was an error parsing the module
     */
    public fun getIRModule(
        intoContext: Context = Context.getGlobalContext()
    ): Module {
        val error = BytePointer(0L)
        val outModule = LLVMModuleRef()
        val result = LLVM.LLVMParseIRInContext(
            intoContext.ref,
            ref,
            outModule,
            error
        )

        if (result != 0) {
            outModule.deallocate()

            throw RuntimeException("Failed to parse ir: ${error.string}")
        }

        return Module(outModule)
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
     * Get a pointer to the first char in the buffer
     *
     * @see LLVM.LLVMGetBufferStart
     */
    public fun getStart(): BytePointer {
        return LLVM.LLVMGetBufferStart(ref)
    }

    /**
     * Get the size of the buffer
     *
     * @see LLVM.LLVMGetBufferSize
     */
    public fun getSize(): Long {
        return LLVM.LLVMGetBufferSize(ref)
    }
    //endregion MemoryBuffers

    public override fun dispose() {
        require(valid) { "Cannot dispose object twice" }

        valid = false

        LLVM.LLVMDisposeMemoryBuffer(ref)
    }
}

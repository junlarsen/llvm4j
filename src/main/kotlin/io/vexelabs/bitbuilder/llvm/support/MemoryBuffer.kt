package io.vexelabs.bitbuilder.llvm.support

import io.vexelabs.bitbuilder.llvm.internal.contracts.Disposable
import io.vexelabs.bitbuilder.llvm.ir.Context
import io.vexelabs.bitbuilder.llvm.ir.Module
import io.vexelabs.bitbuilder.internal.resourceScope
import io.vexelabs.bitbuilder.internal.toResource
import org.bytedeco.javacpp.BytePointer
import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.llvm.LLVM.LLVMMemoryBufferRef
import org.bytedeco.llvm.LLVM.LLVMModuleRef
import org.bytedeco.llvm.global.LLVM
import java.io.File

/**
 * Interface to llvm::MemoryBuffer
 *
 * A memory buffer is a simple read-only access to a piece of memory. LLVM
 * passes around various textual formats like IR or Bitcode in Memory buffers.
 *
 * @see LLVMMemoryBufferRef
 */
public class MemoryBuffer internal constructor() : Disposable {
    public override var valid: Boolean = true
    public lateinit var ref: LLVMMemoryBufferRef
        internal set

    public constructor(llvmRef: LLVMMemoryBufferRef) : this() {
        ref = llvmRef
    }

    /**
     * Parse this memory buffer as if it was LLVM bit code into an LLVM Module
     *
     * The module will be parsed using the provided [ctx]. If no
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
        ctx: Context = Context.getGlobalContext(),
        parseLazy: Boolean = true
    ): Module {
        val buf = BytePointer(256).toResource()

        return resourceScope(buf) {
            val outModule = LLVMModuleRef()
            val result = if (parseLazy) {
                LLVM.LLVMGetBitcodeModuleInContext2(ctx.ref, ref, outModule)
            } else {
                LLVM.LLVMParseBitcodeInContext(ctx.ref, ref, outModule, it)
            }

            if (result != 0) {
                outModule.deallocate()
                throw RuntimeException(
                    "Failed to parse bit code ${":" + it.string}"
                )
            }

            return@resourceScope Module(outModule)
        }
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
        val buf = BytePointer(256).toResource()

        return resourceScope(buf) {
            val outModule = LLVMModuleRef()
            val result = LLVM.LLVMParseIRInContext(
                intoContext.ref,
                ref,
                outModule,
                it
            )

            if (result != 0) {
                outModule.deallocate()
                throw RuntimeException("Failed to parse ir: ${it.string}")
            }

            return@resourceScope Module(outModule)
        }
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

    public companion object {
        /**
         * Loads file contents into a memory buffer
         *
         * @see LLVM.LLVMCreateMemoryBufferWithContentsOfFile
         * @throws RuntimeException
         */
        @JvmStatic
        public fun fromFile(file: File): MemoryBuffer {
            require(file.exists()) { "File does not exist" }

            val buf = BytePointer(256).toResource()

            return resourceScope(buf) {
                val pointer = PointerPointer<LLVMMemoryBufferRef>(1)
                val result = LLVM.LLVMCreateMemoryBufferWithContentsOfFile(
                    file.absolutePath,
                    pointer,
                    it
                )

                if (result != 0) {
                    pointer.deallocate()
                    throw RuntimeException(
                        "Error occurred while creating buffer from" +
                        " file. Provided LLVM Error: ${it.string}"
                    )
                }

                val memBuf = pointer.get(LLVMMemoryBufferRef::class.java, 0)

                return@resourceScope MemoryBuffer(memBuf)
            }
        }
    }

    public override fun dispose() {
        require(valid) { "Cannot dispose object twice" }

        valid = false

        LLVM.LLVMDisposeMemoryBuffer(ref)
    }
}

package org.llvm4j.llvm4j

import org.bytedeco.javacpp.BytePointer
import org.bytedeco.llvm.LLVM.LLVMMemoryBufferRef
import org.bytedeco.llvm.global.LLVM
import org.llvm4j.llvm4j.util.CorrespondsTo
import org.llvm4j.llvm4j.util.Owner
import org.llvm4j.llvm4j.util.Result
import org.llvm4j.llvm4j.util.tryWith
import java.io.File

/**
 * A readonly, null-terminated string owned by LLVM, known as a Message in the C API
 *
 * Do not use this constructor for [BytePointer]s which were not created as a LLVM owned string. If you need to
 * create an instance of [LLVMString], use [LLVMString.create]
 *
 * @author Mats Larsen
 */
public class LLVMString public constructor(ptr: BytePointer) : Owner<BytePointer> {
    public override val ref: BytePointer = ptr

    /**
     * Copies the content of the string.
     */
    public fun getString(): String = ref.string

    public override fun deallocate() {
        LLVM.LLVMDisposeMessage(ref)
    }

    public companion object {
        @JvmStatic
        public fun create(message: String): LLVMString {
            val ptr = BytePointer(message)
            val string = LLVM.LLVMCreateMessage(ptr)

            ptr.deallocate()

            return LLVMString(string)
        }
    }
}

/**
 * A memory buffer is a simple, read-only access to a block of memory. The memory buffer is always null terminated
 *
 * Creation of a memory buffer is usually done with a file.
 *
 * TODO: Research - Can we use stdin creation? Does this even make sense?
 *
 * @author Mats Larsen
 */
@CorrespondsTo("llvm::MemoryBuffer")
public class MemoryBuffer public constructor(ptr: LLVMMemoryBufferRef) : Owner<LLVMMemoryBufferRef> {
    public override val ref: LLVMMemoryBufferRef = ptr

    public fun getSize(): Long {
        return LLVM.LLVMGetBufferSize(ref)
    }

    /**
     * Get a pointer to the start of the buffer. This returns a byte pointer which can be indexed all the way through
     * [getSize] - 1.
     */
    public fun getStartPointer(): BytePointer {
        return LLVM.LLVMGetBufferStart(ref)
    }

    public fun getString(): String {
        return getStartPointer().string
    }

    public override fun deallocate() {
        LLVM.LLVMDisposeMemoryBuffer(ref)
    }

    public companion object {
        @JvmStatic
        public fun create(file: File): Result<MemoryBuffer> = tryWith {
            assert(file.exists()) { "File '$file' does not exist" }

            val fp = BytePointer(file.absolutePath)
            val err = BytePointer(256)
            val buf = LLVMMemoryBufferRef()
            val code = LLVM.LLVMCreateMemoryBufferWithContentsOfFile(fp, buf, err)

            fp.deallocate()

            assert(code == 0) {
                val c = err.string
                err.deallocate()
                buf.deallocate()
                c
            }

            MemoryBuffer(buf)
        }
    }
}

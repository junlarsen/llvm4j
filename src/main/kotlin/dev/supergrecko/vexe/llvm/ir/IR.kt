package dev.supergrecko.vexe.llvm.ir

import dev.supergrecko.vexe.llvm.support.Message
import org.bytedeco.javacpp.BytePointer
import java.io.File

/**
 * Specific wrapper representing a piece of LLVM IR
 *
 * This type is preferred over a [Message] for functions which specifically
 * return a piece of IR because of its utility methods
 */
public class IR(override val pointer: BytePointer) : Message(pointer) {
    /**
     * Writes this IR to a file at the given [path]
     */
    public fun writeToFile(path: File) {
        val content = toString()

        if (!path.exists()) {
            path.createNewFile()
        }

        path.writeText(content)
    }

    /**
     * Compare this IR with another item
     *
     * IR Comparison is done by comparing the IR strings
     */
    public override fun equals(other: Any?): Boolean {
        return toString() == other.toString()
    }

    public override fun hashCode(): Int = pointer.hashCode()

    public override fun toString(): String = getString()
}

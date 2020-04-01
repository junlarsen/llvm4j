package dev.supergrecko.kllvm.core.values

import dev.supergrecko.kllvm.core.typedefs.Value
import dev.supergrecko.kllvm.core.types.IntType
import dev.supergrecko.kllvm.utils.toInt
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public class IntValue internal constructor() : Value() {
    /**
     * Internal constructor for actual reference
     */
    internal constructor(llvmValue: LLVMValueRef) : this() {
        ref = llvmValue
    }

    public constructor(value: Value) : this(value.ref)

    /**
     * @see [LLVM.LLVMConstInt]
     */
    public constructor(type: IntType, value: Long, signExtend: Boolean) : this() {
        ref = LLVM.LLVMConstInt(type.ref, value, signExtend.toInt())
    }

    /**
     * @see [LLVM.LLVMConstIntOfArbitraryPrecision]
     */
    public constructor(type: IntType, words: List<Long>) : this() {
        ref = LLVM.LLVMConstIntOfArbitraryPrecision(
            type.ref,
            words.size,
            words.toLongArray()
        )
    }
}
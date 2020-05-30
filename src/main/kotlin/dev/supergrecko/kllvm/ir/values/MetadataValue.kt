package dev.supergrecko.kllvm.ir.values

import dev.supergrecko.kllvm.ir.Value
import org.bytedeco.llvm.LLVM.LLVMValueRef

public class MetadataValue internal constructor() : Value() {
    /**
     * Construct a new Type from an LLVM pointer reference
     */
    public constructor(llvmValue: LLVMValueRef) : this() {
        ref = llvmValue
    }
}

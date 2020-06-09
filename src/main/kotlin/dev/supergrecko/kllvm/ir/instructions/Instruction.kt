package dev.supergrecko.kllvm.ir.instructions

import dev.supergrecko.kllvm.ir.Value
import dev.supergrecko.kllvm.ir.values.DebugLocationValue
import org.bytedeco.llvm.LLVM.LLVMValueRef

public class Instruction internal constructor() : Value(), DebugLocationValue {
    /**
     * Construct a new Type from an LLVM pointer reference
     */
    public constructor(llvmValue: LLVMValueRef) : this() {
        ref = llvmValue
    }
}

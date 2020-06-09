package dev.supergrecko.vexe.llvm.ir.instructions

import dev.supergrecko.vexe.llvm.ir.Value
import dev.supergrecko.vexe.llvm.ir.values.DebugLocationValue
import org.bytedeco.llvm.LLVM.LLVMValueRef

public class Instruction internal constructor() : Value(), DebugLocationValue {
    /**
     * Construct a new Type from an LLVM pointer reference
     */
    public constructor(llvmValue: LLVMValueRef) : this() {
        ref = llvmValue
    }
}

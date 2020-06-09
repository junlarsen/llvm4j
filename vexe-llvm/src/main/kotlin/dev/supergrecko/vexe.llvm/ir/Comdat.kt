package dev.supergrecko.vexe.llvm.ir

import dev.supergrecko.vexe.llvm.internal.contracts.ContainsReference
import org.bytedeco.llvm.LLVM.LLVMComdat

public class Comdat internal constructor() : ContainsReference<LLVMComdat> {
    public override lateinit var ref: LLVMComdat

    public constructor(comdat: LLVMComdat) : this() {
        ref = comdat
    }
}

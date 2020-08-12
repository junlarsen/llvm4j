package io.vexelabs.bitbuilder.llvm.ir

import io.vexelabs.bitbuilder.llvm.internal.contracts.ContainsReference
import org.bytedeco.llvm.LLVM.LLVMComdat

public class Comdat internal constructor() : ContainsReference<LLVMComdat> {
    public override lateinit var ref: LLVMComdat
        internal set

    public constructor(comdat: LLVMComdat) : this() {
        ref = comdat
    }
}

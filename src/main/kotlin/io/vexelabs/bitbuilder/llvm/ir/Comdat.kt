package io.vexelabs.bitbuilder.llvm.ir

import io.vexelabs.bitbuilder.llvm.internal.contracts.ContainsReference
import org.bytedeco.llvm.LLVM.LLVMComdatRef

public class Comdat internal constructor() : ContainsReference<LLVMComdatRef> {
    public override lateinit var ref: LLVMComdatRef
        internal set

    public constructor(comdat: LLVMComdatRef) : this() {
        ref = comdat
    }
}

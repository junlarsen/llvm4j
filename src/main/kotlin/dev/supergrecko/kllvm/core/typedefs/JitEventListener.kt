package dev.supergrecko.kllvm.core.typedefs

import org.bytedeco.llvm.LLVM.LLVMJITEventListenerRef

public class JitEventListener internal constructor() {
    internal lateinit var ref: LLVMJITEventListenerRef

    public constructor(listener: LLVMJITEventListenerRef) : this() {
        ref = listener
    }
}

package dev.supergrecko.kllvm.core.typedefs

import org.bytedeco.llvm.LLVM.LLVMJITEventListenerRef

public class JitEventListener internal constructor(listener: LLVMJITEventListenerRef) {
    internal var ref: LLVMJITEventListenerRef = listener
}

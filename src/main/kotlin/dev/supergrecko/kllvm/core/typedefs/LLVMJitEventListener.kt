package dev.supergrecko.kllvm.core.typedefs

import org.bytedeco.llvm.LLVM.LLVMJITEventListenerRef

public class LLVMJitEventListener internal constructor(internal val llvmListener: LLVMJITEventListenerRef)
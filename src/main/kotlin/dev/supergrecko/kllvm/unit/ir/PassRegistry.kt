package dev.supergrecko.kllvm.unit.ir

import org.bytedeco.llvm.LLVM.LLVMPassRegistryRef
import org.bytedeco.llvm.global.LLVM

/**
 * The LLVM Pass Registry
 *
 * This registry will always return a reference to the same LLVM Pass Registry
 * as the registry is a singleton in LLVM.
 *
 * [See](https://llvm.org/doxygen/group__LLVMCCorePassRegistry.html)
 */
public class PassRegistry public constructor() {
    internal val ref: LLVMPassRegistryRef = LLVM.LLVMGetGlobalPassRegistry()
}

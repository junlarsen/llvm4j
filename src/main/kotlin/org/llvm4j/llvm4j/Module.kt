package org.llvm4j.llvm4j

import org.bytedeco.llvm.LLVM.LLVMModuleFlagEntry
import org.bytedeco.llvm.LLVM.LLVMModuleRef
import org.llvm4j.llvm4j.util.Owner

public class Module public constructor(ptr: LLVMModuleRef) : Owner<LLVMModuleRef> {
    public override val ref: LLVMModuleRef = ptr

    public class FlagEntry public constructor(ptr: LLVMModuleFlagEntry) : Owner<LLVMModuleFlagEntry> {
        public override val ref: LLVMModuleFlagEntry = ptr
    }
}

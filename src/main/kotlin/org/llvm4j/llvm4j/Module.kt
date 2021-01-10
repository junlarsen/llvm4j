package org.llvm4j.llvm4j

import org.bytedeco.llvm.LLVM.LLVMModuleFlagEntry
import org.bytedeco.llvm.LLVM.LLVMModuleRef
import org.llvm4j.llvm4j.util.Enumeration
import org.llvm4j.llvm4j.util.Owner

public class Module public constructor(ptr: LLVMModuleRef) : Owner<LLVMModuleRef> {
    public override val ref: LLVMModuleRef = ptr

    public class FlagEntry public constructor(ptr: LLVMModuleFlagEntry) : Owner<LLVMModuleFlagEntry> {
        public override val ref: LLVMModuleFlagEntry = ptr
    }
}

public sealed class AddressSpace(public override val value: Int) : Enumeration.EnumVariant {
    public object Generic : AddressSpace(0)
    public class Other(value: Int) : AddressSpace(value)

    public companion object : Enumeration.WithFallback<AddressSpace>({ Other(it) }, Generic)
}
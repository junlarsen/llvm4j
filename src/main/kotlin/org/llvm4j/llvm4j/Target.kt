package org.llvm4j.llvm4j

import org.bytedeco.llvm.LLVM.LLVMTargetDataRef
import org.bytedeco.llvm.LLVM.LLVMTargetLibraryInfoRef
import org.bytedeco.llvm.LLVM.LLVMTargetMachineRef
import org.bytedeco.llvm.LLVM.LLVMTargetRef
import org.llvm4j.llvm4j.util.Owner

public class Target public constructor(ptr: LLVMTargetRef) : Owner<LLVMTargetRef> {
    public override val ref: LLVMTargetRef = ptr
}

public class TargetData public constructor(ptr: LLVMTargetDataRef) : Owner<LLVMTargetDataRef> {
    public override val ref: LLVMTargetDataRef = ptr
}

public class TargetLibraryInfo public constructor(ptr: LLVMTargetLibraryInfoRef) : Owner<LLVMTargetLibraryInfoRef> {
    public override val ref: LLVMTargetLibraryInfoRef = ptr
}

public class TargetMachine public constructor(ptr: LLVMTargetMachineRef) : Owner<LLVMTargetMachineRef> {
    public override val ref: LLVMTargetMachineRef = ptr
}

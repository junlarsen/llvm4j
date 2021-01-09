package org.llvm4j.llvm4j

import org.bytedeco.llvm.LLVM.LLVMTargetLibraryInfoRef
import org.llvm4j.llvm4j.util.Owner

public class TargetLibraryInfo public constructor(ptr: LLVMTargetLibraryInfoRef) : Owner<LLVMTargetLibraryInfoRef> {
    public override val ref: LLVMTargetLibraryInfoRef = ptr
}

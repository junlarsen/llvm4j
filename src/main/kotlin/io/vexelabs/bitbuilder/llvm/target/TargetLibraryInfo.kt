package io.vexelabs.bitbuilder.llvm.target

import io.vexelabs.bitbuilder.llvm.internal.contracts.ContainsReference
import org.bytedeco.llvm.LLVM.LLVMTargetLibraryInfoRef

public class TargetLibraryInfo internal constructor() :
    ContainsReference<LLVMTargetLibraryInfoRef> {
    public override lateinit var ref: LLVMTargetLibraryInfoRef
        internal set

    public constructor(llvmRef: LLVMTargetLibraryInfoRef) : this() {
        ref = llvmRef
    }

    //region Target
    //endregion Target
}
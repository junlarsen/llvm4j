package org.llvm4j.llvm4j

import org.bytedeco.llvm.LLVM.LLVMTargetMachineRef
import org.llvm4j.llvm4j.util.Owner

public class TargetMachine public constructor(ptr: LLVMTargetMachineRef) : Owner<LLVMTargetMachineRef> {
    public override val ref: LLVMTargetMachineRef = ptr
}

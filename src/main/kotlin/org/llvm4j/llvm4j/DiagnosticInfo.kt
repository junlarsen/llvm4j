package org.llvm4j.llvm4j

import org.bytedeco.llvm.LLVM.LLVMDiagnosticInfoRef
import org.llvm4j.llvm4j.util.Owner

public class DiagnosticInfo public constructor(ptr: LLVMDiagnosticInfoRef) : Owner<LLVMDiagnosticInfoRef> {
    public override val ref: LLVMDiagnosticInfoRef = ptr
}

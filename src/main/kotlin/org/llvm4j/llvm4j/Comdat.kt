package org.llvm4j.llvm4j

import org.bytedeco.llvm.LLVM.LLVMComdatRef
import org.llvm4j.llvm4j.util.Owner

public class Comdat public constructor(ptr: LLVMComdatRef) : Owner<LLVMComdatRef> {
    public override val ref: LLVMComdatRef = ptr
}

package org.llvm4j.llvm4j

import org.bytedeco.llvm.LLVM.LLVMGenericValueRef
import org.llvm4j.llvm4j.util.Owner

public class GenericValue public constructor(ptr: LLVMGenericValueRef) : Owner<LLVMGenericValueRef> {
    public override val ref: LLVMGenericValueRef = ptr
}

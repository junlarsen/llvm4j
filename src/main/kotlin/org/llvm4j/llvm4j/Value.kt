package org.llvm4j.llvm4j

import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.llvm4j.llvm4j.util.Owner

public interface Value : Owner<LLVMValueRef>

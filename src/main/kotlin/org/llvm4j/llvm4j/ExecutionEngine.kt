package org.llvm4j.llvm4j

import org.bytedeco.llvm.LLVM.LLVMExecutionEngineRef
import org.bytedeco.llvm.LLVM.LLVMGenericValueRef
import org.bytedeco.llvm.LLVM.LLVMMCJITCompilerOptions
import org.llvm4j.llvm4j.util.Owner

public class ExecutionEngine public constructor(ptr: LLVMExecutionEngineRef) : Owner<LLVMExecutionEngineRef> {
    public override val ref: LLVMExecutionEngineRef = ptr

    public class MCJITCompilerOptions public constructor(ptr: LLVMMCJITCompilerOptions) : Owner<LLVMMCJITCompilerOptions> {
        public override val ref: LLVMMCJITCompilerOptions = ptr
    }
}

public class GenericValue public constructor(ptr: LLVMGenericValueRef) : Owner<LLVMGenericValueRef> {
    public override val ref: LLVMGenericValueRef = ptr
}

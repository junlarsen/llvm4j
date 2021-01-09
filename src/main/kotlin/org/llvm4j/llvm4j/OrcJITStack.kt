package org.llvm4j.llvm4j

import org.bytedeco.llvm.LLVM.LLVMErrorRef
import org.bytedeco.llvm.LLVM.LLVMErrorTypeId
import org.bytedeco.llvm.LLVM.LLVMJITEventListenerRef
import org.bytedeco.llvm.LLVM.LLVMOrcJITStackRef
import org.llvm4j.llvm4j.util.Owner

public class OrcJITStack public constructor(ptr: LLVMOrcJITStackRef) : Owner<LLVMOrcJITStackRef> {
    public override val ref: LLVMOrcJITStackRef = ptr

    public class Error public constructor(ptr: LLVMErrorRef) : Owner<LLVMErrorRef> {
        public override val ref: LLVMErrorRef = ptr
    }

    public class ErrorTypeId public constructor(ptr: LLVMErrorTypeId) : Owner<LLVMErrorTypeId> {
        public override val ref: LLVMErrorTypeId = ptr
    }

    public class JITEventListener public constructor(ptr: LLVMJITEventListenerRef) : Owner<LLVMJITEventListenerRef> {
        public override val ref: LLVMJITEventListenerRef = ptr
    }
}
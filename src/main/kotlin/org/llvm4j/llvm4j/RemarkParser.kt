package org.llvm4j.llvm4j

import org.bytedeco.llvm.LLVM.LLVMRemarkArgRef
import org.bytedeco.llvm.LLVM.LLVMRemarkDebugLocRef
import org.bytedeco.llvm.LLVM.LLVMRemarkEntryRef
import org.bytedeco.llvm.LLVM.LLVMRemarkParserRef
import org.bytedeco.llvm.LLVM.LLVMRemarkStringRef
import org.llvm4j.llvm4j.util.Owner

public class RemarkParser public constructor(ptr: LLVMRemarkParserRef) : Owner<LLVMRemarkParserRef> {
    public override val ref: LLVMRemarkParserRef = ptr
}

public class RemarkEntry public constructor(ptr: LLVMRemarkEntryRef) : Owner<LLVMRemarkEntryRef> {
    public override val ref: LLVMRemarkEntryRef = ptr
}

public class RemarkArgument public constructor(ptr: LLVMRemarkArgRef) : Owner<LLVMRemarkArgRef> {
    public override val ref: LLVMRemarkArgRef = ptr
}

public class RemarkDebugLocation public constructor(ptr: LLVMRemarkDebugLocRef) : Owner<LLVMRemarkDebugLocRef> {
    public override val ref: LLVMRemarkDebugLocRef = ptr
}

public class RemarkString public constructor(ptr: LLVMRemarkStringRef) : Owner<LLVMRemarkStringRef> {
    public override val ref: LLVMRemarkStringRef = ptr
}

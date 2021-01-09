package org.llvm4j.llvm4j

import org.bytedeco.llvm.LLVM.LLVMMetadataRef
import org.bytedeco.llvm.LLVM.LLVMNamedMDNodeRef
import org.bytedeco.llvm.LLVM.LLVMValueMetadataEntry
import org.llvm4j.llvm4j.util.Owner

public class Metadata public constructor(ptr: LLVMMetadataRef) : Owner<LLVMMetadataRef> {
    public override val ref: LLVMMetadataRef = ptr
}

public class ValueMetadataEntry public constructor(ptr: LLVMValueMetadataEntry) : Owner<LLVMValueMetadataEntry> {
    public override val ref: LLVMValueMetadataEntry = ptr
}

public class NamedMetadataNode public constructor(ptr: LLVMNamedMDNodeRef) : Owner<LLVMNamedMDNodeRef> {
    public override val ref: LLVMNamedMDNodeRef = ptr
}
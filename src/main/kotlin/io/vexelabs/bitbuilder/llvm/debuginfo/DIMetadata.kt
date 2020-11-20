package io.vexelabs.bitbuilder.llvm.debuginfo

import io.vexelabs.bitbuilder.llvm.ir.Metadata
import org.bytedeco.llvm.LLVM.LLVMMetadataRef

public class DIMetadata internal constructor() : Metadata() {
    public constructor(llvmRef: LLVMMetadataRef) {
        ref = llvmRef
    }
}
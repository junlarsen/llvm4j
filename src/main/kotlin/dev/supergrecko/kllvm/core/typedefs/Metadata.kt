package dev.supergrecko.kllvm.core.typedefs

import org.bytedeco.llvm.LLVM.LLVMMetadataRef

public class Metadata internal constructor(metadata: LLVMMetadataRef) {
    internal var ref: LLVMMetadataRef = metadata
}

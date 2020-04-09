package dev.supergrecko.kllvm.llvm.typedefs

import org.bytedeco.llvm.LLVM.LLVMMetadataRef

public class Metadata internal constructor() {
    internal lateinit var ref: LLVMMetadataRef

    public constructor(metadata: LLVMMetadataRef) : this() {
        ref = metadata
    }
}

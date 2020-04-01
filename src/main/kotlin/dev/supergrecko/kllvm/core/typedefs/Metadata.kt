package dev.supergrecko.kllvm.core.typedefs

import org.bytedeco.llvm.LLVM.LLVMMetadataRef

public class Metadata internal constructor() {
    internal lateinit var ref: LLVMMetadataRef

    internal constructor(metadata: LLVMMetadataRef) : this() {
        ref = metadata
    }
}

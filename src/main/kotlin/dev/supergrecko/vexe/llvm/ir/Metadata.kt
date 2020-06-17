package dev.supergrecko.vexe.llvm.ir

import dev.supergrecko.vexe.llvm.internal.contracts.ContainsReference
import org.bytedeco.llvm.LLVM.LLVMMetadataRef

public class Metadata internal constructor() :
    ContainsReference<LLVMMetadataRef> {
    public override lateinit var ref: LLVMMetadataRef

    public constructor(metadata: LLVMMetadataRef) : this() {
        ref = metadata
    }
}

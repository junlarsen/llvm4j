package dev.supergrecko.kllvm.types

import dev.supergrecko.kllvm.core.typedefs.Context
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM

public class MetadataType public constructor(context: Context = Context.getGlobalContext()) :
    Type() {

    init {
        ref = LLVM.LLVMMetadataTypeInContext(context.ref)
        requireKind(TypeKind.Metadata)
    }

    public constructor(llvmType: LLVMTypeRef) : this() {
        ref = llvmType
    }
}

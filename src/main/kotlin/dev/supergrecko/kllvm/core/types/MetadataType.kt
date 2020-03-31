package dev.supergrecko.kllvm.core.types

import dev.supergrecko.kllvm.core.typedefs.Context
import dev.supergrecko.kllvm.core.typedefs.Type
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM

public class MetadataType(llvmType: LLVMTypeRef) : Type(llvmType) {
    public constructor(type: Type) : this(type.ref)

    public constructor(context: Context = Context.getGlobalContext()) {
        ref = LLVM.LLVMMetadataTypeInContext(context.ref)
    }
}

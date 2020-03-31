package dev.supergrecko.kllvm.core.types

import dev.supergrecko.kllvm.core.typedefs.Context
import dev.supergrecko.kllvm.core.typedefs.Type
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM

public class TokenType(llvmType: LLVMTypeRef) : Type(llvmType) {

    public constructor(context: Context = Context.getGlobalContext()) {
        ref = LLVM.LLVMTokenTypeInContext(context.ref)
    }
}

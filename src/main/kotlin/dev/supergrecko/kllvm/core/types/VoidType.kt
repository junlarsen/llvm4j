package dev.supergrecko.kllvm.core.types

import dev.supergrecko.kllvm.core.typedefs.Type
import org.bytedeco.llvm.LLVM.LLVMTypeRef

public class VoidType(llvmType: LLVMTypeRef) : Type(llvmType)
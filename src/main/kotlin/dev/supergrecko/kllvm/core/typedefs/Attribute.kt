package dev.supergrecko.kllvm.core.typedefs

import org.bytedeco.llvm.LLVM.LLVMAttributeRef
import org.bytedeco.llvm.LLVM.LLVMBuilderRef

public class Attribute internal constructor(internal val llvmAttribute: LLVMAttributeRef)

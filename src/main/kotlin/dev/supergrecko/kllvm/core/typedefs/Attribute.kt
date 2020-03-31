package dev.supergrecko.kllvm.core.typedefs

import org.bytedeco.llvm.LLVM.LLVMAttributeRef

public class Attribute internal constructor(attribute: LLVMAttributeRef) {
    internal var ref: LLVMAttributeRef = attribute
}

package dev.supergrecko.kllvm.core.typedefs

import org.bytedeco.llvm.LLVM.LLVMAttributeRef

public class Attribute internal constructor() {
    internal lateinit var ref: LLVMAttributeRef

    internal constructor(attribute: LLVMAttributeRef) : this() {
        ref = attribute
    }
}

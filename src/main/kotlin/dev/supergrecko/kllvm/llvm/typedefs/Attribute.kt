package dev.supergrecko.kllvm.llvm.typedefs

import org.bytedeco.llvm.LLVM.LLVMAttributeRef

public class Attribute internal constructor() {
    internal lateinit var ref: LLVMAttributeRef

    /**
     * TODO: Make these constructors internal (see #63)
     */
    public constructor(attribute: LLVMAttributeRef) : this() {
        ref = attribute
    }
}

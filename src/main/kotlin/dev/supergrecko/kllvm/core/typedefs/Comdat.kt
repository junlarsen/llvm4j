package dev.supergrecko.kllvm.core.typedefs

import org.bytedeco.llvm.LLVM.LLVMComdat

public class Comdat internal constructor() {
    internal lateinit var ref: LLVMComdat

    internal constructor(comdat: LLVMComdat) : this() {
        ref = comdat
    }
}

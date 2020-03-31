package dev.supergrecko.kllvm.core.typedefs

import org.bytedeco.llvm.LLVM.LLVMComdat

public class Comdat internal constructor(comdat: LLVMComdat) {
    internal var ref: LLVMComdat = comdat
}

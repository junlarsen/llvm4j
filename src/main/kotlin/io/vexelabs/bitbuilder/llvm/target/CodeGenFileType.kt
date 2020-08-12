package io.vexelabs.bitbuilder.llvm.target

import io.vexelabs.bitbuilder.llvm.internal.contracts.OrderedEnum
import org.bytedeco.llvm.global.LLVM

public enum class CodeGenFileType(public override val value: Int) :
    OrderedEnum<Int> {
    AssemblyFile(LLVM.LLVMAssemblyFile),
    ObjectFile(LLVM.LLVMObjectFile)
}

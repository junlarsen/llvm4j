package dev.supergrecko.vexe.llvm.target

import dev.supergrecko.vexe.llvm.internal.contracts.OrderedEnum
import org.bytedeco.llvm.global.LLVM

public enum class CodeGenFileType(public override val value: Int) :
    OrderedEnum<Int> {
    AssemblyFile(LLVM.LLVMAssemblyFile),
    ObjectFile(LLVM.LLVMObjectFile)
}

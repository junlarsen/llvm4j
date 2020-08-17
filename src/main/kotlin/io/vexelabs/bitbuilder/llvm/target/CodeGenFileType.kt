package io.vexelabs.bitbuilder.llvm.target

import io.vexelabs.bitbuilder.llvm.internal.contracts.ForeignEnum
import org.bytedeco.llvm.global.LLVM

public enum class CodeGenFileType(public override val value: Int) :
    ForeignEnum<Int> {
    AssemblyFile(LLVM.LLVMAssemblyFile),
    ObjectFile(LLVM.LLVMObjectFile);

    public companion object : ForeignEnum.CompanionBase<Int, CodeGenFileType> {
        public override val map: Map<Int, CodeGenFileType> by lazy {
            values().associateBy(CodeGenFileType::value)
        }
    }
}

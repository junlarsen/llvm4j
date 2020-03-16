package dev.supergrecko.kllvm.core.enumerations

import dev.supergrecko.kllvm.contracts.OrderedEnum
import org.bytedeco.llvm.global.LLVM

public enum class LLVMDLLStorageClass(public override val value: Int) : OrderedEnum<Int> {
    DefaultStorageClass(LLVM.LLVMDefaultStorageClass),
    DLLImportStorageClass(LLVM.LLVMDLLImportStorageClass),
    DLLExportStorageClass(LLVM.LLVMDLLExportStorageClass)
}
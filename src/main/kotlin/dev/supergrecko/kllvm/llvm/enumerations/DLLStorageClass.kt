package dev.supergrecko.kllvm.llvm.enumerations

import dev.supergrecko.kllvm.internal.contracts.OrderedEnum
import org.bytedeco.llvm.global.LLVM

/**
 * Support type matching LLVMDLLStorageClass
 *
 * [Documentation](https://llvm.org/doxygen/group__LLVMCCoreTypes.html)
 */
public enum class DLLStorageClass(public override val value: Int) : OrderedEnum<Int> {
    Default(LLVM.LLVMDefaultStorageClass),
    DLLImport(LLVM.LLVMDLLImportStorageClass),
    DLLExport(LLVM.LLVMDLLExportStorageClass)
}
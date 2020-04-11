package dev.supergrecko.kllvm.llvm.enumerations

import dev.supergrecko.kllvm.internal.contracts.OrderedEnum
import org.bytedeco.llvm.global.LLVM

/**
 * Support type matching LLVMLinkage
 *
 * [Documentation](https://llvm.org/doxygen/group__LLVMCCoreTypes.html)
 */
public enum class Linkage(public override val value: Int) : OrderedEnum<Int> {
    AvailableExternally(LLVM.LLVMAvailableExternallyLinkage),
    LinkOnceAny(LLVM.LLVMLinkOnceAnyLinkage),
    LinkOnceODR(LLVM.LLVMLinkOnceODRLinkage),
    LinkOnceODRAutoHide(LLVM.LLVMLinkOnceODRAutoHideLinkage),
    WeakAny(LLVM.LLVMWeakAnyLinkage),
    WeakODR(LLVM.LLVMWeakODRLinkage),
    Appending(LLVM.LLVMAppendingLinkage),
    Internal(LLVM.LLVMInternalLinkage),
    Private(LLVM.LLVMPrivateLinkage),
    DLLImport(LLVM.LLVMDLLImportLinkage),
    DLLExport(LLVM.LLVMDLLExportLinkage),
    ExternalWeak(LLVM.LLVMExternalWeakLinkage),
    Ghost(LLVM.LLVMGhostLinkage),
    Common(LLVM.LLVMCommonLinkage),
    LinkerPrivate(LLVM.LLVMLinkerPrivateLinkage),
    LinkerPrivateWeak(LLVM.LLVMLinkerPrivateWeakLinkage)
}
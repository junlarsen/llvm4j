package dev.supergrecko.kllvm.core.enumerations

import dev.supergrecko.kllvm.contracts.OrderedEnum
import org.bytedeco.llvm.global.LLVM

public enum class LLVMLinkage(public override val value: Int) : OrderedEnum<Int> {
    AvailableExternallyLinkage(LLVM.LLVMAvailableExternallyLinkage),
    LinkOnceAnyLinkage(LLVM.LLVMLinkOnceAnyLinkage),
    LinkOnceODRLinkage(LLVM.LLVMLinkOnceODRLinkage),
    LinkOnceODRAutoHideLinkage(LLVM.LLVMLinkOnceODRAutoHideLinkage),
    WeakAnyLinkage(LLVM.LLVMWeakAnyLinkage),
    WeakODRLinkage(LLVM.LLVMWeakODRLinkage),
    AppendingLinkage(LLVM.LLVMAppendingLinkage),
    InternalLinkage(LLVM.LLVMInternalLinkage),
    PrivateLinkage(LLVM.LLVMPrivateLinkage),
    DLLImportLinkage(LLVM.LLVMDLLImportLinkage),
    DLLExportLinkage(LLVM.LLVMDLLExportLinkage),
    ExternalWeakLinkage(LLVM.LLVMExternalWeakLinkage),
    GhostLinkage(LLVM.LLVMGhostLinkage),
    CommonLinkage(LLVM.LLVMCommonLinkage),
    LinkerPrivateLinkage(LLVM.LLVMLinkerPrivateLinkage),
    LinkerPrivateWeakLinkage(LLVM.LLVMLinkerPrivateWeakLinkage)
}
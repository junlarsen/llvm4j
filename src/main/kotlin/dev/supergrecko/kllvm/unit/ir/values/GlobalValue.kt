package dev.supergrecko.kllvm.unit.ir.values

import dev.supergrecko.kllvm.unit.internal.contracts.OrderedEnum
import dev.supergrecko.kllvm.unit.internal.util.fromLLVMBool
import dev.supergrecko.kllvm.unit.ir.DLLStorageClass
import dev.supergrecko.kllvm.unit.ir.Metadata
import dev.supergrecko.kllvm.unit.ir.MetadataEntries
import dev.supergrecko.kllvm.unit.ir.Module
import dev.supergrecko.kllvm.unit.ir.Type
import dev.supergrecko.kllvm.unit.ir.UnnamedAddress
import dev.supergrecko.kllvm.unit.ir.Value
import dev.supergrecko.kllvm.unit.ir.Visibility
import org.bytedeco.javacpp.SizeTPointer
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

/**
 * Enum representing linkage types
 */
public enum class Linkage(public override val value: Int) : OrderedEnum<Int> {
    External(LLVM.LLVMExternalLinkage),
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
    PrivateWeak(LLVM.LLVMLinkerPrivateWeakLinkage),
}

public open class GlobalValue internal constructor() : Value(), ConstantValue {
    /**
     * Construct a new Type from an LLVM pointer reference
     */
    public constructor(value: LLVMValueRef) : this() {
        ref = value
    }

    //region Core::Values::Constants::GlobalValues
    public fun getLinkage(): Linkage {
        val ln = LLVM.LLVMGetLinkage(ref)

        return Linkage.values().first { it.value == ln }
    }

    public fun setLinkage(linkage: Linkage) {
        LLVM.LLVMSetLinkage(ref, linkage.value)
    }

    public fun getSection(): String {
        return LLVM.LLVMGetSection(ref).string
    }

    public fun setSection(data: String) {
        LLVM.LLVMSetSection(ref, data)
    }

    public fun getVisibility(): Visibility {
        val visibility = LLVM.LLVMGetVisibility(ref)

        return Visibility.values().first { it.value == visibility }
    }

    public fun setVisibility(visibility: Visibility) {
        LLVM.LLVMSetVisibility(ref, visibility.value)
    }

    public fun getStorageClass(): DLLStorageClass {
        val storage = LLVM.LLVMGetDLLStorageClass(ref)

        return DLLStorageClass.values().first { it.value == storage }
    }

    public fun setStorageClass(storageClass: DLLStorageClass) {
        LLVM.LLVMSetDLLStorageClass(ref, storageClass.value)
    }

    public fun getUnnamedAddress(): UnnamedAddress {
        val addr = LLVM.LLVMGetUnnamedAddress(ref)

        return UnnamedAddress.values().first { it.value == addr }
    }

    public fun setUnnamedAddress(address: UnnamedAddress) {
        LLVM.LLVMSetUnnamedAddress(ref, address.value)
    }

    /**
     * Determine if this is just a signature for something
     *
     * Returns true if the primary definition of this global value is outside
     * of the current translation unit.
     *
     * @see LLVM.LLVMIsDeclaration
     */
    public fun isDeclaration(): Boolean {
        return LLVM.LLVMIsDeclaration(ref).fromLLVMBool()
    }

    /**
     * Get the module this global value resides in
     */
    public fun getModule(): Module {
        val mod = LLVM.LLVMGetGlobalParent(ref)

        return Module(mod)
    }

    /**
     * Get the type of this value
     *
     * @see LLVM.LLVMGlobalGetValueType
     */
    public override fun getType(): Type {
        val ty = LLVM.LLVMGlobalGetValueType(ref)

        return Type(ty)
    }

    /**
     * Set the alignment (alignas) of this value
     *
     * @see LLVM.LLVMSetAlignment
     */
    public fun setAlignment(align: Int) {
        return LLVM.LLVMSetAlignment(ref, align)
    }

    /**
     * Get the alignment (alignas) of this value
     *
     * @see LLVM.LLVMGetAlignment
     */
    public fun getAlignment(): Int {
        return LLVM.LLVMGetAlignment(ref)
    }

    /**
     * Attach a [kind] of metadata
     *
     * @see LLVM.LLVMGlobalSetMetadata
     */
    public fun setMetadata(kind: Int, metadata: Metadata) {
        LLVM.LLVMGlobalSetMetadata(ref, kind, metadata.ref)
    }

    /**
     * Erases a piece of metadata at [kind] if it exists
     *
     * @see LLVM.LLVMGlobalEraseMetadata
     */
    public fun eraseMetadata(kind: Int) {
        LLVM.LLVMGlobalEraseMetadata(ref, kind)
    }

    /**
     * Removes all metadata from this value
     *
     * @see LLVM.LLVMGlobalClearMetadata
     */
    public fun clearMetadata() {
        LLVM.LLVMGlobalClearMetadata(ref)
    }

    /**
     * Copies all the metadata from this  value
     *
     * This produces a [MetadataEntries] which must be de-allocated by the
     * user via [MetadataEntries.dispose] otherwise memory will be leaked.
     *
     * @see LLVM.LLVMGlobalCopyAllMetadata
     */
    public fun copyMetadata(): MetadataEntries {
        val ptr = SizeTPointer(0)

        val entries = LLVM.LLVMGlobalCopyAllMetadata(ref, ptr)

        return MetadataEntries(entries)
    }
    //endregion Core::Values::Constants::GlobalValues
}

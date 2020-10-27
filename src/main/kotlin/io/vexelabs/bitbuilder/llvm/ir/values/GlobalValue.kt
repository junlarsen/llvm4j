package io.vexelabs.bitbuilder.llvm.ir.values

import io.vexelabs.bitbuilder.llvm.internal.contracts.ForeignEnum
import io.vexelabs.bitbuilder.llvm.internal.util.fromLLVMBool
import io.vexelabs.bitbuilder.llvm.ir.Comdat
import io.vexelabs.bitbuilder.llvm.ir.DLLStorageClass
import io.vexelabs.bitbuilder.llvm.ir.Metadata
import io.vexelabs.bitbuilder.llvm.ir.MetadataEntries
import io.vexelabs.bitbuilder.llvm.ir.Module
import io.vexelabs.bitbuilder.llvm.ir.Type
import io.vexelabs.bitbuilder.llvm.ir.UnnamedAddress
import io.vexelabs.bitbuilder.llvm.ir.Visibility
import org.bytedeco.javacpp.SizeTPointer
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public open class GlobalValue internal constructor() : ConstantValue() {
    public constructor(llvmRef: LLVMValueRef) : this() {
        ref = llvmRef
    }

    /**
     * Get the linkage type
     *
     * TODO: Research invalid matches
     *
     * @see LLVM.LLVMGetLinkage
     */
    public fun getLinkage(): Linkage {
        val ln = LLVM.LLVMGetLinkage(ref)

        return Linkage[ln]
    }

    /**
     * Set the linkage type
     *
     * @see LLVM.LLVMSetLinkage
     */
    public fun setLinkage(linkage: Linkage) {
        LLVM.LLVMSetLinkage(ref, linkage.value)
    }

    /**
     * Get the section
     *
     * @see LLVM.LLVMGetSection
     */
    public fun getSection(): String? {
        val ptr = LLVM.LLVMGetSection(ref)

        return ptr?.string
    }

    /**
     * Set the section
     *
     * @see LLVM.LLVMSetSection
     */
    public fun setSection(data: String) {
        LLVM.LLVMSetSection(ref, data)
    }

    /**
     * Get the visibility
     *
     * @see LLVM.LLVMGetVisibility
     */
    public fun getVisibility(): Visibility {
        val visibility = LLVM.LLVMGetVisibility(ref)

        return Visibility[visibility]
    }

    /**
     * Set the visibility
     *
     * @see LLVM.LLVMSetVisibility
     */
    public fun setVisibility(visibility: Visibility) {
        LLVM.LLVMSetVisibility(ref, visibility.value)
    }

    /**
     * Get the storage class
     *
     * @see LLVM.LLVMGetDLLStorageClass
     */
    public fun getStorageClass(): DLLStorageClass {
        val storage = LLVM.LLVMGetDLLStorageClass(ref)

        return DLLStorageClass[storage]
    }

    /**
     * Set the storage calss
     *
     * @see LLVM.LLVMSetDLLStorageClass
     */
    public fun setStorageClass(storageClass: DLLStorageClass) {
        LLVM.LLVMSetDLLStorageClass(ref, storageClass.value)
    }

    /**
     * Get unnamed address importance
     *
     * @see LLVM.LLVMGetUnnamedAddress
     */
    public fun getUnnamedAddress(): UnnamedAddress {
        val addr = LLVM.LLVMGetUnnamedAddress(ref)

        return UnnamedAddress[addr]
    }

    /**
     * Set unnamed address importance
     *
     * @see LLVM.LLVMSetUnnamedAddress
     */
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
     * Returns the "value type"
     *
     * This differs from the formal type of a global value which is always a
     * pointer type.
     *
     * @see LLVM.LLVMGlobalGetValueType
     */
    public fun getValueType(): Type {
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
        // Do not resourceScope this as it is passed to MetadataEntries
        val ptr = SizeTPointer(1)

        val entries = LLVM.LLVMGlobalCopyAllMetadata(ref, ptr)

        return MetadataEntries(entries, ptr)
    }

    /**
     * Get the comdat assigned to this value
     *
     * @see LLVM.LLVMGetComdat
     */
    public fun getComdat(): Comdat? {
        val comdat = LLVM.LLVMGetComdat(ref)

        return comdat?.let { Comdat(it) }
    }

    /**
     * Set the comdat for this value
     *
     * @see LLVM.LLVMSetComdat
     */
    public fun setComdat(comdat: Comdat) {
        LLVM.LLVMSetComdat(ref, comdat.ref)
    }

    public enum class Linkage(public override val value: Int) :
        ForeignEnum<Int> {
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
        PrivateWeak(LLVM.LLVMLinkerPrivateWeakLinkage);

        public companion object : ForeignEnum.CompanionBase<Int, Linkage> {
            public override val map: Map<Int, Linkage> by lazy {
                values().associateBy(Linkage::value)
            }
        }
    }
}

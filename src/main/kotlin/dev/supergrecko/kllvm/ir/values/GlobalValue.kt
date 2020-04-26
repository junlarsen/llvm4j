package dev.supergrecko.kllvm.ir.values

import dev.supergrecko.kllvm.internal.contracts.OrderedEnum
import dev.supergrecko.kllvm.internal.contracts.Unreachable
import dev.supergrecko.kllvm.internal.util.fromLLVMBool
import dev.supergrecko.kllvm.ir.DLLStorageClass
import dev.supergrecko.kllvm.ir.Metadata
import dev.supergrecko.kllvm.ir.MetadataEntries
import dev.supergrecko.kllvm.ir.Module
import dev.supergrecko.kllvm.ir.Type
import dev.supergrecko.kllvm.ir.UnnamedAddress
import dev.supergrecko.kllvm.ir.Value
import dev.supergrecko.kllvm.ir.Visibility
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

public open class GlobalValue internal constructor(): Value(), Constant {
    /**
     * Construct a new Type from an LLVM pointer reference
     */
    public constructor(value: LLVMValueRef) : this() {
        ref = value
    }

    //region Core::Values::Constants::GlobalValues
    public var linkage: Linkage
        get() {
            val refLinkage = LLVM.LLVMGetLinkage(ref)

            return Linkage.values()
                .firstOrNull { it.value == refLinkage }
                ?: throw Unreachable()
        }
        set(value) = LLVM.LLVMSetLinkage(ref, value.value)

    public var section: String
        get() = LLVM.LLVMGetSection(ref).string
        set(value) = LLVM.LLVMSetSection(ref, value)

    public var visibility: Visibility
        get() {
            val refVisibility = LLVM.LLVMGetVisibility(ref)

            return Visibility.values()
                .firstOrNull { it.value == refVisibility }
                ?: throw Unreachable()
        }
    set(value) = LLVM.LLVMSetVisibility(ref, value.value)

    public var dllStorageClass: DLLStorageClass
        get() {
            val refStorageClass = LLVM.LLVMGetDLLStorageClass(ref)

            return DLLStorageClass.values()
                .firstOrNull { it.value == refStorageClass }
                ?: throw Unreachable()
        }
        set(value) = LLVM.LLVMSetDLLStorageClass(ref, value.value)

    public var unnamedAddress: UnnamedAddress
        get() {
            val refUnnamedAddress = LLVM.LLVMGetUnnamedAddress(ref)

            return UnnamedAddress.values()
                .firstOrNull { it.value == refUnnamedAddress }
                ?: throw Unreachable()
        }
        set(value) = LLVM.LLVMSetUnnamedAddress(ref, value.value)

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
        // TODO: Resolve LLVM ptr type
        val ptr = SizeTPointer(0)

        val entries = LLVM.LLVMGlobalCopyAllMetadata(ref, ptr)

        return MetadataEntries(entries)
    }
    //endregion Core::Values::Constants::GlobalValues
}
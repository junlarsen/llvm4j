package io.vexelabs.bitbuilder.llvm.target

import io.vexelabs.bitbuilder.llvm.internal.contracts.ContainsReference
import io.vexelabs.bitbuilder.llvm.internal.contracts.Disposable
import io.vexelabs.bitbuilder.llvm.internal.contracts.ForeignEnum
import io.vexelabs.bitbuilder.llvm.ir.Context
import io.vexelabs.bitbuilder.llvm.ir.Type
import io.vexelabs.bitbuilder.llvm.ir.types.IntType
import io.vexelabs.bitbuilder.llvm.ir.types.StructType
import io.vexelabs.bitbuilder.llvm.ir.values.GlobalVariable
import io.vexelabs.bitbuilder.llvm.support.Message
import org.bytedeco.llvm.LLVM.LLVMTargetDataRef
import org.bytedeco.llvm.global.LLVM

/**
 * Interface to llvm::TargetData
 *
 * A parsed version of the target data layout string in and methods for
 * querying it
 *
 * @see LLVMTargetDataRef
 */
public class TargetData internal constructor() :
    ContainsReference<LLVMTargetDataRef>, Disposable {
    public override var valid: Boolean = true
    public override lateinit var ref: LLVMTargetDataRef
        internal set

    public constructor(llvmRef: LLVMTargetDataRef) : this() {
        ref = llvmRef
    }

    /**
     * Create a new target data layout
     *
     * @see LLVM.LLVMCreateTargetData
     */
    public constructor(target: String) : this() {
        ref = LLVM.LLVMCreateTargetData(target)
    }

    /**
     * Get the string representation of this target
     *
     * This string must be manually de-allocated by the user
     *
     * @see LLVM.LLVMCopyStringRepOfTargetData
     * @see Message.dispose
     */
    public fun getStringRepresentation(): Message {
        val message = LLVM.LLVMCopyStringRepOfTargetData(ref)

        return Message(message)
    }

    /**
     * Get the byte ordering for this target
     *
     * Returns either LittleEndian or BigEndian
     *
     * @see LLVM.LLVMByteOrder
     */
    public fun getByteOrder(): ByteOrder {
        val order = LLVM.LLVMByteOrder(ref)

        return ByteOrder[order]
    }

    /**
     * Get the size of a pointer for this target in bytes
     *
     * @see LLVM.LLVMPointerSize
     * @see LLVM.LLVMPointerSizeForAS
     */
    public fun getPointerSize(forAddressSpace: Int? = null): Int {
        return if (forAddressSpace == null) {
            LLVM.LLVMPointerSize(ref)
        } else {
            LLVM.LLVMPointerSizeForAS(ref, forAddressSpace)
        }
    }

    /**
     * Get an the integer type which is the size of a pointer on this target
     *
     * The returned integer type will have the width of [getPointerSize]. The
     * type will be created inside the provided [context].
     *
     * @see getPointerSize
     * @see LLVM.LLVMIntPtrTypeInContext
     * @see LLVM.LLVMIntPtrTypeForASInContext
     */
    public fun getIntPointerType(
        context: Context,
        forAddressSpace: Int? = null
    ): IntType {
        val type = if (forAddressSpace == null) {
            LLVM.LLVMIntPtrTypeInContext(context.ref, ref)
        } else {
            LLVM.LLVMIntPtrTypeForASInContext(context.ref, ref, forAddressSpace)
        }

        return IntType(type)
    }

    /**
     * Computes the size of a type in bytes for this target
     *
     * @see LLVM.LLVMSizeOfTypeInBits
     */
    public fun getSizeOfType(type: Type): Long {
        return LLVM.LLVMSizeOfTypeInBits(ref, type.ref)
    }

    /**
     * Computes the storage size of a type in bytes for this target
     *
     * @see LLVM.LLVMStoreSizeOfType
     */
    public fun getStorageSizeOfType(type: Type): Long {
        return LLVM.LLVMStoreSizeOfType(ref, type.ref)
    }

    /**
     * Computes the ABI size of a type in bytes for this target
     *
     * @see LLVM.LLVMABISizeOfType
     */
    public fun getABISizeOfType(type: Type): Long {
        return LLVM.LLVMABISizeOfType(ref, type.ref)
    }

    /**
     * Computes the ABI alignment of a type in bytes for this target
     *
     * @see LLVM.LLVMABIAlignmentOfType
     */
    public fun getABIAlignmentOfType(type: Type): Int {
        return LLVM.LLVMABIAlignmentOfType(ref, type.ref)
    }

    /**
     * Computes the call frame alignment of a type in bytes for this target
     *
     * @see LLVM.LLVMCallFrameAlignmentOfType
     */
    public fun getCallFrameAlignmentOfType(type: Type): Int {
        return LLVM.LLVMCallFrameAlignmentOfType(ref, type.ref)
    }

    /**
     * Computes the preferred alignment of a type for this target
     *
     * @see LLVM.LLVMPreferredAlignmentOfType
     */
    public fun getPreferredAlignmentOfType(type: Type): Int {
        return LLVM.LLVMPreferredAlignmentOfType(ref, type.ref)
    }

    /**
     * Computes the preferred alignment of a global variable in bytes for
     * this target
     *
     * @see LLVM.LLVMPreferredAlignmentOfGlobal
     */
    public fun getPreferredAlignmentOfGlobal(global: GlobalVariable): Int {
        return LLVM.LLVMPreferredAlignmentOfGlobal(ref, global.ref)
    }

    /**
     * Given a valid byte offset into the structure, returns the structure
     * index that contains it
     *
     * @see LLVM.LLVMElementAtOffset
     */
    public fun getElementAtOffset(struct: StructType, offset: Long): Int {
        return LLVM.LLVMElementAtOffset(ref, struct.ref, offset)
    }

    /**
     * Computes the byte offset of the indexed struct element for a target
     *
     * @see LLVM.LLVMOffsetOfElement
     */
    public fun getOffsetOfElement(struct: StructType, element: Int): Long {
        return LLVM.LLVMOffsetOfElement(ref, struct.ref, element)
    }

    public enum class ByteOrder(public override val value: Int) :
        ForeignEnum<Int> {
        LittleEndian(LLVM.LLVMLittleEndian),
        BigEndian(LLVM.LLVMBigEndian);

        public companion object :
            ForeignEnum.CompanionBase<Int, ByteOrder> {
            public override val map: Map<Int, ByteOrder> by lazy {
                values().associateBy(ByteOrder::value)
            }
        }
    }

    public override fun dispose() {
        require(valid) { "Cannot dispose object twice" }

        valid = false

        LLVM.LLVMDisposeTargetData(ref)
    }
}

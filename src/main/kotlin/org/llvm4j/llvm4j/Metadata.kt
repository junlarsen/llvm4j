package org.llvm4j.llvm4j

import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.javacpp.SizeTPointer
import org.bytedeco.llvm.LLVM.LLVMMetadataRef
import org.bytedeco.llvm.LLVM.LLVMNamedMDNodeRef
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.LLVM.LLVMValueMetadataEntry
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM
import org.llvm4j.llvm4j.util.CorrespondsTo
import org.llvm4j.llvm4j.util.CustomApi
import org.llvm4j.llvm4j.util.Owner

@CorrespondsTo("llvm::Metadata")
public open class Metadata public constructor(ptr: LLVMMetadataRef) : Owner<LLVMMetadataRef> {
    public override val ref: LLVMMetadataRef = ptr
}

public class ValueMetadataEntry public constructor(ptr: LLVMValueMetadataEntry) : Owner<LLVMValueMetadataEntry> {
    public override val ref: LLVMValueMetadataEntry = ptr
}

/**
 * A [Value] disguised as a [Metadata]
 *
 * @see MetadataAsValue
 *
 * @author Mats Larsen
 */
@CorrespondsTo("llvm::ValueAsMetadata")
public class ValueAsMetadata(ptr: LLVMMetadataRef) : Metadata(ptr)

/**
 * A named metadata node which resides in a [Module]
 *
 * This uses the extended C API from JavaCPP
 *
 * TODO: Testing - Test once [ValueAsMetadata] and [MetadataAsValue] are implemented
 *
 * @author Mats Larsen
 */
@CorrespondsTo("llvm::NamedMDNode")
public class NamedMetadataNode public constructor(ptr: LLVMNamedMDNodeRef) : Owner<LLVMNamedMDNodeRef> {
    public override val ref: LLVMNamedMDNodeRef = ptr
    
    public fun getName(): String {
        val size = SizeTPointer(1L)
        val ptr = LLVM.LLVMGetNamedMetadataName(ref, size)
        val copy = ptr.string

        size.deallocate()
        ptr.deallocate()

        return copy
    }

    @CustomApi
    public fun getOperandCount(): Int {
        return LLVM.getNamedMDNodeNumOperands(ref)
    }

    @CustomApi
    public fun getOperands(targetContext: Context): Array<MetadataAsValue> {
        val size = getOperandCount()
        val buffer = PointerPointer<LLVMValueRef>(size.toLong())

        LLVM.getNamedMDNodeOperands(ref, buffer, targetContext.ref)

        return List(size) {
            LLVMValueRef(buffer.get(it.toLong()))
        }.map(::MetadataAsValue).toTypedArray()
    }

    @CustomApi
    public fun addOperand(operand: MetadataAsValue) {
        LLVM.addNamedMDNodeOperand(ref, operand.ref)
    }
}

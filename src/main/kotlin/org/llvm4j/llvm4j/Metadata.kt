package org.llvm4j.llvm4j

import org.bytedeco.javacpp.IntPointer
import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.javacpp.SizeTPointer
import org.bytedeco.llvm.LLVM.LLVMMetadataRef
import org.bytedeco.llvm.LLVM.LLVMNamedMDNodeRef
import org.bytedeco.llvm.LLVM.LLVMValueMetadataEntry
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM
import org.llvm4j.llvm4j.util.CorrespondsTo
import org.llvm4j.llvm4j.util.CustomApi
import org.llvm4j.llvm4j.util.Owner
import org.llvm4j.llvm4j.util.Result
import org.llvm4j.llvm4j.util.tryWith

/**
 * Metadata is program metadata which can be attached to instructions or functions in the IR which convey extra
 * information about the code to both optimizers and code generators.
 *
 * A widely used example of metadata are source-level debug information. LLVM has two kinds of metadata, strings and
 * nodes.
 *
 * Metadata strings are a single string value. Metadata nodes are a list of other metadata nodes. Any value can be
 * wrapped into metadata with [ValueAsMetadata].
 *
 * @see ValueAsMetadata
 * @see MetadataAsValue
 *
 * Known inheritors in the LLVM hierarchy:
 *
 * @see MetadataString
 * @see MetadataNode
 *
 * TODO: API - DebugInfo Metadata subtypes and APIs
 * TODO: Implemenent operands for [MetadataString], [MetadataNode] once javacpp-presets#1001 is merged
 */
@CorrespondsTo("llvm::Metadata")
public sealed class Metadata constructor(ptr: LLVMMetadataRef) : Owner<LLVMMetadataRef> {
    public override val ref: LLVMMetadataRef = ptr

    public fun toValue(context: Context): MetadataAsValue {
        val v = LLVM.LLVMMetadataAsValue(context.ref, ref)

        return MetadataAsValue(v)
    }
}

public class AnyMetadata public constructor(ptr: LLVMMetadataRef) : Metadata(ptr)

/**
 * A [Value] disguised as a [Metadata]
 *
 * @see MetadataAsValue
 *
 * @author Mats Larsen
 */
@CorrespondsTo("llvm::ValueAsMetadata")
public class ValueAsMetadata(ptr: LLVMMetadataRef) : Metadata(ptr)

@CorrespondsTo("llvm::MDNode")
public class MetadataNode public constructor(ptr: LLVMMetadataRef) : Metadata(ptr)

@CorrespondsTo("llvm::MDString")
public class MetadataString public constructor(ptr: LLVMMetadataRef) : Metadata(ptr)

/**
 * A named metadata node which resides in a [Module]
 *
 * This uses the extended C API from JavaCPP
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
        }.map(::MetadataAsValue).toTypedArray().also {
            buffer.deallocate()
        }
    }

    @CustomApi
    public fun addOperand(operand: MetadataAsValue) {
        LLVM.addNamedMDNodeOperand(ref, operand.ref)
    }
}

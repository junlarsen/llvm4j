package io.vexelabs.bitbuilder.llvm.ir

import io.vexelabs.bitbuilder.llvm.internal.contracts.ContainsReference
import io.vexelabs.bitbuilder.llvm.internal.contracts.PointerIterator
import io.vexelabs.bitbuilder.llvm.internal.util.map
import io.vexelabs.bitbuilder.raii.resourceScope
import io.vexelabs.bitbuilder.raii.toResource
import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.javacpp.SizeTPointer
import org.bytedeco.llvm.LLVM.LLVMModuleRef
import org.bytedeco.llvm.LLVM.LLVMNamedMDNodeRef
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

/**
 * Interface to llvm::NamedMDNode
 *
 * Represents a Named Metadata Node which is a top level metadata node in a
 * [Module]
 *
 * A named metadata node is essentially an array of other [Metadata] nodes.
 * These are named "operands" in the LLVM-C API and you can modify this list
 * with the [getOperands], [addOperand] and
 * [getOperandCount] member methods.
 *
 * @see LLVMNamedMDNodeRef
 */
public class NamedMetadataNode public constructor(
    public override val ref: LLVMNamedMDNodeRef,
    public val owner: LLVMModuleRef
) : ContainsReference<LLVMNamedMDNodeRef> {
    /**
     * Get the name of this named metadata node
     *
     * This name is immutable which means we can get it by lazy like this.
     */
    public val name: String by lazy { getNodeName() }

    /**
     * Retrieve the of this metadata node
     *
     * This is only ran once per named metadata node to cache the value into
     * [name]
     *
     * @see LLVM.LLVMGetNamedMetadataName
     */
    private fun getNodeName(): String {
        val len = SizeTPointer(1).toResource()

        return resourceScope(len) {
            val ptr = LLVM.LLVMGetNamedMetadataName(ref, it)
            val contents = ptr.string

            ptr.deallocate()

            return@resourceScope contents
        }
    }

    /**
     * Get all the operands in this list
     *
     * @see LLVM.LLVMGetNamedMetadataOperands
     */
    public fun getOperands(): List<Metadata> {
        val size = getOperandCount()
        val ptr = PointerPointer<LLVMValueRef>(size.toLong())

        LLVM.LLVMGetNamedMetadataOperands(owner, name, ptr)

        return ptr.map { Metadata.fromValue(Value(it)) }
    }

    /**
     * Get the amount of operands in this list
     *
     * @see LLVM.LLVMGetNamedMetadataNumOperands
     */
    public fun getOperandCount(): Int {
        return LLVM.LLVMGetNamedMetadataNumOperands(owner, name)
    }

    /**
     * Add a metadata operand to this list
     *
     * LLVM-C expects a metadata node as value (see [Metadata.toValue] for
     * this method, but because we know we actually require a metadata node,
     * we can do the conversion ourself, giving the user better type safety.
     *
     * This will by default use the context the [owner] module resides in,
     * but this can be changed by passing the [withContext] parameter.
     *
     * @see LLVM.LLVMAddNamedMetadataOperand
     */
    public fun addOperand(metadata: Metadata, withContext: Context? = null) {
        val ctx = withContext ?: Module(owner).getContext()
        val value = metadata.toValue(ctx)

        LLVM.LLVMAddNamedMetadataOperand(owner, name, value.ref)
    }

    /**
     * Class to perform iteration over named metadata nodes
     *
     * @see [PointerIterator]
     */
    public class Iterator(owner: Module, ref: LLVMNamedMDNodeRef) :
        PointerIterator<NamedMetadataNode, LLVMNamedMDNodeRef>(
            start = ref,
            yieldNext = { LLVM.LLVMGetNextNamedMetadata(it) },
            apply = { NamedMetadataNode(it, owner.ref) }
        )
}

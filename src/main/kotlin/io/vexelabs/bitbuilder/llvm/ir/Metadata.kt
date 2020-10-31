package io.vexelabs.bitbuilder.llvm.ir

import io.vexelabs.bitbuilder.llvm.internal.contracts.ContainsReference
import io.vexelabs.bitbuilder.internal.map
import io.vexelabs.bitbuilder.internal.resourceScope
import io.vexelabs.bitbuilder.internal.toPointerPointer
import io.vexelabs.bitbuilder.internal.toResource
import org.bytedeco.javacpp.IntPointer
import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.llvm.LLVM.LLVMMetadataRef
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

/**
 * Interface to llvm::Metadata
 *
 * LLVM IR allows metadata to be attached to instructions in the program that
 * can convey extra information about the code to the optimizers and code
 * generator.
 *
 * This can for example be used to generate debug information
 *
 * @see LLVMMetadataRef
 */
public open class Metadata internal constructor() :
    ContainsReference<LLVMMetadataRef> {
    public final override lateinit var ref: LLVMMetadataRef
        internal set

    public constructor(llvmRef: LLVMMetadataRef) : this() {
        ref = llvmRef
    }

    /**
     * Represent a [MetadataNode] which has been cast to a [Value]
     */
    public class MetadataAsValue(ref: LLVMValueRef) : Value(ref)

    /**
     * Represent a [Value] which has been cast to a [MetadataNode]
     */
    public class ValueAsMetadata(ref: LLVMMetadataRef) : Metadata(ref)

    /**
     * Cast this Metadata node to a value
     *
     * @see LLVM.LLVMMetadataAsValue
     */
    public fun toValue(
        withContext: Context = Context.getGlobalContext()
    ): MetadataAsValue {
        val md = LLVM.LLVMMetadataAsValue(withContext.ref, ref)

        return MetadataAsValue(md)
    }

    public companion object {
        /**
         * Cast a value to a [MetadataNode]
         *
         * @see LLVM.LLVMValueAsMetadata
         */
        public fun fromValue(value: Value): ValueAsMetadata {
            val md = LLVM.LLVMValueAsMetadata(value.ref)

            return ValueAsMetadata(md)
        }

        /**
         * Cast this Metadata node to a value
         *
         * LLVM-C accepts a [LLVMValueRef] for this so the node is cast to a
         * [Metadata.MetadataAsValue]. This requires a context. Set the context
         * which this should use with [withContext]
         *
         * @see LLVM.LLVMMetadataAsValue
         * @see LLVM.LLVMMetadataAsValue
         */
        public fun toValue(
            metadata: Metadata,
            withContext: Context = Context.getGlobalContext()
        ): MetadataAsValue = metadata.toValue(withContext)
    }

    /**
     * Test if this is a metadata string
     *
     * LLVM-C accepts a [LLVMValueRef] for this so the node is cast to a
     * [Metadata.MetadataAsValue]. This requires a context. Set the context
     * which this should use with [withContext]
     *
     * @see LLVM.LLVMIsAMDNode
     */
    public fun isString(
        withContext: Context = Context.getGlobalContext()
    ): Boolean {
        return LLVM.LLVMIsAMDString(toValue(withContext).ref) != null
    }

    /**
     * Test if this is a metadata node
     *
     * LLVM-C accepts a [LLVMValueRef] for this so the node is cast to a
     * [Metadata.MetadataAsValue]. This requires a context. Set the context
     * which this should use with [withContext]
     *
     * @see LLVM.LLVMIsAMDNode
     */
    public fun isNode(
        withContext: Context = Context.getGlobalContext()
    ): Boolean {
        return LLVM.LLVMIsAMDNode(toValue(withContext).ref) != null
    }
}

public class MetadataString internal constructor() : Metadata() {
    public constructor(llvmRef: LLVMMetadataRef) : this() {
        ref = llvmRef
    }

    public constructor(
        data: String,
        context: Context = Context.getGlobalContext()
    ) : this() {
        ref = LLVM.LLVMMDStringInContext2(
            context.ref,
            data,
            data.length.toLong()
        )
    }

    /**
     * Get the string from a MDString node
     *
     * @see LLVM.LLVMGetMDString
     */
    public fun getString(
        context: Context = Context.getGlobalContext()
    ): String {
        val len = IntPointer(1).toResource()

        return resourceScope(len) {
            val metadata = toValue(context)
            val ptr = LLVM.LLVMGetMDString(metadata.ref, it)
            val contents = ptr.string

            ptr.deallocate()

            return@resourceScope contents
        }
    }
}

public open class MetadataNode internal constructor() : Metadata() {
    public constructor(llvmRef: LLVMMetadataRef) : this() {
        ref = llvmRef
    }

    public constructor(
        values: List<Metadata>,
        context: Context = Context.getGlobalContext()
    ) : this() {
        val ptr = values.map { it.ref }.toPointerPointer()

        ref = LLVM.LLVMMDNodeInContext2(
            context.ref,
            ptr,
            values.size.toLong()
        )

        ptr.deallocate()
    }

    /**
     * Get the amount of operands in a metadata node
     *
     * LLVM-C accepts a [LLVMValueRef] for this so the node is cast to a
     * [Metadata.MetadataAsValue]. This requires a context. Set the context
     * which this should use with [withContext]
     *
     * @see LLVM.LLVMGetMDNodeNumOperands
     */
    public fun getOperandCount(
        withContext: Context = Context.getGlobalContext()
    ): Int {
        return LLVM.LLVMGetMDNodeNumOperands(toValue(withContext).ref)
    }

    /**
     * Get the operands from a metadata node
     *
     * LLVM-C accepts a [LLVMValueRef] for this so the node is cast to a
     * [Metadata.MetadataAsValue]. This requires a context. Set the context
     * which this should use with [withContext]
     *
     * @see LLVM.LLVMGetMDNodeOperands
     */
    public fun getOperands(
        withContext: Context = Context.getGlobalContext()
    ): List<Value> {
        val count = getOperandCount().toLong()
        val ptr = PointerPointer<LLVMValueRef>(count)

        LLVM.LLVMGetMDNodeOperands(
            toValue(withContext).ref,
            ptr
        )

        return ptr.map { Value(it) }
    }
}

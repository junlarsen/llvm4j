package dev.supergrecko.vexe.llvm.ir

import dev.supergrecko.vexe.llvm.internal.contracts.ContainsReference
import dev.supergrecko.vexe.llvm.internal.util.map
import org.bytedeco.javacpp.IntPointer
import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.llvm.LLVM.LLVMMetadataRef
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public class Metadata internal constructor() :
    ContainsReference<LLVMMetadataRef> {
    public override lateinit var ref: LLVMMetadataRef
        internal set

    public constructor(llvmRef: LLVMMetadataRef) : this() {
        ref = llvmRef
    }

    //region Core::Metadata
    /**
     * Create a string metadata node
     *
     * @see LLVM.LLVMMDStringInContext2
     */
    public constructor(
        string: String,
        context: Context = Context.getGlobalContext()
    ) : this() {
        ref = LLVM.LLVMMDStringInContext2(
            context.ref,
            string,
            string.length.toLong()
        )
    }

    /**
     * Create a metadata node
     *
     * @see LLVM.LLVMMDNodeInContext2
     */
    public constructor(
        operands: List<Metadata>,
        context: Context = Context.getGlobalContext()
    ) : this() {
        val ptr = PointerPointer(*operands.map { it.ref }.toTypedArray())
        ref = LLVM.LLVMMDNodeInContext2(
            context.ref,
            ptr,
            operands.size.toLong()
        )
    }

    /**
     * Create a metadata node from a value
     *
     * @see LLVM.LLVMValueAsMetadata
     */
    public constructor(value: Value) : this() {
        ref = LLVM.LLVMValueAsMetadata(value.ref)
    }

    /**
     * Get the current metadata as value
     *
     * @see LLVM.LLVMMetadataAsValue
     */
    public fun asValue(
        context: Context = Context.getGlobalContext()
    ): Value {
        val v = LLVM.LLVMMetadataAsValue(context.ref, ref)

        return Value(v)
    }

    /**
     * Get the string from a MDString node
     *
     * @see LLVM.LLVMGetMDString
     */
    public fun getString(
        context: Context = Context.getGlobalContext()
    ): String {
        val len = IntPointer(0)

        return LLVM.LLVMGetMDString(asValue(context).ref, len).string
    }

    /**
     * Get the amount of operands in a metadata node
     *
     * @see LLVM.LLVMGetMDNodeNumOperands
     */
    public fun getOperandCount(
        context: Context = Context.getGlobalContext()
    ): Int {
        return LLVM.LLVMGetMDNodeNumOperands(asValue(context).ref)
    }

    /**
     * Get the operands from a metadata node
     *
     * @see LLVM.LLVMGetMDNodeOperands
     */
    public fun getOperands(
        context: Context = Context.getGlobalContext()
    ): List<Value> {
        val count = getOperandCount().toLong()
        val ptr = PointerPointer<LLVMValueRef>(count)

        LLVM.LLVMGetMDNodeOperands(
            asValue(context).ref,
            ptr
        )

        return ptr.map { Value(it) }
    }

    public companion object {
        /**
         * Create a metadata node from a value
         *
         * @see LLVM.LLVMValueAsMetadata
         */
        public fun fromValue(value: Value): Metadata {
            return Metadata(value)
        }
    }
    //endregion Core::Metadata
}

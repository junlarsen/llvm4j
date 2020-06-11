package dev.supergrecko.vexe.llvm.ir.instructions.traits

import dev.supergrecko.vexe.llvm.internal.contracts.ContainsReference
import dev.supergrecko.vexe.llvm.internal.contracts.Unreachable
import dev.supergrecko.vexe.llvm.internal.util.map
import dev.supergrecko.vexe.llvm.internal.util.wrap
import dev.supergrecko.vexe.llvm.ir.Attribute
import dev.supergrecko.vexe.llvm.ir.AttributeIndex
import dev.supergrecko.vexe.llvm.ir.CallConvention
import dev.supergrecko.vexe.llvm.ir.Type
import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.llvm.LLVM.LLVMAttributeRef
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public interface CallBase : ContainsReference<LLVMValueRef> {
    //region Core::Instructions::CallSitesAndInvocations
    /**
     * Get the argument count for the call
     *
     * @see LLVM.LLVMGetNumArgOperands
     */
    public fun getArgumentCount(): Int {
        return LLVM.LLVMGetNumArgOperands(ref)
    }

    /**
     * Set the calling convention for the call
     *
     * TODO: Determine whether this is the enum to use, C API just states
     *   unsigned
     *
     * @see LLVM.LLVMSetInstructionCallConv
     */
    public fun setCallConvention(callConvention: CallConvention) {
        return LLVM.LLVMSetInstructionCallConv(ref, callConvention.value)
    }

    /**
     * Get the calling convention used for the call
     *
     * @see LLVM.LLVMSetInstructionCallConv
     */
    public fun getCallConvention(): CallConvention {
        val cc = LLVM.LLVMGetInstructionCallConv(ref)

        return CallConvention.values()
            .firstOrNull { it.value == cc }
            ?: throw Unreachable()
    }

    /**
     * Set the alignment for a parameter
     *
     * @see LLVM.LLVMSetInstrParamAlignment
     */
    public fun setParameterAlignment(parameterIndex: Int, alignment: Int) {
        LLVM.LLVMSetInstrParamAlignment(ref, parameterIndex, alignment)
    }

    /**
     * Get the function type of the function which this instruction invokes
     *
     * TODO: Test for proper return type
     *
     * @see LLVM.LLVMGetCalledFunctionType
     */
    public fun getCalledFunctionType(): Type {
        val ty = LLVM.LLVMGetCalledFunctionType(ref)

        return Type(ty)
    }

    /**
     * Add an attribute to the call site
     *
     * @see LLVM.LLVMAddCallSiteAttribute
     */
    public fun addAttribute(index: AttributeIndex, attribute: Attribute) {
        return addAttribute(index.value.toInt(), attribute)
    }

    /**
     * Add an attribute to the call site
     *
     * @see LLVM.LLVMAddCallSiteAttribute
     */
    public fun addAttribute(index: Int, attribute: Attribute) {
        LLVM.LLVMAddCallSiteAttribute(ref, index, attribute.ref)
    }

    /**
     * Get the attribute count at the call site
     *
     * @see LLVM.LLVMGetCallSiteAttributeCount
     */
    public fun getAttributeCount(index: AttributeIndex): Int {
        return getAttributeCount(index.value.toInt())
    }

    /**
     * Get the attribute count at the call site
     *
     * @see LLVM.LLVMGetCallSiteAttributeCount
     */
    public fun getAttributeCount(index: Int): Int {
        return LLVM.LLVMGetCallSiteAttributeCount(ref, index)
    }

    /**
     * Get all the attributes at the call site
     *
     * @see LLVM.LLVMGetCallSiteAttributes
     */
    public fun getAttributes(index: AttributeIndex): List<Attribute> {
        return getAttributes(index.value.toInt())
    }

    /**
     * Get all the attributes at the call site
     *
     * @see LLVM.LLVMGetCallSiteAttributes
     */
    public fun getAttributes(index: Int): List<Attribute> {
        val size = getAttributeCount(index).toLong()
        val ptr = PointerPointer<LLVMAttributeRef>(size)

        LLVM.LLVMGetCallSiteAttributes(ref, index, ptr)

        return ptr.map { Attribute(it) }
    }

    /**
     * Get a single enum attribute at the call site
     *
     * @see LLVM.LLVMGetCallSiteEnumAttribute
     */
    public fun getEnumAttribute(index: AttributeIndex, kind: Int): Attribute? {
        return getEnumAttribute(index.value.toInt(), kind)
    }

    /**
     * Get a single enum attribute at the call site
     *
     * @see LLVM.LLVMGetCallSiteEnumAttribute
     */
    public fun getEnumAttribute(index: Int, kind: Int): Attribute? {
        val attr = LLVM.LLVMGetCallSiteEnumAttribute(ref, index, kind)

        return wrap(attr) { Attribute(it) }
    }

    /**
     * Get a single string attribute at the call site
     *
     * @see LLVM.LLVMGetCallSiteStringAttribute
     */
    public fun getStringAttribute(
        index: AttributeIndex,
        kind: String
    ): Attribute? {
        return getStringAttribute(index.value.toInt(), kind)
    }

    /**
     * Get a single string attribute at the call site
     *
     * @see LLVM.LLVMGetCallSiteStringAttribute
     */
    public fun getStringAttribute(index: Int, kind: String): Attribute? {
        val strlen = kind.length
        val attr = LLVM.LLVMGetCallSiteStringAttribute(ref, index, kind, strlen)

        return wrap(attr) { Attribute(it) }
    }

    /**
     * Remove an enum attribute at the call site
     *
     * @see LLVM.LLVMRemoveCallSiteEnumAttribute
     */
    public fun removeEnumAttribute(index: AttributeIndex, kind: Int) {
        removeEnumAttribute(index.value.toInt(), kind)
    }

    /**
     * Remove an enum attribute at the call site
     *
     * @see LLVM.LLVMRemoveCallSiteEnumAttribute
     */
    public fun removeEnumAttribute(index: Int, kind: Int) {
        LLVM.LLVMRemoveCallSiteEnumAttribute(ref, index, kind)
    }

    /**
     * Remove a string attribute at the call site
     *
     * @see LLVM.LLVMRemoveCallSiteStringAttribute
     */
    public fun removeStringAttribute(index: AttributeIndex, kind: String) {
        removeStringAttribute(index.value.toInt(), kind)
    }

    /**
     * Remove a string attribute at the call site
     *
     * @see LLVM.LLVMRemoveCallSiteStringAttribute
     */
    public fun removeStringAttribute(index: Int, kind: String) {
        val strlen = kind.length

        LLVM.LLVMRemoveCallSiteStringAttribute(ref, index, kind, strlen)
    }
    //endregion Core::Instructions::CallSitesAndInvocations
}
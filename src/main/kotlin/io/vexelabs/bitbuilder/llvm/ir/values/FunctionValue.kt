package io.vexelabs.bitbuilder.llvm.ir.values

import io.vexelabs.bitbuilder.internal.fromLLVMBool
import io.vexelabs.bitbuilder.internal.map
import io.vexelabs.bitbuilder.llvm.internal.contracts.PointerIterator
import io.vexelabs.bitbuilder.llvm.ir.AttributeIndex
import io.vexelabs.bitbuilder.llvm.ir.BasicBlock
import io.vexelabs.bitbuilder.llvm.ir.CallConvention
import io.vexelabs.bitbuilder.llvm.ir.Context
import io.vexelabs.bitbuilder.llvm.ir.Value
import io.vexelabs.bitbuilder.llvm.ir.attributes.Attribute
import io.vexelabs.bitbuilder.llvm.ir.attributes.AttributeEnum
import io.vexelabs.bitbuilder.llvm.ir.attributes.AttributeString
import io.vexelabs.bitbuilder.llvm.ir.values.traits.DebugLocationValue
import io.vexelabs.bitbuilder.llvm.support.VerifierFailureAction
import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.llvm.LLVM.LLVMAttributeRef
import org.bytedeco.llvm.LLVM.LLVMBasicBlockRef
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public open class FunctionValue internal constructor() :
    Value(),
    DebugLocationValue {
    public constructor(llvmRef: LLVMValueRef) : this() {
        ref = llvmRef
    }

    /**
     * Add a new basic block to this function and returns it
     *
     * You may optionally pass a context which the block will reside in. If
     * no context is passed, the context of this function will be used.
     *
     * @see LLVM.LLVMAppendBasicBlock
     */
    public fun createBlock(
        name: String,
        context: Context = getContext()
    ): BasicBlock {
        val bb = LLVM.LLVMAppendBasicBlockInContext(
            context.ref,
            ref,
            name
        )

        return BasicBlock(bb)
    }

    /**
     * Append an already-existing basic block
     *
     * These blocks can be acquire via BasicBlock(Context, String)
     *
     * @see LLVM.LLVMAppendExistingBasicBlock
     */
    public fun appendBlock(block: BasicBlock) {
        LLVM.LLVMAppendExistingBasicBlock(ref, block.ref)
    }

    /**
     * Get the entry basic block for this function
     *
     * @see LLVM.LLVMGetEntryBasicBlock
     */
    public fun getEntryBlock(): BasicBlock? {
        val bb = LLVM.LLVMGetEntryBasicBlock(ref)

        return bb?.let { BasicBlock(it) }
    }

    /**
     * Get the start of the basic block iterator
     *
     * @see PointerIterator
     */
    public fun getBlockIterator(): BasicBlock.Iterator? {
        val bb = LLVM.LLVMGetFirstBasicBlock(ref)

        return bb?.let { BasicBlock.Iterator(it) }
    }

    /**
     * Get how many blocks reside inside this function
     *
     * @see LLVM.LLVMCountBasicBlocks
     */
    public fun getBlockCount(): Int {
        return LLVM.LLVMCountBasicBlocks(ref)
    }

    /**
     * Get all the blocks in this function
     *
     * @see LLVM.LLVMGetBasicBlocks
     */
    public fun getBlocks(): List<BasicBlock> {
        // Allocate a pointer with size of block count
        val ptr = PointerPointer<LLVMBasicBlockRef>(getBlockCount().toLong())

        LLVM.LLVMGetBasicBlocks(ref, ptr)

        return ptr.map { BasicBlock(it) }.also {
            ptr.deallocate()
        }
    }

    /**
     * Get a parameter from this function at [index]
     *
     * @see LLVM.LLVMGetParam
     */
    public fun getParameter(index: Int): Value {
        require(index < getParameterCount())

        val value = LLVM.LLVMGetParam(ref, index)

        return Value(value)
    }

    /**
     * Get the amount of parameters this function expects
     *
     * @see LLVM.LLVMCountParams
     */
    public fun getParameterCount(): Int {
        return LLVM.LLVMCountParams(ref)
    }

    /**
     * Get all parameters from this function
     *
     * @see LLVM.LLVMGetParams
     */
    public fun getParameters(): List<Value> {
        val ptr = PointerPointer<LLVMValueRef>(getParameterCount().toLong())

        LLVM.LLVMGetParams(ref, ptr)

        return ptr.map { Value(it) }.also {
            ptr.deallocate()
        }
    }

    /**
     * Set the parameter alignment for parameter [value]
     *
     * @see LLVM.LLVMSetParamAlignment
     */
    public fun setParameterAlignment(value: Value, align: Int) {
        LLVM.LLVMSetParamAlignment(value.ref, align)
    }

    /**
     * Get the indirect resolver if it has been set
     *
     * @see LLVM.LLVMGetGlobalIFuncResolver
     */
    public fun getIndirectResolver(): IndirectFunction? {
        val resolver = LLVM.LLVMGetGlobalIFuncResolver(ref)

        return resolver?.let { IndirectFunction(it) }
    }

    /**
     * Set the indirect resolver
     *
     * @see LLVM.LLVMSetGlobalIFuncResolver
     */
    public fun setIndirectResolver(function: IndirectFunction) {
        LLVM.LLVMSetGlobalIFuncResolver(ref, function.ref)
    }

    /**
     * Get the calling convention for this function
     *
     * @see LLVM.LLVMGetFunctionCallConv
     */
    public fun getCallConvention(): CallConvention {
        val cc = LLVM.LLVMGetFunctionCallConv(ref)

        return CallConvention[cc]
    }

    /**
     * Set the calling convention for this function
     *
     * @see LLVM.LLVMSetFunctionCallConv
     */
    public fun setCallConvention(convention: CallConvention) {
        LLVM.LLVMSetFunctionCallConv(ref, convention.value)
    }

    /**
     * Get the personality function for this function
     *
     * @see LLVM.LLVMGetPersonalityFn
     */
    public fun getPersonalityFunction(): FunctionValue {
        require(hasPersonalityFunction()) {
            "This function does not have a personality function"
        }

        val fn = LLVM.LLVMGetPersonalityFn(ref)

        return FunctionValue(fn)
    }

    /**
     * Set the personality function for this function
     *
     * @see LLVM.LLVMSetPersonalityFn
     */
    public fun setPersonalityFunction(function: FunctionValue) {
        LLVM.LLVMSetPersonalityFn(ref, function.ref)
    }

    /**
     * Get the GC name for this function
     *
     * @see LLVM.LLVMGetGC
     */
    public fun getGarbageCollector(): String {
        return LLVM.LLVMGetGC(ref).string
    }

    /**
     * Set the GC name for this function
     *
     * @see LLVM.LLVMSetGC
     */
    public fun setGarbageCollector(collector: String) {
        LLVM.LLVMSetGC(ref, collector)
    }

    /**
     * Determine if this function has a personality function
     *
     * @see LLVM.LLVMHasPersonalityFn
     */
    public fun hasPersonalityFunction(): Boolean {
        return LLVM.LLVMHasPersonalityFn(ref).fromLLVMBool()
    }

    /**
     * Delete this function from its parent
     *
     * @see LLVM.LLVMDeleteFunction
     */
    public open fun delete() {
        LLVM.LLVMDeleteFunction(ref)
    }

    /**
     * If this function is an intrinsic, get its id
     *
     * @see LLVM.LLVMGetIntrinsicID
     */
    public fun getIntrinsicId(): Int {
        return LLVM.LLVMGetIntrinsicID(ref)
    }

    /**
     * Add an attribute at an [index]
     *
     * Overload for passing an [AttributeIndex] instead of an integer
     *
     * @see LLVM.LLVMAddAttributeAtIndex
     */
    public fun addAttribute(index: AttributeIndex, attribute: Attribute) {
        addAttribute(index.value.toInt(), attribute)
    }

    /**
     * Add an attribute at an [index]
     *
     * @see LLVM.LLVMAddAttributeAtIndex
     */
    public fun addAttribute(index: Int, attribute: Attribute) {
        LLVM.LLVMAddAttributeAtIndex(ref, index, attribute.ref)
    }

    /**
     * Get the amount of attributes at an [index]
     *
     * Overload for passing an [AttributeIndex] instead of an integer
     *
     * @see LLVM.LLVMGetAttributeCountAtIndex
     */
    public fun getAttributeCount(index: AttributeIndex): Int {
        return getAttributeCount(index.value.toInt())
    }

    /**
     * Get the amount of attributes at an [index]
     *
     * @see LLVM.LLVMGetAttributeCountAtIndex
     */
    public fun getAttributeCount(index: Int): Int {
        return LLVM.LLVMGetAttributeCountAtIndex(ref, index)
    }

    /**
     * Get all attributes at an [index] for the function
     *
     * @see LLVM.LLVMGetAttributesAtIndex
     */
    public fun getAttributes(index: AttributeIndex): List<Attribute> {
        val ptr = PointerPointer<LLVMAttributeRef>(
            getAttributeCount(index).toLong()
        )

        LLVM.LLVMGetAttributesAtIndex(ref, index.value.toInt(), ptr)

        return ptr.map { Attribute.create(it) }.also {
            ptr.deallocate()
        }
    }

    /**
     * Pull the attribute value from an [index] with a [kind]
     *
     * Overload for passing an [AttributeIndex] instead of an integer
     *
     * @see LLVM.LLVMGetEnumAttributeAtIndex
     */
    public fun getEnumAttribute(
        index: AttributeIndex,
        kind: Int
    ): AttributeEnum? = getEnumAttribute(index.value.toInt(), kind)

    /**
     * Pull the attribute value from an [index] with a [kind]
     *
     * @see LLVM.LLVMGetEnumAttributeAtIndex
     */
    public fun getEnumAttribute(
        index: Int,
        kind: Int
    ): AttributeEnum? {
        val ref = LLVM.LLVMGetEnumAttributeAtIndex(
            ref,
            index,
            kind
        )

        return ref?.let { AttributeEnum(it) }
    }

    /**
     * Pull the attribute value from an [index] with a [kind]
     *
     * Overload for passing an [AttributeIndex] instead of an integer
     *
     * @see LLVM.LLVMGetStringAttributeAtIndex
     */
    public fun getStringAttribute(
        index: AttributeIndex,
        kind: String
    ): AttributeString? = getStringAttribute(index.value.toInt(), kind)

    /**
     * Pull the attribute value from an [index] with a [kind]
     *
     * @see LLVM.LLVMGetStringAttributeAtIndex
     */
    public fun getStringAttribute(
        index: Int,
        kind: String
    ): AttributeString? {
        val ref = LLVM.LLVMGetStringAttributeAtIndex(
            ref,
            index,
            kind,
            kind.length
        )

        return ref?.let { AttributeString(it) }
    }

    /**
     * Removes an attribute at the given index
     *
     * Overload for passing an [AttributeIndex] instead of an integer
     *
     * @see LLVM.LLVMRemoveEnumAttributeAtIndex
     */
    public fun removeEnumAttribute(
        index: AttributeIndex,
        kind: Int
    ): Unit = removeEnumAttribute(index.value.toInt(), kind)

    /**
     * Removes an attribute at the given index
     *
     * @see LLVM.LLVMRemoveEnumAttributeAtIndex
     */
    public fun removeEnumAttribute(
        index: Int,
        kind: Int
    ): Unit = LLVM.LLVMRemoveEnumAttributeAtIndex(ref, index, kind)

    /**
     * Removes an attribute at the given index
     *
     * Overload for passing an [AttributeIndex] instead of an integer
     *
     * @see LLVM.LLVMRemoveStringAttributeAtIndex
     */
    public fun removeStringAttribute(
        index: AttributeIndex,
        kind: String
    ): Unit = removeStringAttribute(index.value.toInt(), kind)

    /**
     * Removes an attribute at the given index
     *
     * @see LLVM.LLVMRemoveStringAttributeAtIndex
     */
    public fun removeStringAttribute(
        index: Int,
        kind: String
    ): Unit = LLVM.LLVMRemoveStringAttributeAtIndex(
        ref,
        index,
        kind,
        kind.length
    )

    /**
     * TODO: Find out what this does
     *
     * @see LLVM.LLVMAddTargetDependentFunctionAttr
     */
    public

    fun addTargetDependentAttribute(attribute: String, value: String) {
        LLVM.LLVMAddTargetDependentFunctionAttr(ref, attribute, value)
    }

    /**
     * Verify that the function structure is valid
     *
     * As opposed to the LLVM implementation, this returns true if the function
     * is valid.
     *
     * @see LLVM.LLVMVerifyFunction
     */
    public fun verify(action: VerifierFailureAction): Boolean {
        val result = LLVM.LLVMVerifyFunction(ref, action.value)

        // 0 on success, invert
        return !result.fromLLVMBool()
    }

    /**
     * View the function structure
     *
     * From the LLVM Source:
     *
     * This function is meant for use from the debugger. You can just say
     * 'call F->viewCFG()' and a ghost view window should pop up from the
     * program, displaying the CFG of the current function. This depends on
     * there being a 'dot' and 'gv' program in your path.
     *
     * If [hideBasicBlocks] is true then [LLVM.LLVMViewFunctionCFGOnly] will be
     * used instead of [LLVM.LLVMViewFunctionCFG]
     *
     * TODO: Does this even work via JNI??
     *
     * @see LLVM.LLVMViewFunctionCFG
     */
    public fun viewConfiguration(hideBasicBlocks: Boolean) {
        if (hideBasicBlocks) {
            LLVM.LLVMViewFunctionCFGOnly(ref)
        } else {
            LLVM.LLVMViewFunctionCFG(ref)
        }
    }

    /**
     * Class to perform iteration over functions
     *
     * @see [PointerIterator]
     */
    public class Iterator(ref: LLVMValueRef) :
        PointerIterator<FunctionValue, LLVMValueRef>(
            start = ref,
            yieldNext = { LLVM.LLVMGetNextFunction(it) },
            apply = { FunctionValue(it) }
        )
}

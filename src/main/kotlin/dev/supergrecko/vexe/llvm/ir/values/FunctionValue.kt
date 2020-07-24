package dev.supergrecko.vexe.llvm.ir.values

import dev.supergrecko.vexe.llvm.internal.contracts.PointerIterator
import dev.supergrecko.vexe.llvm.internal.contracts.Unreachable
import dev.supergrecko.vexe.llvm.internal.util.fromLLVMBool
import dev.supergrecko.vexe.llvm.internal.util.map
import dev.supergrecko.vexe.llvm.ir.Attribute
import dev.supergrecko.vexe.llvm.ir.AttributeIndex
import dev.supergrecko.vexe.llvm.ir.BasicBlock
import dev.supergrecko.vexe.llvm.ir.CallConvention
import dev.supergrecko.vexe.llvm.ir.Context
import dev.supergrecko.vexe.llvm.ir.Module
import dev.supergrecko.vexe.llvm.ir.NamedMetadataNode
import dev.supergrecko.vexe.llvm.ir.Value
import dev.supergrecko.vexe.llvm.ir.types.FunctionType
import dev.supergrecko.vexe.llvm.ir.values.traits.DebugLocationValue
import dev.supergrecko.vexe.llvm.support.VerifierFailureAction
import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.llvm.LLVM.LLVMAttributeRef
import org.bytedeco.llvm.LLVM.LLVMBasicBlockRef
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public open class FunctionValue internal constructor() : Value(),
    DebugLocationValue {
    public constructor(llvmRef: LLVMValueRef) : this() {
        ref = llvmRef
    }

    //region Core::BasicBlock
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
     * @see LLVM.LLVMGetFirstBasicBlock
     */
    public fun getFirstBlock(): BasicBlock.Iterator? {
        val bb = LLVM.LLVMGetFirstBasicBlock(ref)

        return bb?.let { BasicBlock.Iterator(it) }
    }

    /**
     * Get the end of the basic block iterator
     *
     * @see LLVM.LLVMGetLastBasicBlock
     */
    public fun getLastBlock(): BasicBlock.Iterator? {
        val bb = LLVM.LLVMGetLastBasicBlock(ref)

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

        return ptr.map { BasicBlock(it) }
    }
    //endregion Core::BasicBlock

    //region Core::Values::Constants::FunctionValues::FunctionParameters
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

        return ptr.map { Value(it) }
    }

    /**
     * Set the parameter alignment for parameter [value]
     */
    public fun setParameterAlignment(value: Value, align: Int) {
        LLVM.LLVMSetParamAlignment(value.ref, align)
    }
    //endregion Core::Values::Constants::FunctionValues::FunctionParameters

    //region Core::Values::Constants::FunctionValues::IndirectFunctions
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
     * Make this indirect function global in the given [module]
     *
     * @see LLVM.LLVMAddGlobalIFunc
     */
    public fun makeGlobal(
        module: Module,
        name: String,
        type: FunctionType,
        addressSpace: Int,
        resolver: FunctionValue
    ): IndirectFunction {
        val indirect = LLVM.LLVMAddGlobalIFunc(
            module.ref,
            name,
            name.length.toLong(),
            type.ref,
            addressSpace,
            resolver.ref
        )

        return IndirectFunction(indirect)
    }
    //endregion Core::Values::Constants::FunctionValues::IndirectFunctions

    //region Core::Values::Constants::FunctionValues
    /**
     * Get the calling convention for this function
     *
     * @see LLVM.LLVMGetFunctionCallConv
     */
    public fun getCallConvention(): CallConvention {
        val cc = LLVM.LLVMGetFunctionCallConv(ref)

        return CallConvention.values()
            .firstOrNull { it.value == cc }
            ?: throw Unreachable()
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
     * @see LLVM.LLVMGetAttributeCountAtIndex
     */
    public fun getAttributeCount(index: AttributeIndex): Int {
        return LLVM.LLVMGetAttributeCountAtIndex(ref, index.value.toInt())
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

        return ptr.map { Attribute(it) }
    }

    /**
     * Pull the attribute value from an [index] with a [kind]
     *
     * @see LLVM.LLVMGetEnumAttributeAtIndex
     */
    public fun getAttribute(
        index: AttributeIndex,
        kind: Int
    ): Attribute {
        val ref = LLVM.LLVMGetEnumAttributeAtIndex(
            ref, index.value.toInt(), kind
        )

        return Attribute(ref)
    }

    /**
     * Pull the attribute value from an [index] with a [kind]
     *
     * @see LLVM.LLVMGetStringAttributeAtIndex
     */
    public fun getAttribute(
        index: AttributeIndex,
        kind: String
    ): Attribute {
        val ref = LLVM.LLVMGetStringAttributeAtIndex(
            ref, index.value.toInt(), kind, kind.length
        )

        return Attribute(ref)
    }

    /**
     * Removes an attribute at the given index
     *
     * @see LLVM.LLVMRemoveEnumAttributeAtIndex
     */
    public fun removeAttribute(
        index: AttributeIndex,
        kind: Int
    ) {
        LLVM.LLVMRemoveEnumAttributeAtIndex(
            ref, index.value.toInt(), kind
        )
    }

    /**
     * Removes an attribute at the given index
     *
     * @see LLVM.LLVMRemoveStringAttributeAtIndex
     */
    public fun removeAttribute(
        index: AttributeIndex,
        kind: String
    ) {
        LLVM.LLVMRemoveStringAttributeAtIndex(
            ref, index.value.toInt(), kind, kind.length
        )
    }

    /**
     * @see LLVM.LLVMAddTargetDependentFunctionAttr
     */
    public fun addTargetDependentAttribute(
        attribute: String,
        value: String
    ) {
        LLVM.LLVMAddTargetDependentFunctionAttr(ref, attribute, value)
    }
    //endregion Core::Values::Constants::FunctionValues

    //region Analysis
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
    //endregion Analysis

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

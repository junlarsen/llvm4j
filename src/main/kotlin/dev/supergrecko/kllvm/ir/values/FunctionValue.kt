package dev.supergrecko.kllvm.ir.values

import arrow.core.Option
import dev.supergrecko.kllvm.internal.contracts.Unreachable
import dev.supergrecko.kllvm.internal.util.fromLLVMBool
import dev.supergrecko.kllvm.internal.util.map
import dev.supergrecko.kllvm.internal.util.wrap
import dev.supergrecko.kllvm.ir.Attribute
import dev.supergrecko.kllvm.ir.AttributeIndex
import dev.supergrecko.kllvm.ir.BasicBlock
import dev.supergrecko.kllvm.ir.CallConvention
import dev.supergrecko.kllvm.ir.Context
import dev.supergrecko.kllvm.ir.Module
import dev.supergrecko.kllvm.ir.Value
import dev.supergrecko.kllvm.ir.types.FunctionType
import dev.supergrecko.kllvm.support.VerifierFailureAction
import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.llvm.LLVM.LLVMAttributeRef
import org.bytedeco.llvm.LLVM.LLVMBasicBlockRef
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public open class FunctionValue internal constructor() : Value() {
    // TODO: Test entire unit
    //   It is currently not possible to test this unit as there is currently
    //   no way to create a FunctionValue. - grecko 21.04.2020
    /**
     * Construct a new Type from an LLVM pointer reference
     */
    public constructor(llvmValue: LLVMValueRef) : this() {
        ref = llvmValue
    }

    //region Core::BasicBlock
    /**
     * Adds a new basic block to this function and returns it
     *
     * You may optionally pass a context which the block will reside in. If
     * no context is passed, the context of this function will be used.
     *
     * @see LLVM.LLVMAppendBasicBlock
     */
    public fun addBlock(
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
     * Get the entry basic block for this function
     *
     * @see LLVM.LLVMGetEntryBasicBlock
     */
    public fun getEntryBlock(): Option<BasicBlock> {
        val bb = LLVM.LLVMGetEntryBasicBlock(ref)

        return wrap(bb) { BasicBlock(it) }
    }

    /**
     * Get the first basic block inside this function.
     *
     * Use [BasicBlock.getNextBlock] to advance this iterator on the returned
     * basic block instance.
     *
     * @see LLVM.LLVMGetFirstBasicBlock
     */
    public fun getFirstBlock(): Option<BasicBlock> {
        val bb = LLVM.LLVMGetFirstBasicBlock(ref)

        return wrap(bb) { BasicBlock(it) }
    }

    /**
     * Get the last basic block inside this function.
     *
     * Use [BasicBlock.getPreviousBlock] to advance this iterator on the
     * returned basic block instance.
     *
     * @see LLVM.LLVMGetLastBasicBlock
     */
    public fun getLastBlock(): Option<BasicBlock> {
        val bb = LLVM.LLVMGetLastBasicBlock(ref)

        return wrap(bb) { BasicBlock(it) }
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
    public fun getIndirectResolver(): Option<IndirectFunction> {
        val resolver = LLVM.LLVMGetGlobalIFuncResolver(ref)

        return wrap(resolver) { IndirectFunction(it) }
    }

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
    public fun getCallConvention(): CallConvention {
        val cc = LLVM.LLVMGetFunctionCallConv(ref)

        return CallConvention.values()
            .firstOrNull { it.value == cc }
            ?: throw Unreachable()
    }

    public fun setCallConvention(convention: CallConvention) {
        LLVM.LLVMSetFunctionCallConv(ref, convention.value)
    }

    public fun getPersonalityFunction(): FunctionValue {
        require(hasPersonalityFunction()) {
            "This function does not have a personality function"
        }

        val fn = LLVM.LLVMGetPersonalityFn(ref)

        return FunctionValue(fn)
    }

    public fun setPersonalityFunction(function: FunctionValue) {
        LLVM.LLVMSetPersonalityFn(ref, function.ref)
    }

    public fun getGarbageCollector(): String {
        return LLVM.LLVMGetGC(ref).string
    }

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
     */
    public fun verify(action: VerifierFailureAction): Boolean {
        // LLVM Source says:
        // > Note that this function's return value is inverted from what you
        // > would expect of a function called "verify".
        // Thus we invert it again ...
        return !LLVM.LLVMVerifyFunction(ref, action.value).fromLLVMBool()
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
     */
    public fun viewConfiguration(hideBasicBlocks: Boolean) {
        if (hideBasicBlocks) {
            LLVM.LLVMViewFunctionCFGOnly(ref)
        } else {
            LLVM.LLVMViewFunctionCFG(ref)
        }
    }
    //endregion Analysis
}

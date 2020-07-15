package dev.supergrecko.vexe.llvm.ir

import dev.supergrecko.vexe.llvm.internal.contracts.ContainsReference
import dev.supergrecko.vexe.llvm.internal.contracts.Unreachable
import dev.supergrecko.vexe.llvm.internal.util.fromLLVMBool
import dev.supergrecko.vexe.llvm.support.Message
import org.bytedeco.javacpp.SizeTPointer
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public open class Value internal constructor() :
    ContainsReference<LLVMValueRef> {
    public final override lateinit var ref: LLVMValueRef
        internal set

    /**
     * Construct a new Type from an LLVM pointer reference
     */
    public constructor(llvmRef: LLVMValueRef) : this() {
        ref = llvmRef
    }

    //region Core::Values::GeneralAPIs
    /**
     * Get the IR name for this value
     *
     * @see LLVM.LLVMGetValueName2
     */
    public fun getName(): String {
        val len = SizeTPointer(0)
        val ptr = LLVM.LLVMGetValueName2(ref, len)

        return ptr.string
    }

    /**
     * Set the IR name for this value
     *
     * This does not work on constants. Ensure the context of this value does
     * not discard value names.
     *
     * @see LLVM.LLVMSetValueName2
     */
    public fun setName(name: String) {
        LLVM.LLVMSetValueName2(ref, name, name.length.toLong())
    }

    /**
     * Get the type of this value
     *
     * @see LLVM.LLVMTypeOf
     */
    public open fun getType(): Type {
        val type = LLVM.LLVMTypeOf(ref)

        return Type(type)
    }

    /**
     * Determine whether this value is undefined or not
     *
     * @see LLVM.LLVMIsUndef
     */
    public fun isUndef(): Boolean {
        return LLVM.LLVMIsUndef(ref).fromLLVMBool()
    }

    /**
     * Determine if this value is constant
     *
     * @see LLVM.LLVMIsConstant
     */
    public fun isConstant(): Boolean {
        return LLVM.LLVMIsConstant(ref).fromLLVMBool()
    }

    /**
     * Get the value kind of this value
     *
     * @see LLVM.LLVMGetValueKind
     */
    public fun getValueKind(): ValueKind {
        val kind = LLVM.LLVMGetValueKind(ref)

        return ValueKind.values()
            .firstOrNull { it.value == kind }
            ?: throw Unreachable()
    }

    /**
     * All values hold a context through their type
     *
     * Fetches the context this value was created in.
     *
     * @see LLVM.LLVMGetTypeContext
     */
    public fun getContext(): Context {
        return getType().getContext()
    }

    /**
     * Dump the string representation of this value to stderr
     *
     * @see LLVM.LLVMDumpValue
     */
    public fun dump() {
        LLVM.LLVMDumpValue(ref)
    }

    /**
     * Get the LLVM IR for this value
     *
     * This IR must be disposed via [IR.dispose] otherwise memory will
     * be leaked.
     *
     * @see LLVM.LLVMPrintValueToString
     */
    public fun getIR(): IR {
        val ptr = LLVM.LLVMPrintValueToString(ref)

        return IR(ptr)
    }

    /**
     * Replaces all usages of this value with [value]
     *
     * TODO: Move this, presumably to Uses?
     *
     * @see LLVM.LLVMReplaceAllUsesWith
     */
    public fun replaceAllUsesWith(value: Value) {
        LLVM.LLVMReplaceAllUsesWith(ref, value.ref)
    }

    /**
     * Is this value a metadata node?
     *
     * @see LLVM.LLVMIsAMDNode
     */
    public fun isMetadataNode(): Boolean {
        return LLVM.LLVMIsAMDNode(ref) != null
    }

    /**
     * Is this value a metadata string?
     *
     * @see LLVM.LLVMIsAMDString
     */
    public fun isMetadataString(): Boolean {
        return LLVM.LLVMIsAMDString(ref) != null
    }
    //endregion Core::Values::GeneralAPIs

    //region Core::Values::Usage
    /**
     * Get the first [Use] for this value
     *
     * Move the iterator with [Use.getNextUse]
     *
     * @see LLVM.LLVMGetFirstUse
     */
    public fun getFirstUse(): Use? {
        val use = LLVM.LLVMGetFirstUse(ref)

        return use?.let { Use(it) }
    }
    //endregion Core::Values::Usage

    //region Core::Values::Constants
    /**
     * Determine if this value is a null pointer
     *
     * @see LLVM.LLVMIsNull
     */
    public fun isNull(): Boolean {
        return LLVM.LLVMIsNull(ref).fromLLVMBool()
    }
    //endregion Core::Values::Constants

    //region Core::BasicBlock
    /**
     * Is this value a basic block?
     *
     * @see LLVM.LLVMIsABasicBlock
     */
    public fun isBasicBlock(): Boolean {
        return LLVM.LLVMIsABasicBlock(ref) != null
    }

    /**
     * Converts this value into a Basic Block
     *
     * This is done by unwrapping the instance into a BasicBlock
     *
     * TODO: Research more about this cast
     *
     * @see LLVM.LLVMValueAsBasicBlock
     */
    public fun toBasicBlock(): BasicBlock {
        val bb = LLVM.LLVMValueAsBasicBlock(ref)

        return BasicBlock(bb)
    }
    //endregion Core::BasicBlock
}

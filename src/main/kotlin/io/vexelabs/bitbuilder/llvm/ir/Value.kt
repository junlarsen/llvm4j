package io.vexelabs.bitbuilder.llvm.ir

import io.vexelabs.bitbuilder.llvm.internal.contracts.ContainsReference
import io.vexelabs.bitbuilder.llvm.internal.util.fromLLVMBool
import io.vexelabs.bitbuilder.llvm.ir.values.FunctionValue
import io.vexelabs.bitbuilder.raii.resourceScope
import io.vexelabs.bitbuilder.raii.toResource
import org.bytedeco.javacpp.SizeTPointer
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

/**
 * Interface to llvm::Value
 *
 * This is a very important LLVM class. It is the base class of all values
 * computed by a program that may be used as operands to other values. Value
 * is the super class of other important classes such as Instruction and
 * Function.
 *
 * - All Values have a [Type].
 * - Type is not a subclass of Value.
 * - Some values can have a name and they belong to some [Module].
 * - Setting the name on the Value automatically updates the module's symbol
 *   table.
 *
 * @see Type
 * @see Instruction
 * @see FunctionValue
 * @see Module
 *
 * Every value has a "use list" that keeps track of which other Values are
 * using this Value.
 *
 * @see Use
 * @see User
 *
 * @see LLVMValueRef
 */
public open class Value internal constructor() :
    ContainsReference<LLVMValueRef> {
    public final override lateinit var ref: LLVMValueRef
        internal set

    public constructor(llvmRef: LLVMValueRef) : this() {
        ref = llvmRef
    }

    /**
     * Get the IR name for this value
     *
     * @see LLVM.LLVMGetValueName2
     */
    public fun getName(): String {
        val len = SizeTPointer(1).toResource { it.deallocate() }

        return resourceScope(len) {
            val ptr = LLVM.LLVMGetValueName2(ref, it)
            val contents = ptr.string

            ptr.deallocate()

            return@resourceScope contents
        }
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

        return ValueKind[kind]
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

    /**
     * Get the start of the use iterator
     *
     * @see LLVM.LLVMGetFirstUse
     */
    public fun getFirstUse(): Use.Iterator? {
        val use = LLVM.LLVMGetFirstUse(ref)

        return use?.let { Use.Iterator(it) }
    }

    /**
     * Determine if this value is a null pointer
     *
     * @see LLVM.LLVMIsNull
     */
    public fun isNull(): Boolean {
        return LLVM.LLVMIsNull(ref).fromLLVMBool()
    }

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
     * @see LLVM.LLVMValueAsBasicBlock
     */
    public fun toBasicBlock(): BasicBlock {
        require(isBasicBlock()) {
            "This value is not a basic block"
        }

        val bb = LLVM.LLVMValueAsBasicBlock(ref)

        return BasicBlock(bb)
    }
}

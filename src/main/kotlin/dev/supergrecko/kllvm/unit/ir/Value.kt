package dev.supergrecko.kllvm.unit.ir

import dev.supergrecko.kllvm.unit.internal.contracts.ContainsReference
import dev.supergrecko.kllvm.unit.internal.contracts.OrderedEnum
import dev.supergrecko.kllvm.unit.internal.contracts.Unreachable
import dev.supergrecko.kllvm.unit.internal.util.fromLLVMBool
import dev.supergrecko.kllvm.unit.internal.util.wrap
import dev.supergrecko.kllvm.unit.ir.instructions.Instruction
import dev.supergrecko.kllvm.unit.ir.values.FunctionValue
import dev.supergrecko.kllvm.unit.ir.values.GenericValue
import dev.supergrecko.kllvm.unit.ir.values.GlobalValue
import dev.supergrecko.kllvm.unit.ir.values.MetadataValue
import dev.supergrecko.kllvm.unit.ir.values.PhiValue
import dev.supergrecko.kllvm.unit.ir.values.constants.ConstantArray
import dev.supergrecko.kllvm.unit.ir.values.constants.ConstantFloat
import dev.supergrecko.kllvm.unit.ir.values.constants.ConstantInt
import dev.supergrecko.kllvm.unit.ir.values.constants.ConstantPointer
import dev.supergrecko.kllvm.unit.ir.values.constants.ConstantStruct
import dev.supergrecko.kllvm.unit.ir.values.constants.ConstantVector
import org.bytedeco.javacpp.SizeTPointer
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

/**
 * Support types matching LLVMValueKind
 *
 * [Documentation](https://llvm.org/doxygen/group__LLVMCCoreTypes.html)
 */
public enum class ValueKind(public override val value: Int) : OrderedEnum<Int> {
    Argument(LLVM.LLVMArgumentValueKind),
    BasicBlock(LLVM.LLVMBasicBlockValueKind),
    MemoryUse(LLVM.LLVMMemoryUseValueKind),
    MemoryDef(LLVM.LLVMMemoryDefValueKind),
    MemoryPhi(LLVM.LLVMMemoryPhiValueKind),
    Function(LLVM.LLVMFunctionValueKind),
    GlobalAlias(LLVM.LLVMGlobalAliasValueKind),
    GlobalIFunc(LLVM.LLVMGlobalIFuncValueKind),
    GlobalVariable(LLVM.LLVMGlobalVariableValueKind),
    BlockAddress(LLVM.LLVMBlockAddressValueKind),
    ConstantExpr(LLVM.LLVMConstantExprValueKind),
    ConstantArray(LLVM.LLVMConstantArrayValueKind),
    ConstantStruct(LLVM.LLVMConstantStructValueKind),
    ConstantVector(LLVM.LLVMConstantVectorValueKind),
    UndefValue(LLVM.LLVMUndefValueValueKind),
    ConstantAggregateZero(LLVM.LLVMConstantAggregateZeroValueKind),
    ConstantDataArray(LLVM.LLVMConstantDataArrayValueKind),
    ConstantDataVector(LLVM.LLVMConstantDataVectorValueKind),
    ConstantInt(LLVM.LLVMConstantIntValueKind),
    ConstantFP(LLVM.LLVMConstantFPValueKind),
    ConstantPointerNull(LLVM.LLVMConstantPointerNullValueKind),
    ConstantTokenNone(LLVM.LLVMConstantTokenNoneValueKind),
    MetadataAsValue(LLVM.LLVMMetadataAsValueValueKind),
    InlineAsm(LLVM.LLVMInlineAsmValueKind),
    Instruction(LLVM.LLVMInstructionValueKind)
}

/**
 * Base class mirroring llvm::Value
 */
public open class Value internal constructor() :
    ContainsReference<LLVMValueRef> {
    public final override lateinit var ref: LLVMValueRef
        internal set

    /**
     * Construct a new Type from an LLVM pointer reference
     */
    public constructor(value: LLVMValueRef) : this() {
        ref = value
    }

    //region Core::Values::GeneralAPIs
    /**
     * Get the IR name for this value
     *
     * @see LLVM.LLVMGetValueName2
     */
    public fun getName(): String {
        val ptr = LLVM.LLVMGetValueName2(ref, SizeTPointer(0))

        return ptr.string
    }

    /**
     * Set the IR name for this value
     *
     * If this yields unexpected results, see the source code for this file
     *
     * LLVM-C does not provide a way for us to check if a value has a
     * name or not and thus we cannot implement all the checks LLVM-C++
     * does in their source file which means this may yield unwanted or
     * unexpected results.
     *
     * https://llvm.org/doxygen/Value_8cpp_source.html#l00223
     *
     * TODO: Research if there is any other way we can determine the above
     * TODO: Test when viable solution has been found
     */
    public fun setName(name: String) {
        require(!(getContext().isDiscardingValueNames() && this !is GlobalValue))

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
     */
    public fun getContext(): Context = getType().getContext()

    /**
     * Dump the string representation of this value to stderr
     *
     * @see LLVM.LLVMDumpValue
     */
    public fun dump() {
        LLVM.LLVMDumpValue(ref)
    }

    /**
     * Get the value in a string format
     *
     * @see LLVM.LLVMPrintValueToString
     */
    public fun dumpToString(): String {
        val ptr = LLVM.LLVMPrintValueToString(ref)

        return ptr.string
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
     * Get the first use for this value, the next value can be retrieved by
     * calling [Use.nextUse] on the returned value.
     *
     * @see LLVM.LLVMGetFirstUse
     */
    public fun getFirstUse(): Use? {
        val use = LLVM.LLVMGetFirstUse(ref)

        return wrap(use) { Use(it) }
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

    //region Typecasting
    /**
     * Attempts to use the current [ref] for a new value
     *
     * TODO: Do something about these
     */
    public fun asArrayValue() = ConstantArray(ref)
    public fun asFloatValue() = ConstantFloat(ref)
    public fun asFunctionValue() = FunctionValue(ref)
    public fun asGenericValue() = GenericValue(ref)
    public fun asGlobalValue() = GlobalValue(ref)
    public fun asInstructionValue() = Instruction(ref)
    public fun asIntValue() = ConstantInt(ref)
    public fun asMetadataValue() = MetadataValue(ref)
    public fun asPhiValue() = PhiValue(ref)
    public fun asPointerValue() = ConstantPointer(ref)
    public fun asStructValue() = ConstantStruct(ref)
    public fun asVectorValue() = ConstantVector(ref)
    //endregion Typecasting
}

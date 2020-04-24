package dev.supergrecko.kllvm.ir

import dev.supergrecko.kllvm.internal.contracts.ContainsReference
import dev.supergrecko.kllvm.internal.contracts.OrderedEnum
import dev.supergrecko.kllvm.internal.contracts.Unreachable
import dev.supergrecko.kllvm.internal.util.fromLLVMBool
import dev.supergrecko.kllvm.ir.instructions.Instruction
import dev.supergrecko.kllvm.ir.instructions.Opcode
import dev.supergrecko.kllvm.ir.types.PointerType
import dev.supergrecko.kllvm.ir.values.FunctionValue
import dev.supergrecko.kllvm.ir.values.GenericValue
import dev.supergrecko.kllvm.ir.values.GlobalVariable
import dev.supergrecko.kllvm.ir.values.MetadataValue
import dev.supergrecko.kllvm.ir.values.PhiValue
import dev.supergrecko.kllvm.ir.values.PointerValue
import dev.supergrecko.kllvm.ir.values.constants.ConstantArray
import dev.supergrecko.kllvm.ir.values.constants.ConstantFloat
import dev.supergrecko.kllvm.ir.values.constants.ConstantInt
import dev.supergrecko.kllvm.ir.values.constants.ConstantStruct
import dev.supergrecko.kllvm.ir.values.constants.ConstantVector
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

    //region Core::Values::Constants::GeneralAPIs
    /**
     * Use the IR name for this value
     *
     * @see LLVM.LLVMGetValueName2
     * @see LLVM.LLVMSetValueName2
     */
    public var valueName: String
        get() {
            val ptr = LLVM.LLVMGetValueName2(ref, SizeTPointer(0))

            return ptr.string
        }
        set(value) = LLVM.LLVMSetValueName2(ref, value, value.length.toLong())

    /**
     * Get the type of this value
     *
     * @see LLVM.LLVMTypeOf
     */
    public fun getType(): Type {
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
    public fun getValueKind(): ValueKind = getValueKind(ref)

    /**
     * Dump the value
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

    // TODO: Implement these two
    public fun isAMDNode() {}
    public fun isAMDString() {}
    //endregion Core::Values::Constants::GeneralAPIs

    //region Core::Values::Constants
    /**
     * Determine if this value is a null pointer
     *
     * @see LLVM.LLVMIsNull
     */
    public fun isNull(): Boolean {
        return LLVM.LLVMIsNull(ref).fromLLVMBool()
    }

    /**
     * Cast this to a Constant Pointer to the current value
     *
     * @see LLVM.LLVMConstPointerCast
     */
    fun constPointerCast(toType: PointerType): PointerValue {
        val value = LLVM.LLVMConstPointerCast(ref, toType.ref)

        return PointerValue(value)
    }
    //endregion Core::Values::Constants

    //region Core::Values::Constants::ConstantExpressions
    /**
     * Get the opcode for a constant value
     *
     * TODO: Move this, presumably to instructions?
     *
     * @see LLVM.LLVMGetConstOpcode
     */
    public fun getOpcode(): Opcode {
        require(isConstant())

        val int = LLVM.LLVMGetConstOpcode(ref)

        return Opcode.values()
            .firstOrNull { it.value == int }
            ?: throw Unreachable()
    }
    //endregion Core::Values::Constants::ConstantExpressions

    //region Typecasting
    /**
     * Attempts to use the current [ref] for a new value
     */
    public fun asArrayValue() = ConstantArray(ref)
    public fun asFloatValue() = ConstantFloat(ref)
    public fun asFunctionValue() = FunctionValue(ref)
    public fun asGenericValue() = GenericValue(ref)
    public fun asGlobalValue() = GlobalVariable(ref)
    public fun asInstructionValue() = Instruction(ref)
    public fun asIntValue() = ConstantInt(ref)
    public fun asMetadataValue() = MetadataValue(ref)
    public fun asPhiValue() = PhiValue(ref)
    public fun asPointerValue() = PointerValue(ref)
    public fun asStructValue() = ConstantStruct(ref)
    public fun asVectorValue() = ConstantVector(ref)
    //endregion Typecasting

    public companion object {
        /**
         * Obtain the value kind for this value
         *
         * @see LLVM.LLVMGetValueKind
         */
        @JvmStatic
        public fun getValueKind(value: LLVMValueRef): ValueKind {
            val kind = LLVM.LLVMGetValueKind(value)

            return ValueKind.values()
                .firstOrNull { it.value == kind }
            // Theoretically unreachable, but kept if wrong LLVM version is used
                ?: throw IllegalArgumentException(
                    "Value $value has invalid value kind"
                )
        }
    }
}

package dev.supergrecko.kllvm.core.enumerations

import dev.supergrecko.kllvm.contracts.OrderedEnum
import org.bytedeco.llvm.global.LLVM

/**
 * Support type matching LLVMValueKind
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

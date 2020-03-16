package dev.supergrecko.kllvm.core.enumerations

import dev.supergrecko.kllvm.contracts.OrderedEnum
import org.bytedeco.llvm.global.LLVM

public enum class LLVMValueKind(public override val value: Int) : OrderedEnum<Int> {
    ArgumentValueKind(LLVM.LLVMArgumentValueKind),
    BasicBlockValueKind(LLVM.LLVMBasicBlockValueKind),
    MemoryUseValueKind(LLVM.LLVMMemoryUseValueKind),
    MemoryDefValueKind(LLVM.LLVMMemoryDefValueKind),
    MemoryPhiValueKind(LLVM.LLVMMemoryPhiValueKind),
    FunctionValueKind(LLVM.LLVMFunctionValueKind),
    GlobalAliasValueKind(LLVM.LLVMGlobalAliasValueKind),
    GlobalIFuncValueKind(LLVM.LLVMGlobalIFuncValueKind),
    GlobalVariableValueKind(LLVM.LLVMGlobalVariableValueKind),
    BlockAddressValueKind(LLVM.LLVMBlockAddressValueKind),
    ConstantExprValueKind(LLVM.LLVMConstantExprValueKind),
    ConstantArrayValueKind(LLVM.LLVMConstantArrayValueKind),
    ConstantStructValueKind(LLVM.LLVMConstantStructValueKind),
    ConstantVectorValueKind(LLVM.LLVMConstantVectorValueKind),
    UndefValueValueKind(LLVM.LLVMUndefValueValueKind),
    ConstantAggregateZeroValueKind(LLVM.LLVMConstantAggregateZeroValueKind),
    ConstantDataArrayValueKind(LLVM.LLVMConstantDataArrayValueKind),
    ConstantDataVectorValueKind(LLVM.LLVMConstantDataVectorValueKind),
    ConstantIntValueKind(LLVM.LLVMConstantIntValueKind),
    ConstantFPValueKind(LLVM.LLVMConstantFPValueKind),
    ConstantPointerNullValueKind(LLVM.LLVMConstantPointerNullValueKind),
    ConstantTokenNoneValueKind(LLVM.LLVMConstantTokenNoneValueKind),
    MetadataAsValueValueKind(LLVM.LLVMMetadataAsValueValueKind),
    InlineAsmValueKind(LLVM.LLVMInlineAsmValueKind),
    InstructionValueKind(LLVM.LLVMInstructionValueKind)
}
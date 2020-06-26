package dev.supergrecko.vexe.llvm.ir

import dev.supergrecko.vexe.llvm.internal.contracts.OrderedEnum
import org.bytedeco.llvm.global.LLVM

/**
 * Support types matching LLVMTypeKind
 *
 * [Documentation](https://llvm.org/doxygen/group__LLVMCCoreTypes.html)
 */
public enum class TypeKind(public override val value: Int) : OrderedEnum<Int> {
    Void(LLVM.LLVMVoidTypeKind),
    Half(LLVM.LLVMHalfTypeKind),
    Float(LLVM.LLVMFloatTypeKind),
    Double(LLVM.LLVMDoubleTypeKind),
    X86_FP80(LLVM.LLVMX86_FP80TypeKind),
    FP128(LLVM.LLVMFP128TypeKind),
    PPC_FP128(LLVM.LLVMPPC_FP128TypeKind),
    Label(LLVM.LLVMLabelTypeKind),
    Integer(LLVM.LLVMIntegerTypeKind),
    Function(LLVM.LLVMFunctionTypeKind),
    Struct(LLVM.LLVMStructTypeKind),
    Array(LLVM.LLVMArrayTypeKind),
    Pointer(LLVM.LLVMPointerTypeKind),
    Vector(LLVM.LLVMVectorTypeKind),
    Metadata(LLVM.LLVMMetadataTypeKind),
    X86_MMX(LLVM.LLVMX86_MMXTypeKind),
    Token(LLVM.LLVMTokenTypeKind)
}

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
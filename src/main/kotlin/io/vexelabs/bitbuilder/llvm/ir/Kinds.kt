package io.vexelabs.bitbuilder.llvm.ir

import io.vexelabs.bitbuilder.llvm.internal.contracts.ForeignEnum
import org.bytedeco.llvm.global.LLVM

public enum class TypeKind(public override val value: Int) : ForeignEnum<Int> {
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
    BFloat(LLVM.LLVMBFloatTypeKind),
    Token(LLVM.LLVMTokenTypeKind);

    public companion object : ForeignEnum.CompanionBase<Int, TypeKind> {
        public override val map: Map<Int, TypeKind> by lazy {
            values().associateBy(TypeKind::value)
        }
    }
}

public enum class ValueKind(public override val value: Int) : ForeignEnum<Int> {
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
    Instruction(LLVM.LLVMInstructionValueKind);

    public companion object : ForeignEnum.CompanionBase<Int, ValueKind> {
        public override val map: Map<Int, ValueKind> by lazy {
            values().associateBy(ValueKind::value)
        }
    }
}

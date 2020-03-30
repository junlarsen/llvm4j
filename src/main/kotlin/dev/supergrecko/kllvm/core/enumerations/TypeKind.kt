package dev.supergrecko.kllvm.core.enumerations

import dev.supergrecko.kllvm.contracts.OrderedEnum
import org.bytedeco.llvm.global.LLVM

/**
 * Support type matching LLVMTypeKind
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

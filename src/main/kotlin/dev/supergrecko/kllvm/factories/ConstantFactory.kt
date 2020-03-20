package dev.supergrecko.kllvm.factories

import dev.supergrecko.kllvm.annotation.ExpectsType
import dev.supergrecko.kllvm.annotation.RejectsType
import dev.supergrecko.kllvm.contracts.Factory
import dev.supergrecko.kllvm.core.LLVMType
import dev.supergrecko.kllvm.core.LLVMValue
import dev.supergrecko.kllvm.core.enumerations.LLVMTypeKind
import dev.supergrecko.kllvm.utils.Radix
import org.bytedeco.llvm.global.LLVM

public object ConstantFactory : Factory<LLVMValue> {

    @RejectsType(LLVMTypeKind.Function, LLVMTypeKind.Label)
    public fun constNull(ty: LLVMType): LLVMValue {
        require(!ty.inKinds(LLVMTypeKind.Function, LLVMTypeKind.Label))

        if (ty.isKind(LLVMTypeKind.Struct)) {
            require(!ty.isOpaqueStruct())
        }

        val value = LLVM.LLVMConstNull(ty.llvmType)

        return LLVMValue(value)
    }

    @ExpectsType(LLVMTypeKind.Integer)
    public fun constAllOnes(ty: LLVMType): LLVMValue {
        require(ty.isKind(LLVMTypeKind.Integer))

        val value = LLVM.LLVMConstAllOnes(ty.llvmType)

        return LLVMValue(value)
    }

    public fun constUndef(ty: LLVMType): LLVMValue {
        val value = LLVM.LLVMGetUndef(ty.llvmType)

        return LLVMValue(value)
    }

    public fun constPointerNull(ty: LLVMType): LLVMValue {
        val ptr = LLVM.LLVMConstPointerNull(ty.llvmType)

        return LLVMValue(ptr)
    }

    public fun constInt(ty: LLVMType, value: Long, signExtend: Boolean) {}
    public fun constIntAP(ty: LLVMType, words: List<Long>) {}
    public fun constInt(ty: LLVMType, text: String, radix: Radix) {}

    public fun constReal(ty: LLVMType, value: Long) {}
    public fun constReal(ty: LLVMType, text: String) {}
}
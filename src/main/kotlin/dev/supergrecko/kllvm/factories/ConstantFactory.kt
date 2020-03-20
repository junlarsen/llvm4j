package dev.supergrecko.kllvm.factories

import dev.supergrecko.kllvm.annotation.ExpectsType
import dev.supergrecko.kllvm.annotation.RejectsType
import dev.supergrecko.kllvm.contracts.Factory
import dev.supergrecko.kllvm.core.LLVMContext
import dev.supergrecko.kllvm.core.LLVMType
import dev.supergrecko.kllvm.core.LLVMValue
import dev.supergrecko.kllvm.core.enumerations.LLVMTypeKind
import dev.supergrecko.kllvm.utils.Radix
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public object ConstantFactory : Factory<LLVMValue> {
    //region Core::Values::Constants

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

    //endregion Core::Values::Constants
    //region Core::Values::Constants::ScalarConstants

    public fun constInt(ty: LLVMType, value: Long, signExtend: Boolean): LLVMValue { TODO() }
    public fun constIntOfAP(ty: LLVMType, words: List<Long>): LLVMValue { TODO() }
    public fun constIntOfString(ty: LLVMType, text: String, radix: Radix): LLVMValue { TODO() }

    public fun constReal(ty: LLVMType, value: Long): LLVMValue { TODO() }
    public fun constRealOfString(ty: LLVMType, text: String): LLVMValue { TODO() }

    //endregion Core::Values::Constants::ScalarConstants
    //region Core::Values::Constants::CompositeConstants

    public fun constString(text: String, nullTerminate: Boolean, context: LLVMContext = LLVMContext.global()): LLVMValue { TODO() }
    public fun constStruct(values: List<LLVMValue>, packed: Boolean, context: LLVMContext = LLVMContext.global()): LLVMValue { TODO() }
    public fun constArray(ty: LLVMType, values: List<LLVMValue>): LLVMValue { TODO() }
    public fun constNamedStruct(ty: LLVMType, values: List<LLVMValue>): LLVMValue { TODO() }
    public fun constVector(values: LLVMValueRef): LLVMValue { TODO() }

    //endregion Core::Values::Constants::CompositeConstants
    //region Core::Values::Constants::ConstantExpressions

    //endregion Core::Values::Constants::ConstantExpressions
}

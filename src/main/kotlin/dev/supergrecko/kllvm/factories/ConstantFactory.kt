package dev.supergrecko.kllvm.factories

import dev.supergrecko.kllvm.annotation.ExpectsType
import dev.supergrecko.kllvm.annotation.RejectsType
import dev.supergrecko.kllvm.contracts.Factory
import dev.supergrecko.kllvm.core.typedefs.LLVMContext
import dev.supergrecko.kllvm.core.typedefs.LLVMType
import dev.supergrecko.kllvm.core.typedefs.LLVMValue
import dev.supergrecko.kllvm.core.enumerations.LLVMTypeKind
import dev.supergrecko.kllvm.utils.Radix
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public object ConstantFactory : Factory<LLVMValue> {
    //region Core::Values::Constants

    public fun constNull(type: LLVMType): LLVMValue {
        require(!type.isInTypeKinds(LLVMTypeKind.Function, LLVMTypeKind.Label))

        if (type.isTypeKind(LLVMTypeKind.Struct)) {
            require(!type.isStructOpaque())
        }

        val value = LLVM.LLVMConstNull(type.llvmType)

        return LLVMValue(value)
    }

    public fun constAllOnes(type: LLVMType): LLVMValue {
        require(type.isTypeKind(LLVMTypeKind.Integer))

        val value = LLVM.LLVMConstAllOnes(type.llvmType)

        return LLVMValue(value)
    }

    public fun constUndef(type: LLVMType): LLVMValue {
        val value = LLVM.LLVMGetUndef(type.llvmType)

        return LLVMValue(value)
    }

    public fun constPointerNull(type: LLVMType): LLVMValue {
        val ptr = LLVM.LLVMConstPointerNull(type.llvmType)

        return LLVMValue(ptr)
    }

    //endregion Core::Values::Constants
    //region Core::Values::Constants::ScalarConstants

    public fun constInt(type: LLVMType, value: Long, signExtend: Boolean): LLVMValue { TODO() }
    public fun constIntOfAP(type: LLVMType, words: List<Long>): LLVMValue { TODO() }
    public fun constIntOfString(type: LLVMType, text: String, radix: Radix): LLVMValue { TODO() }

    public fun constReal(type: LLVMType, value: Long): LLVMValue { TODO() }
    public fun constRealOfString(type: LLVMType, text: String): LLVMValue { TODO() }

    //endregion Core::Values::Constants::ScalarConstants
    //region Core::Values::Constants::CompositeConstants

    public fun constString(text: String, nullTerminate: Boolean, context: LLVMContext = LLVMContext.getGlobalContext()): LLVMValue { TODO() }
    public fun constStruct(values: List<LLVMValue>, packed: Boolean, context: LLVMContext = LLVMContext.getGlobalContext()): LLVMValue { TODO() }
    public fun constArray(type: LLVMType, values: List<LLVMValue>): LLVMValue { TODO() }
    public fun constNamedStruct(type: LLVMType, values: List<LLVMValue>): LLVMValue { TODO() }
    public fun constVector(values: LLVMValueRef): LLVMValue { TODO() }

    //endregion Core::Values::Constants::CompositeConstants
    //region Core::Values::Constants::ConstantExpressions

    //endregion Core::Values::Constants::ConstantExpressions
}

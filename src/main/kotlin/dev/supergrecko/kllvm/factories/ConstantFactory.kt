package dev.supergrecko.kllvm.factories

import dev.supergrecko.kllvm.contracts.Factory
import dev.supergrecko.kllvm.core.typedefs.Context
import dev.supergrecko.kllvm.core.typedefs.Type
import dev.supergrecko.kllvm.core.typedefs.Value
import dev.supergrecko.kllvm.core.types.StructType
import dev.supergrecko.kllvm.utils.Radix
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public object ConstantFactory : Factory<Value> {
    //region Core::Values::Constants

    public fun constNull(type: Type): Value {
        if (type is StructType) {
            require(!type.isOpaque())
        }

        val value = LLVM.LLVMConstNull(type.llvmType)

        return Value(value)
    }

    public fun constAllOnes(type: Type): Value {
        val value = LLVM.LLVMConstAllOnes(type.llvmType)

        return Value(value)
    }

    public fun constUndef(type: Type): Value {
        val value = LLVM.LLVMGetUndef(type.llvmType)

        return Value(value)
    }

    public fun constPointerNull(type: Type): Value {
        val ptr = LLVM.LLVMConstPointerNull(type.llvmType)

        return Value(ptr)
    }

    //endregion Core::Values::Constants
    //region Core::Values::Constants::ScalarConstants

    public fun constInt(type: Type, value: Long, signExtend: Boolean): Value { TODO() }
    public fun constIntOfAP(type: Type, words: List<Long>): Value { TODO() }
    public fun constIntOfString(type: Type, text: String, radix: Radix): Value { TODO() }

    public fun constReal(type: Type, value: Long): Value { TODO() }
    public fun constRealOfString(type: Type, text: String): Value { TODO() }

    //endregion Core::Values::Constants::ScalarConstants
    //region Core::Values::Constants::CompositeConstants

    public fun constString(text: String, nullTerminate: Boolean, context: Context = Context.getGlobalContext()): Value { TODO() }
    public fun constStruct(values: List<Value>, packed: Boolean, context: Context = Context.getGlobalContext()): Value { TODO() }
    public fun constArray(type: Type, values: List<Value>): Value { TODO() }
    public fun constNamedStruct(type: Type, values: List<Value>): Value { TODO() }
    public fun constVector(values: LLVMValueRef): Value { TODO() }

    //endregion Core::Values::Constants::CompositeConstants
    //region Core::Values::Constants::ConstantExpressions

    //endregion Core::Values::Constants::ConstantExpressions
}

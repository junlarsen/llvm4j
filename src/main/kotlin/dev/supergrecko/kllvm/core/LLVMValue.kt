package dev.supergrecko.kllvm.core

import dev.supergrecko.kllvm.core.enumerations.LLVMValueKind
import dev.supergrecko.kllvm.utils.toBoolean
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM
import java.lang.IllegalArgumentException

public class LLVMValue internal constructor(
        internal val llvmValue: LLVMValueRef,
        public var kind: LLVMValueKind = getValueKind(llvmValue)
) {
    //region Core::Types

    public fun isNull(): Boolean {
        return LLVM.LLVMIsNull(llvmValue).toBoolean()
    }

    //endregion Core::Types
    //region Core::Values::Constants::ScalarConstants

    public fun getIntZeroExtValue(): Long { TODO() }
    public fun getIntSignExtValue(): Long { TODO() }
    public fun getRealValue(): Double { TODO() }

    //endregion Core::Values::Constants::ScalarConstants
    //region Core::Values::Constants::CompositeConstants

    public fun isConstantString(): Boolean { TODO() }
    public fun getAsString(): Boolean { TODO() }
    public fun getElementAsConstant(index: Boolean): LLVMValue { TODO() }

    //endregion Core::Values::Constants::CompositeConstants
    //region Core::Values::Constants::ConstantExpressions



    //endregion Core::Values::Constants::ConstantExpressions

    /**
     * Obtain the type of a value
     *
     * TODO: Find region
     */
    public fun typeOf(): LLVMType {
        val type = LLVM.LLVMTypeOf(llvmValue)

        return LLVMType(type, LLVMType.getTypeKind(type))
    }

    /**
     * Obtain the value kind for this value
     */
    public fun getValueKind(): LLVMValueKind {
        return getValueKind(llvmValue)
    }

    public fun isKind(kind: LLVMValueKind): Boolean {
        return kind == this.kind
    }

    public fun inKinds(vararg kinds: LLVMValueKind): Boolean {
        return kind in kinds
    }

    public companion object {
        @JvmStatic
        public fun getValueKind(value: LLVMValue): LLVMValueKind {
            return getValueKind(value.llvmValue)
        }

        /**
         * Obtain the value kind for this value
         */
        @JvmStatic
        public fun getValueKind(value: LLVMValueRef): LLVMValueKind {
            val kind = LLVM.LLVMGetValueKind(value)

            return LLVMValueKind.values()
                    .firstOrNull { it.value == kind }
                    // Theoretically unreachable, but kept if wrong LLVM version is used
                    ?: throw IllegalArgumentException("Value $value has invalid value kind")
        }
    }
}
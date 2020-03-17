package dev.supergrecko.kllvm.core

import dev.supergrecko.kllvm.contracts.Validator
import dev.supergrecko.kllvm.core.enumerations.LLVMValueKind
import dev.supergrecko.kllvm.utils.toBoolean
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM
import java.lang.IllegalArgumentException

public class LLVMValue internal constructor(
        internal val llvmValue: LLVMValueRef,
        kind: LLVMValueKind
) : Validator<LLVMValueKind>(kind) {
    public fun isNull(): Boolean {
        return LLVM.LLVMIsNull(llvmValue).toBoolean()
    }

    /**
     * Obtain the type of a value
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

    public companion object {
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
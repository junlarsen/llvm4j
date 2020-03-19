package dev.supergrecko.kllvm.core

import dev.supergrecko.kllvm.core.enumerations.LLVMTypeKind
import dev.supergrecko.kllvm.core.enumerations.LLVMValueKind
import dev.supergrecko.kllvm.utils.except
import dev.supergrecko.kllvm.utils.requires
import dev.supergrecko.kllvm.utils.toBoolean
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM
import java.lang.IllegalArgumentException

public class LLVMValue internal constructor(
        internal val llvmValue: LLVMValueRef,
        public var kind: LLVMValueKind
) {
    //region Core::Types

    public fun isNull(): Boolean {
        return LLVM.LLVMIsNull(llvmValue).toBoolean()
    }

    //endregion Core::Types

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

    public companion object {
        //region Core::Values::Constants

        @JvmStatic
        public fun createConstAllOnes(type: LLVMType): LLVMValue {
            requires(type.kind, LLVMTypeKind.Integer)

            val value = LLVM.LLVMConstAllOnes(type.llvmType)

            return LLVMValue(value, getValueKind(value))
        }

        /**
         * Create a zero value of a type
         *
         * This operation is not valid for functions, labels or opaque structures.
         */
        @JvmStatic
        public fun createZeroValue(type: LLVMType): LLVMValue {
            except(type.kind, LLVMTypeKind.Function, LLVMTypeKind.Label)

            if (type.getTypeKind() == LLVMTypeKind.Struct) {
                require(!type.isOpaqueStruct())
            }

            val value = LLVM.LLVMConstNull(type.llvmType)

            return LLVMValue(value, getValueKind(value))
        }

        /**
         * Obtain a constant that is a const ptr pointing to NULL for the specified type
         */
        @JvmStatic
        public fun createConstPointerNull(type: LLVMType): LLVMValue {
            val ptr = LLVM.LLVMConstPointerNull(type.llvmType)

            return LLVMValue(ptr, getValueKind(ptr))
        }

        @JvmStatic
        public fun createUndefined(type: LLVMType): LLVMValue {
            val value = LLVM.LLVMGetUndef(type.llvmType)

            return LLVMValue(value, LLVMValue.getValueKind(value))
        }

        //endregion Core::Values::Constants
        //region Core::Values::Constants::ScalarConstants



        //endregion Core::Values::Constants::ScalarConstants

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
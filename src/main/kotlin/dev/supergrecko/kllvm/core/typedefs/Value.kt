package dev.supergrecko.kllvm.core.typedefs

import dev.supergrecko.kllvm.contracts.Unreachable
import dev.supergrecko.kllvm.core.enumerations.ThreadLocalMode
import dev.supergrecko.kllvm.core.enumerations.ValueKind
import dev.supergrecko.kllvm.utils.toBoolean
import dev.supergrecko.kllvm.utils.toInt
import org.bytedeco.javacpp.SizeTPointer
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM
import java.lang.reflect.Constructor

public open class Value internal constructor(
        internal val llvmValue: LLVMValueRef
) {
    //region Core::Values::Constants::GlobalVariables
    public var externallyInitialized: Boolean
        get() = LLVM.LLVMIsExternallyInitialized(llvmValue).toBoolean()
        set(value) = LLVM.LLVMSetExternallyInitialized(llvmValue, value.toInt())

    public var initializer: Value
        get() = Value(LLVM.LLVMGetInitializer(llvmValue))
        set(value) = LLVM.LLVMSetInitializer(llvmValue, value.llvmValue)

    public var globalConstant: Boolean
        get() = LLVM.LLVMIsGlobalConstant(llvmValue).toBoolean()
        set(value) = LLVM.LLVMSetGlobalConstant(llvmValue, value.toInt())

    public var threadLocalMode: ThreadLocalMode
        get() {
            val mode = LLVM.LLVMGetThreadLocalMode(llvmValue)

            return ThreadLocalMode.values()
                    .firstOrNull { it.value == mode }
                    ?: throw Unreachable()
        }
        set(value) = LLVM.LLVMSetThreadLocalMode(llvmValue, value.value)

    public var threadLocal: Boolean
        get() = LLVM.LLVMIsThreadLocal(llvmValue).toBoolean()
        set(value) = LLVM.LLVMSetThreadLocal(llvmValue, value.toInt())
    //endregion Core::Values::Constants::GlobalVariables

    //region Core::Values::Constants
    public fun isNull(): Boolean {
        return LLVM.LLVMIsNull(llvmValue).toBoolean()
    }
    //endregion Core::Values::Constants

    //region Core::Values::Constants::GeneralAPIs
    public fun getType(): Type {
        val type = LLVM.LLVMTypeOf(llvmValue)

        return Type(type)
    }

    public fun isUndef(): Boolean {
        return LLVM.LLVMIsUndef(llvmValue).toBoolean()
    }

    public fun isConstant(): Boolean {
        return LLVM.LLVMIsConstant(llvmValue).toBoolean()
    }

    public fun setValueName(name: String) {
        LLVM.LLVMSetValueName2(llvmValue, name, name.length.toLong())
    }

    public fun getValueName(): String {
        val ptr = LLVM.LLVMGetValueName2(llvmValue, SizeTPointer(0))

        return ptr.string
    }

    public fun getValueKind(): ValueKind = getValueKind(llvmValue)

    public fun dump() {
        LLVM.LLVMDumpValue(llvmValue)
    }

    public fun dumpToString(): String {
        val ptr = LLVM.LLVMPrintValueToString(llvmValue)

        return ptr.string
    }

    public fun replaceAllUsesWith(value: Value) {
        LLVM.LLVMReplaceAllUsesWith(llvmValue, value.llvmValue)
    }

    // TODO: Implement these two
    public fun isAMDNode() {}
    public fun isAMDString() {}
    //endregion Core::Values::Constants::GeneralAPIs

    public inline fun <reified T : Value> cast(): T {
        val ctor: Constructor<T> = T::class.java.getDeclaredConstructor(LLVMValueRef::class.java)

        return ctor.newInstance(getUnderlyingReference())
                ?: throw TypeCastException("Failed to cast LLVMType to T")
    }

    public fun getUnderlyingReference(): LLVMValueRef = llvmValue

    public companion object {
        /**
         * Obtain the value kind for this value
         */
        @JvmStatic
        public fun getValueKind(value: LLVMValueRef): ValueKind {
            val kind = LLVM.LLVMGetValueKind(value)

            return ValueKind.values()
                    .firstOrNull { it.value == kind }
            // Theoretically unreachable, but kept if wrong LLVM version is used
                    ?: throw IllegalArgumentException("Value $value has invalid value kind")
        }
    }
}

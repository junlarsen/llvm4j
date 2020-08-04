package dev.supergrecko.vexe.llvm.ir

import dev.supergrecko.vexe.llvm.internal.contracts.ContainsReference
import dev.supergrecko.vexe.llvm.internal.contracts.Unreachable
import dev.supergrecko.vexe.llvm.internal.util.fromLLVMBool
import dev.supergrecko.vexe.llvm.ir.types.ArrayType
import dev.supergrecko.vexe.llvm.ir.types.PointerType
import dev.supergrecko.vexe.llvm.ir.types.StructType
import dev.supergrecko.vexe.llvm.ir.types.VectorType
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM

public open class Type internal constructor() : ContainsReference<LLVMTypeRef> {
    public final override lateinit var ref: LLVMTypeRef
        internal set

    /**
     * Construct a new Type from an LLVM pointer reference
     */
    public constructor(llvmRef: LLVMTypeRef) : this() {
        ref = llvmRef
    }

    //region Core::Types
    /**
     * Get the type kind for this type
     *
     * @see LLVM.LLVMGetTypeKind
     */
    public fun getTypeKind(): TypeKind = getTypeKind(ref)

    /**
     * Determine whether this type has a known size
     *
     * @see LLVM.LLVMTypeIsSized
     */
    public fun isSized(): Boolean {
        return LLVM.LLVMTypeIsSized(ref).fromLLVMBool()
    }

    /**
     * Get the context this type is unique to
     *
     * @see LLVM.LLVMGetTypeContext
     */
    public fun getContext(): Context {
        val ctx = LLVM.LLVMGetTypeContext(ref)

        return Context(ctx)
    }

    /**
     * Get the LLVM IR for this type
     *
     * This IR must be disposed via [IR.dispose] otherwise memory will
     * be leaked.
     *
     * @see LLVM.LLVMPrintTypeToString
     */
    public fun getIR(): IR {
        val ptr = LLVM.LLVMPrintTypeToString(ref)

        return IR(ptr)
    }
    //endregion Core::Types

    //region Core::Values::Constants
    /**
     * Get a constant null of this type
     *
     * @see LLVM.LLVMConstNull
     */
    public fun getConstantNull(): Value {
        // Opaque structures cannot have a null types
        if (this is StructType) {
            require(!isOpaque())
        }

        val v = LLVM.LLVMConstNull(ref)

        return Value(v)
    }

    /**
     * Get a constant undefined of this type
     *
     * @see LLVM.LLVMGetUndef
     */
    public fun getConstantUndef(): Value {
        val v = LLVM.LLVMGetUndef(ref)

        return Value(v)
    }

    /**
     * Get a constant nullptr of this type
     *
     * @see LLVM.LLVMConstPointerNull
     */
    public fun getConstantNullPointer(): Value {
        val v = LLVM.LLVMConstPointerNull(ref)

        return Value(v)
    }
    //endregion Core::Values::Constants

    //region Typecasting
    /**
     * Get a pointer type which points to this type
     */
    public fun toPointerType(addressSpace: Int = 0): PointerType {
        return PointerType(this, addressSpace)
    }

    /**
     * Get an array type of [size] elements containing elements of this type
     */
    public fun toArrayType(size: Int) = ArrayType(this, size)

    /**
     * Get a vector type of [size] elements containing elements of this type
     */
    public fun toVectorType(size: Int) = VectorType(this, size)
    //endregion Typecasting

    companion object {
        /**
         * Get the type kind of a type
         *
         * @see LLVM.LLVMGetTypeKind
         */
        @JvmStatic
        internal fun getTypeKind(type: LLVMTypeRef): TypeKind {
            val kind = LLVM.LLVMGetTypeKind(type)

            return TypeKind.values()
                .firstOrNull { it.value == kind }
                ?: throw Unreachable()
        }
    }
}

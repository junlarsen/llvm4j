package io.vexelabs.bitbuilder.llvm.ir

import io.vexelabs.bitbuilder.internal.fromLLVMBool
import io.vexelabs.bitbuilder.llvm.internal.contracts.ContainsReference
import io.vexelabs.bitbuilder.llvm.ir.types.ArrayType
import io.vexelabs.bitbuilder.llvm.ir.types.PointerType
import io.vexelabs.bitbuilder.llvm.ir.types.StructType
import io.vexelabs.bitbuilder.llvm.ir.types.VectorType
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM

/**
 * Interface to llvm::Type
 *
 * A type is the data type of a [Value] in the LLVM IR.
 *
 * See https://llvm.org/docs/LangRef.html#type-system
 *
 * @see LLVMTypeRef
 */
public open class Type internal constructor() : ContainsReference<LLVMTypeRef> {
    public final override lateinit var ref: LLVMTypeRef
        internal set

    public constructor(llvmRef: LLVMTypeRef) : this() {
        ref = llvmRef
    }

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

    /**
     * Get a pointer type which points to this type
     */
    public fun toPointerType(addressSpace: Int = 0): PointerType {
        return PointerType(this, addressSpace)
    }

    /**
     * Get an array type of [size] elements containing elements of this type
     */
    public fun toArrayType(size: Int): ArrayType = ArrayType(this, size)

    /**
     * Get a vector type of [size] elements containing elements of this type
     */
    public fun toVectorType(size: Int): VectorType = VectorType(this, size)

    public companion object {
        /**
         * Get the type kind of a type
         *
         * @see LLVM.LLVMGetTypeKind
         */
        @JvmStatic
        internal fun getTypeKind(type: LLVMTypeRef): TypeKind {
            val kind = LLVM.LLVMGetTypeKind(type)

            return TypeKind[kind]
        }
    }
}

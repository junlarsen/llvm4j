package io.vexelabs.bitbuilder.llvm.ir

import io.vexelabs.bitbuilder.internal.fromLLVMBool
import io.vexelabs.bitbuilder.internal.toPointerPointer
import io.vexelabs.bitbuilder.llvm.internal.contracts.ContainsReference
import io.vexelabs.bitbuilder.llvm.ir.types.ArrayType
import io.vexelabs.bitbuilder.llvm.ir.types.PointerType
import io.vexelabs.bitbuilder.llvm.ir.types.StructType
import io.vexelabs.bitbuilder.llvm.ir.types.VectorType
import io.vexelabs.bitbuilder.llvm.ir.values.constants.ConstantArray
import io.vexelabs.bitbuilder.llvm.ir.values.constants.ConstantPointer
import io.vexelabs.bitbuilder.llvm.ir.values.constants.ConstantVector
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
    public fun getConstantNullPointer(): ConstantPointer {
        val v = LLVM.LLVMConstPointerNull(ref)

        return ConstantPointer(v)
    }

    /**
     * Create an array of values of a given [type]
     *
     * @see LLVM.LLVMConstArray
     */
    public fun getConstantArray(
        type: Type,
        vararg values: Value
    ): ConstantArray {
        val ptr = values.map { it.ref }.toPointerPointer()
        val ref = LLVM.LLVMConstArray(type.ref, ptr, values.size)

        ptr.deallocate()

        return ConstantArray(ref)
    }

    /**
     * Create a new vector of a list of values
     *
     * @see LLVM.LLVMConstVector
     */
    public fun getConstantVector(
        vararg values: Value
    ): ConstantVector {
        val ptr = values.map { it.ref }.toPointerPointer()
        val ref = LLVM.LLVMConstVector(ptr, values.size)

        ptr.deallocate()

        return ConstantVector(ref)
    }

    /**
     * Get a pointer type
     *
     * Creates a pointer which points to this type. An address space may be
     * provided but defaults to 0 (unspecified).
     *
     * @throws IllegalArgumentException if pointer space is negative
     *
     * @see LLVM.LLVMPointerType
     */
    public fun getPointerType(withAddressSpace: Int? = null): PointerType {
        if (withAddressSpace != null) {
            require(withAddressSpace >= 0) { "Cannot use negative address space" }
        }

        val ref = LLVM.LLVMPointerType(ref, withAddressSpace ?: 0)

        return PointerType(ref)
    }

    /**
     * Get an array type
     *
     * Constructs an array of this type with size [size].
     *
     * @see LLVM.LLVMArrayType
     */
    public fun getArrayType(size: Int): ArrayType {
        require(size >= 0) { "Cannot make array of negative size" }

        val ref = LLVM.LLVMArrayType(ref, size)

        return ArrayType(ref)
    }

    /**
     * Get a vector type
     *
     * Constructs a vector types of this type with size [size].
     *
     * @see LLVM.LLVMVectorType
     */
    public fun getVectorType(size: Int): VectorType {
        require(size >= 0) { "Cannot make vector of negative size" }

        val ref = LLVM.LLVMVectorType(ref, size)

        return VectorType(ref)
    }

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

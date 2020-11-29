package io.vexelabs.bitbuilder.llvm.ir

import io.vexelabs.bitbuilder.internal.fromLLVMBool
import io.vexelabs.bitbuilder.internal.toLLVMBool
import io.vexelabs.bitbuilder.internal.toPointerPointer
import io.vexelabs.bitbuilder.llvm.internal.contracts.ContainsReference
import io.vexelabs.bitbuilder.llvm.internal.contracts.Disposable
import io.vexelabs.bitbuilder.llvm.internal.contracts.Unreachable
import io.vexelabs.bitbuilder.llvm.ir.callbacks.DiagnosticHandlerBase
import io.vexelabs.bitbuilder.llvm.ir.callbacks.DiagnosticHandlerCallback
import io.vexelabs.bitbuilder.llvm.ir.callbacks.YieldCallback
import io.vexelabs.bitbuilder.llvm.ir.callbacks.YieldCallbackBase
import io.vexelabs.bitbuilder.llvm.ir.types.FloatType
import io.vexelabs.bitbuilder.llvm.ir.types.FunctionType
import io.vexelabs.bitbuilder.llvm.ir.types.IntType
import io.vexelabs.bitbuilder.llvm.ir.types.LabelType
import io.vexelabs.bitbuilder.llvm.ir.types.MetadataType
import io.vexelabs.bitbuilder.llvm.ir.types.StructType
import io.vexelabs.bitbuilder.llvm.ir.types.TokenType
import io.vexelabs.bitbuilder.llvm.ir.types.VoidType
import io.vexelabs.bitbuilder.llvm.ir.types.X86MMXType
import io.vexelabs.bitbuilder.llvm.ir.values.constants.ConstantStruct
import org.bytedeco.javacpp.Pointer
import org.bytedeco.llvm.LLVM.LLVMContextRef
import org.bytedeco.llvm.global.LLVM

/**
 * Interface to llvm::Context
 *
 * A context is an instance of LLVMs core infrastructure, including metadata
 * id tables, types and constant uniquing tables. There should only be one
 * context per thread if you are running LLVM multithreaded.
 *
 * By default, LLVM also provides a "Global Context" which is lazily
 * initialized upon first usage, meaning you do not need to manually create a
 * context unless you're using multiple threads.
 *
 * @see Context.getGlobalContext
 *
 * @see LLVMContextRef
 */
public class Context public constructor(
    public override val ref: LLVMContextRef = LLVM.LLVMContextCreate()
) : Disposable, ContainsReference<LLVMContextRef> {
    public override var valid: Boolean = true

    /**
     * Does this context discard the IR names for values
     *
     * @see LLVM.LLVMContextShouldDiscardValueNames
     */
    public fun isDiscardingValueNames(): Boolean {
        return LLVM.LLVMContextShouldDiscardValueNames(ref).fromLLVMBool()
    }

    /**
     * Set whether this module should discard value names
     *
     * @see LLVM.LLVMContextSetDiscardValueNames
     */
    public fun setDiscardValueNames(discard: Boolean) {
        LLVM.LLVMContextSetDiscardValueNames(ref, discard.toLLVMBool())
    }

    /**
     * Set the DiagnosticHandler for this context
     *
     * Optionally, pass a [payload] which will be passed as the second
     * argument to the callback type
     *
     * @see LLVM.LLVMContextSetDiagnosticHandler
     */
    public fun setDiagnosticHandler(
        payload: Pointer? = null,
        handler: DiagnosticHandlerCallback
    ) {
        val handlePtr = DiagnosticHandlerBase(handler)

        LLVM.LLVMContextSetDiagnosticHandler(
            ref,
            handlePtr,
            payload
        )
    }

    /**
     * Get the llvm::DiagnosticContext for this context
     *
     * Get the payload which was set with the diagnostic handler
     *
     * @see LLVM.LLVMContextGetDiagnosticContext
     */
    public fun getDiagnosticContext(): Pointer? {
        val ctx = LLVM.LLVMContextGetDiagnosticContext(ref)

        return ctx?.let { Pointer(it) }
    }

    /**
     * Register a yield callback with the given context
     *
     * Optionally, pass a [payload] which will be passed as the second
     * argument to the callback type
     *
     * @see LLVM.LLVMContextSetYieldCallback
     */
    public fun setYieldCallback(
        payload: Pointer? = null,
        callback: YieldCallback
    ) {
        val handlePtr = YieldCallbackBase(callback)

        LLVM.LLVMContextSetYieldCallback(ref, handlePtr, payload)
    }

    /**
     * Get the metadata kind id [name]
     *
     * This is used for [Instruction.setMetadata] to convert string metadata
     * keys to integer ones.
     *
     * @see LLVM.LLVMGetMDKindID
     * @see LLVM.LLVMGetMDKindIDInContext
     */
    public fun getMetadataKindId(name: String): Int {
        return LLVM.LLVMGetMDKindIDInContext(ref, name, name.length)
    }

    /**
     * Create a new metadata node.
     *
     * A metadata node is a list of other metadata items.
     *
     * @see LLVM.LLVMMDNodeInContext2
     */
    public fun createMetadataNode(values: List<Metadata>): MetadataNode {
        val ptr = values.map { it.ref }.toPointerPointer()
        val ref = LLVM.LLVMMDNodeInContext2(
            ref,
            ptr,
            values.size.toLong()
        )

        ptr.deallocate()

        return MetadataNode(ref)
    }

    /**
     * Create a new metadata string
     *
     * A metadata string is a single tagged string value.
     *
     * @see LLVM.LLVMMDStringInContext2
     */
    public fun createMetadataString(value: String): MetadataString {
        val ref = LLVM.LLVMMDStringInContext2(
            ref,
            value,
            value.length.toLong()
        )

        return MetadataString(ref)
    }

    /**
     * Create a new module with a file name
     *
     * Optionally pass a [context] which this module will reside in. If no
     * context is passed, the global llvm context is used.
     *
     * @see LLVM.LLVMModuleCreateWithNameInContext
     */
    public fun createModule(sourceFileName: String): Module {
        val ref = LLVM.LLVMModuleCreateWithNameInContext(sourceFileName, ref)

        return Module(ref)
    }

    /**
     * Get a floating point type
     *
     * This function will create a fp types of the provided [kind].
     *
     * @see LLVM.LLVMHalfTypeKind
     */
    public fun getFloatType(kind: TypeKind): FloatType {
        val ref = when (kind) {
            TypeKind.Half -> LLVM.LLVMHalfTypeInContext(ref)
            TypeKind.Float -> LLVM.LLVMFloatTypeInContext(ref)
            TypeKind.Double -> LLVM.LLVMDoubleTypeInContext(ref)
            TypeKind.X86_FP80 -> LLVM.LLVMX86FP80TypeInContext(ref)
            TypeKind.FP128 -> LLVM.LLVMFP128TypeInContext(ref)
            TypeKind.PPC_FP128 -> LLVM.LLVMPPCFP128TypeInContext(ref)
            TypeKind.BFloat -> LLVM.LLVMBFloatTypeInContext(ref)
            else -> throw Unreachable()
        }

        return FloatType(ref)
    }

    /**
     * Get an integer type
     *
     * This will create an integer types of the size [size]. If the size matches
     * any of LLVM's preset integer sizes then that size will be returned.
     * Otherwise an arbitrary size int types will be returned.
     *
     * @throws IllegalArgumentException if size not in 1..8388606
     *
     * @see LLVM.LLVMIntTypeInContext
     */
    public fun getIntType(size: Int): IntType {
        val ref = when (size) {
            1 -> LLVM.LLVMInt1TypeInContext(ref)
            8 -> LLVM.LLVMInt8TypeInContext(ref)
            16 -> LLVM.LLVMInt16TypeInContext(ref)
            32 -> LLVM.LLVMInt32TypeInContext(ref)
            64 -> LLVM.LLVMInt64TypeInContext(ref)
            128 -> LLVM.LLVMInt128TypeInContext(ref)
            else -> {
                require(size in 1..8388606) {
                    "LLVM only supports integers of 2^23-1 bits size"
                }

                LLVM.LLVMIntTypeInContext(ref, size)
            }
        }

        return IntType(ref)
    }

    /**
     * Get the label type
     *
     * The label type is the type kind of a [BasicBlock]
     *
     * @see LLVM.LLVMLabelTypeInContext
     */
    public fun getLabelType(): LabelType {
        val ref = LLVM.LLVMLabelTypeInContext(ref)

        return LabelType(ref)
    }

    /**
     * Get the metadata type
     *
     * The metadata type is the type kind of a [Metadata]
     *
     * @see LLVM.LLVMMetadataTypeInContext
     */
    public fun getMetadataType(): MetadataType {
        val ref = LLVM.LLVMMetadataTypeInContext(ref)

        return MetadataType(ref)
    }

    /**
     * Get a structure type
     *
     * This method creates a structure types inside this context. All struct
     * members should be passed in the [members] iterable.
     *
     * @see LLVM.LLVMStructTypeInContext
     */
    public fun getStructType(
        vararg members: Type,
        packed: Boolean
    ): StructType {
        val ptr = members.map { it.ref }.toPointerPointer()
        val ref = LLVM.LLVMStructTypeInContext(
            ref,
            ptr,
            members.size,
            packed.toLLVMBool()
        )

        ptr.deallocate()

        return StructType(ref)
    }

    /**
     * Get an opaque struct type
     *
     * This will create an opaque struct (a struct without a body, like a C
     * forward declaration) with the given [name].
     *
     * To add members to an opaque struct, use the [StructType.setBody] method.
     *
     * @see LLVM.LLVMStructCreateNamed
     */
    public fun getOpaqueStructType(name: String): StructType {
        val ref = LLVM.LLVMStructCreateNamed(ref, name)

        return StructType(ref)
    }

    /**
     * Get the token type
     *
     * The token type is used when a value is associated with an instruction
     * but all uses of the value must not attempt to introspect or obscure it.
     * As such, it is not appropriate to have a phi or select of type token.
     *
     * @see LLVM.LLVMTokenTypeInContext
     */
    public fun getTokenType(): TokenType {
        val ref = LLVM.LLVMTokenTypeInContext(ref)

        return TokenType(ref)
    }

    /**
     * Get the void type
     *
     * The void type signals the return type of a function which does not
     * return a value. It does not represent any value and it has no size
     *
     * @see LLVM.LLVMVoidTypeInContext
     */
    public fun getVoidType(): VoidType {
        val ref = LLVM.LLVMVoidTypeInContext(ref)

        return VoidType(ref)
    }

    /**
     * Get the x86_mmx type
     *
     * The x86_mmx type represents a value held in an MMX register on an x86
     * machine. The operations allowed on it are quite limited: parameters
     * and return values, load and store, and bitcast. User-specified MMX
     * instructions are represented as intrinsic or asm calls with arguments
     * and/or results of this type. There are no arrays, vectors or constants
     * of this type.
     *
     * @see LLVM.LLVMX86MMXTypeInContext
     */
    public fun getX86MMXType(): X86MMXType {
        val ref = LLVM.LLVMX86MMXTypeInContext(ref)

        return X86MMXType(ref)
    }

    /**
     * Get a function type
     *
     * This will construct a function types which returns the types provided in
     * [returns] which expects to receive parameters of the types provided in
     * [types]. You can mark a function types as variadic by setting the
     * [variadic] arg to true.
     *
     * @see LLVM.LLVMFunctionType
     */
    public fun getFunctionType(
        returns: Type,
        vararg arguments: Type,
        variadic: Boolean
    ): FunctionType {
        val ptr = arguments.map { it.ref }.toPointerPointer()
        val ref = LLVM.LLVMFunctionType(
            returns.ref,
            ptr,
            arguments.size,
            variadic.toLLVMBool()
        )

        ptr.deallocate()

        return FunctionType(ref)
    }

    /**
     * Create a string attribute
     *
     * @see LLVM.LLVMCreateStringAttribute
     */
    public fun createStringAttribute(
        kind: String,
        value: String
    ): StringAttribute {
        val ref = LLVM.LLVMCreateStringAttribute(
            ref,
            kind,
            kind.length,
            value,
            value.length
        )

        return StringAttribute(ref)
    }

    /**
     * Create an enum attribute
     *
     * @see LLVM.LLVMCreateEnumAttribute
     */
    public fun createEnumAttribute(
        kind: Int,
        value: Long
    ): EnumAttribute {
        val ref = LLVM.LLVMCreateEnumAttribute(ref, kind, value)

        return EnumAttribute(ref)
    }

    /**
     * Create a new basic block without inserting it into a function
     *
     * @see LLVM.LLVMCreateBasicBlockInContext
     */
    public fun createBasicBlock(name: String? = null): BasicBlock {
        val ref = LLVM.LLVMCreateBasicBlockInContext(ref, name)

        return BasicBlock(ref)
    }

    /**
     * Create a new IR builder
     *
     * @see LLVM.LLVMCreateBuilderInContext
     */
    public fun createBuilder(): Builder {
        val ref = LLVM.LLVMCreateBuilderInContext(ref)

        return Builder(ref)
    }

    /**
     * Create a constant struct of a list of values
     *
     * This is a so-called anonymous struct as its type is constructed from
     * the [values] passed.
     *
     * To create a ConstantStruct of an already existing Struct Type, use
     * [StructType.getConstant]
     *
     * @see LLVM.LLVMConstStructInContext
     */
    public fun createConstantStruct(
        vararg values: Value,
        packed: Boolean
    ): ConstantStruct {
        val ptr = values.map { it.ref }.toPointerPointer()
        val ref = LLVM.LLVMConstStructInContext(
            ref,
            ptr,
            values.size,
            packed.toLLVMBool()
        )

        ptr.deallocate()

        return ConstantStruct(ref)
    }

    public companion object {
        /**
         * Obtain the global LLVM context
         *
         * @see LLVM.LLVMGetGlobalContext
         */
        @JvmStatic
        public fun getGlobalContext(): Context {
            val ctx = LLVM.LLVMGetGlobalContext()

            return Context(ctx)
        }
    }

    public override fun dispose() {
        require(valid) { "Cannot dispose object twice" }

        valid = false

        LLVM.LLVMContextDispose(ref)
    }
}

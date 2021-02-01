package org.llvm4j.llvm4j

import org.bytedeco.javacpp.Pointer
import org.bytedeco.llvm.LLVM.LLVMContextRef
import org.bytedeco.llvm.LLVM.LLVMDiagnosticHandler
import org.bytedeco.llvm.LLVM.LLVMDiagnosticInfoRef
import org.bytedeco.llvm.LLVM.LLVMYieldCallback
import org.bytedeco.llvm.global.LLVM
import org.llvm4j.llvm4j.util.Callback
import org.llvm4j.llvm4j.util.CorrespondsTo
import org.llvm4j.llvm4j.util.None
import org.llvm4j.llvm4j.util.Option
import org.llvm4j.llvm4j.util.Owner
import org.llvm4j.llvm4j.util.Result
import org.llvm4j.llvm4j.util.Some
import org.llvm4j.llvm4j.util.toBoolean
import org.llvm4j.llvm4j.util.toInt
import org.llvm4j.llvm4j.util.toPointerPointer
import org.llvm4j.llvm4j.util.tryWith

/**
 * A context keeps the state a LLVM system requires.
 *
 * The context's data is local to a single thread and the context is not thread-safe. If compilation using multiple
 * threads is required, create a [Context] for each thread.
 *
 * The context is also a "super object" of sorts. Most state you can build in the LLVM system is built from this
 * object and these constructors are available on the [Context] class as methods.
 *
 * There is also a default, lazily initiated global context available.
 *
 * The context object is also a constructor to a lot of other elements in an LLVM system. These are exposed through
 * `newT` methods. All of these create new, unique values (not cached, like the `getT` methods) and are as following:
 *
 * - [newModule]
 * - [newBasicBlock]
 * - [newEnumAttribute]
 * - [newStringAttribute]
 *
 * TODO: LLVM 12.x - getScalableVectorType / LLVMScalableVectorType
 * TODO: LLVM 12.x - getTypeByName / LLVMGetTypeByName2
 * TODO: Research - Can we upcast LLVMDiagnosticHandler to Context.DiagnosticHandler to implement getDiagnosticHandler?
 * TODO: Testing - Can we reliably test callback methods? [YieldCallback], [DiagnosticHandler]
 * TODO: Testing - Test [getMetadataKindId] for Metadata
 * TODO: Testing - Is [getMetadataKindId] consistently testable across platforms?
 * TODO: Testing - Ensure [Instruction] discards value name once Instruction is stable
 *
 * @see GlobalContext
 *
 * @author Mats Larsen
 */
@CorrespondsTo("llvm::LLVMContext")
public open class Context public constructor(
    ptr: LLVMContextRef = LLVM.LLVMContextCreate()
) : Owner<LLVMContextRef> {
    public override val ref: LLVMContextRef = ptr

    public fun setDiagnosticHandler(handler: DiagnosticHandler, payload: Option<Pointer>) {
        LLVM.LLVMContextSetDiagnosticHandler(ref, handler, payload.toNullable())
    }

    public fun getDiagnosticPayload(): Option<Pointer> {
        val payload = LLVM.LLVMContextGetDiagnosticContext(ref)

        return payload?.let { Some(it) } ?: None
    }

    public fun setYieldCallback(handler: YieldCallback, payload: Option<Pointer>) {
        LLVM.LLVMContextSetYieldCallback(ref, handler, payload.toNullable())
    }

    public fun isDiscardingValueNames(): Boolean {
        return LLVM.LLVMContextShouldDiscardValueNames(ref).toBoolean()
    }

    public fun setDiscardingValueNames(isDiscarding: Boolean) {
        return LLVM.LLVMContextSetDiscardValueNames(ref, isDiscarding.toInt())
    }

    public fun getMetadataKindId(name: String): Int {
        return LLVM.LLVMGetMDKindIDInContext(ref, name, name.length)
    }

    public fun getIntegerType(bitWidth: Int): Result<IntegerType> = tryWith {
        assert(bitWidth in 1..8388606) { "Invalid integer bit width" }

        val intTy = LLVM.LLVMIntTypeInContext(ref, bitWidth)
        IntegerType(intTy)
    }

    public fun getInt1Type(): IntegerType {
        val ptr = LLVM.LLVMInt1TypeInContext(ref)

        return IntegerType(ptr)
    }

    public fun getInt8Type(): IntegerType {
        val ptr = LLVM.LLVMInt8TypeInContext(ref)

        return IntegerType(ptr)
    }

    public fun getInt16Type(): IntegerType {
        val ptr = LLVM.LLVMInt16TypeInContext(ref)

        return IntegerType(ptr)
    }

    public fun getInt32Type(): IntegerType {
        val ptr = LLVM.LLVMInt32TypeInContext(ref)

        return IntegerType(ptr)
    }

    public fun getInt64Type(): IntegerType {
        val ptr = LLVM.LLVMInt64TypeInContext(ref)

        return IntegerType(ptr)
    }

    public fun getInt128Type(): IntegerType {
        val ptr = LLVM.LLVMInt128TypeInContext(ref)

        return IntegerType(ptr)
    }

    public fun getFunctionType(returnType: Type, vararg parameters: Type, isVariadic: Boolean = false): FunctionType {
        val buffer = parameters.map { it.ref }.toPointerPointer()
        val fnTy = LLVM.LLVMFunctionType(returnType.ref, buffer, parameters.size, isVariadic.toInt())

        buffer.deallocate()

        return FunctionType(fnTy)
    }

    public fun getStructType(vararg elements: Type, isPacked: Boolean = false): StructType {
        val buffer = elements.map { it.ref }.toPointerPointer()
        val struct = LLVM.LLVMStructTypeInContext(ref, buffer, elements.size, isPacked.toInt())

        buffer.deallocate()

        return StructType(struct)
    }

    public fun getNamedStructType(name: String): NamedStructType {
        val struct = LLVM.LLVMStructCreateNamed(ref, name)

        return NamedStructType(struct)
    }

    public fun getVoidType(): VoidType {
        val ptr = LLVM.LLVMVoidTypeInContext(ref)

        return VoidType(ptr)
    }

    public fun getFloatType(): FloatingPointType {
        val ptr = LLVM.LLVMFloatTypeInContext(ref)

        return FloatingPointType(ptr)
    }

    public fun getBFloatType(): FloatingPointType {
        val ptr = LLVM.LLVMBFloatTypeInContext(ref)

        return FloatingPointType(ptr)
    }

    public fun getHalfType(): FloatingPointType {
        val ptr = LLVM.LLVMHalfTypeInContext(ref)

        return FloatingPointType(ptr)
    }

    public fun getDoubleType(): FloatingPointType {
        val ptr = LLVM.LLVMFloatTypeInContext(ref)

        return FloatingPointType(ptr)
    }

    public fun getX86FP80Type(): FloatingPointType {
        val ptr = LLVM.LLVMX86FP80TypeInContext(ref)

        return FloatingPointType(ptr)
    }

    public fun getFP128Type(): FloatingPointType {
        val ptr = LLVM.LLVMFP128TypeInContext(ref)

        return FloatingPointType(ptr)
    }

    public fun getPPCFP128Type(): FloatingPointType {
        val ptr = LLVM.LLVMPPCFP128TypeInContext(ref)

        return FloatingPointType(ptr)
    }

    public fun getArrayType(of: Type, size: Int): Result<ArrayType> = tryWith {
        assert(size > 0) { "Element count must be greater than 0" }
        assert(of.isValidArrayElementType()) { "Invalid type for array element" }

        val arrayTy = LLVM.LLVMArrayType(of.ref, size)
        ArrayType(arrayTy)
    }

    public fun getVectorType(of: Type, size: Int): Result<VectorType> = tryWith {
        assert(size > 0) { "Element count must be greater than 0" }
        assert(of.isValidVectorElementType()) { "Invalid type for vector element" }

        val vecTy = LLVM.LLVMVectorType(of.ref, size)
        VectorType(vecTy)
    }

    public fun getPointerType(
        of: Type,
        addressSpace: AddressSpace = AddressSpace.Generic
    ): Result<PointerType> = tryWith {
        assert(addressSpace.value >= 0) { "Address space must be a positive number" }
        assert(of.isValidPointerElementType()) { "Invalid type for pointer element" }

        val ptrTy = LLVM.LLVMPointerType(of.ref, addressSpace.value)
        PointerType(ptrTy)
    }

    public fun getX86MMXType(): X86MMXType {
        val ptr = LLVM.LLVMX86MMXTypeInContext(ref)

        return X86MMXType(ptr)
    }

    public fun getLabelType(): LabelType {
        val ptr = LLVM.LLVMLabelTypeInContext(ref)

        return LabelType(ptr)
    }

    public fun getMetadataType(): MetadataType {
        val ptr = LLVM.LLVMMetadataTypeInContext(ref)

        return MetadataType(ptr)
    }

    public fun getTokenType(): TokenType {
        val ptr = LLVM.LLVMTokenTypeInContext(ref)

        return TokenType(ptr)
    }

    public fun newEnumAttribute(kindId: Int, value: Long): Attribute {
        val attr = LLVM.LLVMCreateEnumAttribute(ref, kindId, value)

        return Attribute(attr)
    }

    public fun newStringAttribute(kindId: String, value: String): Attribute {
        val attr = LLVM.LLVMCreateStringAttribute(ref, kindId, kindId.length, value, value.length)

        return Attribute(attr)
    }

    public fun newModule(name: String): Module {
        val mod = LLVM.LLVMModuleCreateWithNameInContext(name, ref)

        return Module(mod)
    }

    public fun getConstantString(string: String, nullTerminate: Boolean): ConstantDataArray {
        val cds = LLVM.LLVMConstStringInContext(ref, string, string.length, nullTerminate.toInt())

        return ConstantDataArray(cds)
    }

    public fun getMetadataString(value: String): MetadataString {
        val node = LLVM.LLVMMDStringInContext2(ref, value, value.length.toLong())

        return MetadataString(node)
    }

    public fun getMetadataNode(vararg values: Metadata): MetadataNode {
        val buffer = values.map { it.ref }.toPointerPointer()
        val node = LLVM.LLVMMDNodeInContext2(ref, buffer, values.size.toLong())

        buffer.deallocate()

        return MetadataNode(node)
    }

    public fun newBasicBlock(name: String): BasicBlock {
        val bb = LLVM.LLVMCreateBasicBlockInContext(ref, name)

        return BasicBlock(bb)
    }

    public class DiagnosticHandler(private val closure: (Payload) -> Unit) :
        LLVMDiagnosticHandler(),
        Callback<Unit, DiagnosticHandler.Payload> {
        public override fun invoke(ctx: Payload): Unit = closure(ctx)

        public override fun call(p0: LLVMDiagnosticInfoRef, p1: Pointer?) {
            val info = DiagnosticInfo(p0)
            val payload = p1?.let { Some(it) } ?: None
            val data = Payload(info, payload)

            return invoke(data)
        }

        public data class Payload(
            public val info: DiagnosticInfo,
            public val payload: Option<Pointer>
        )
    }

    public class YieldCallback(private val closure: (Payload) -> Unit) :
        LLVMYieldCallback(),
        Callback<Unit, YieldCallback.Payload> {
        public override fun invoke(ctx: Payload): Unit = closure(ctx)

        public override fun call(p0: LLVMContextRef, p1: Pointer?) {
            val context = Context(p0)
            val payload = p1?.let { Some(it) } ?: None
            val data = Payload(context, payload)

            return invoke(data)
        }

        public data class Payload(
            public val context: Context,
            public val payload: Option<Pointer>
        )
    }

    public override fun deallocate() {
        LLVM.LLVMContextDispose(ref)
    }
}

/**
 * The default global LLVM context
 *
 * @author Mats Larsen
 */
public object GlobalContext : Context(LLVM.LLVMGetGlobalContext())

/**
 * A diagnostic report by the LLVM backend, provided through [Context.setDiagnosticHandler]
 *
 * @author Mats Larsen
 */
public class DiagnosticInfo public constructor(ptr: LLVMDiagnosticInfoRef) : Owner<LLVMDiagnosticInfoRef> {
    public override val ref: LLVMDiagnosticInfoRef = ptr

    public fun getDescription(): String {
        val ptr = LLVM.LLVMGetDiagInfoDescription(ref)
        val copy = ptr.string

        ptr.deallocate()

        return copy
    }

    public fun getSeverity(): DiagnosticSeverity {
        val severity = LLVM.LLVMGetDiagInfoSeverity(ref)

        return DiagnosticSeverity.from(severity).get()
    }
}

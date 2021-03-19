package org.llvm4j.llvm4j

import org.bytedeco.javacpp.BytePointer
import org.bytedeco.javacpp.Pointer
import org.bytedeco.llvm.LLVM.LLVMMemoryBufferRef
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM
import org.llvm4j.llvm4j.util.CorrespondsTo
import org.llvm4j.llvm4j.util.Owner
import org.llvm4j.optional.Option
import org.llvm4j.optional.Result
import org.llvm4j.optional.result
import java.io.File

/**
 * A readonly, null-terminated string owned by LLVM, known as a Message in the C API
 *
 * Do not use this constructor for [BytePointer]s which were not created as a LLVM owned string. If you need to
 * create an instance of [LLVMString], use [LLVMString.of]
 *
 * @author Mats Larsen
 */
public class LLVMString public constructor(ptr: BytePointer) : Owner<BytePointer> {
    public override val ref: BytePointer = ptr

    /**
     * Copies the content of the string.
     */
    public fun getString(): String = ref.string

    public override fun deallocate() {
        LLVM.LLVMDisposeMessage(ref)
    }

    public companion object {
        @JvmStatic
        public fun of(message: String): LLVMString {
            val ptr = BytePointer(message)
            val string = LLVM.LLVMCreateMessage(ptr)

            ptr.deallocate()

            return LLVMString(string)
        }
    }
}

/**
 * A memory buffer is a simple, read-only access to a block of memory. The memory buffer is always null terminated
 *
 * Creation of a memory buffer is usually done with a file.
 *
 * TODO: Research - Can we use stdin creation? Does this even make sense?
 *
 * @author Mats Larsen
 */
@CorrespondsTo("llvm::MemoryBuffer")
public class MemoryBuffer public constructor(ptr: LLVMMemoryBufferRef) : Owner<LLVMMemoryBufferRef> {
    public override val ref: LLVMMemoryBufferRef = ptr

    public fun getSize(): Long {
        return LLVM.LLVMGetBufferSize(ref)
    }

    /**
     * Get a pointer to the start of the buffer. This returns a byte pointer which can be indexed all the way through
     * [getSize] - 1.
     */
    public fun getStartPointer(): BytePointer {
        return LLVM.LLVMGetBufferStart(ref)
    }

    public fun getString(): String {
        return getStartPointer().string
    }

    public override fun deallocate() {
        LLVM.LLVMDisposeMemoryBuffer(ref)
    }

    public companion object {
        @JvmStatic
        public fun of(file: File): Result<MemoryBuffer, AssertionError> = result {
            assert(file.exists()) { "File '$file' does not exist" }

            val fp = BytePointer(file.absolutePath)
            val err = BytePointer(256)
            val buf = LLVMMemoryBufferRef()
            val code = LLVM.LLVMCreateMemoryBufferWithContentsOfFile(fp, buf, err)

            fp.deallocate()

            assert(code == 0) {
                val c = err.string
                err.deallocate()
                buf.deallocate()
                c
            }

            MemoryBuffer(buf)
        }
    }
}

/**
 * Facade for performing nice casting through Kotlin generics
 *
 * @author Mats Larsen
 */
public object TypeCasting {
    /**
     * Create a casting path from Owner type [F] to Pointer type [T]
     *
     * This is a helper function to map paths in the [directions] map
     */
    private inline fun <reified F, T : Pointer> path(noinline conversion: (T) -> F): Pair<Class<F>, (T) -> F> {
        return F::class.java to conversion
    }

    public val directions: Map<Class<*>, Any> = mapOf(
        path<Type, LLVMTypeRef>(::Type),
        path<IntegerType, LLVMTypeRef>(::IntegerType),
    )

    private inline fun <reified F> assertion(
        noinline assertion: (LLVMValueRef) -> LLVMValueRef?
    ): Pair<Class<F>, (LLVMValueRef) -> LLVMValueRef?> {
        return F::class.java to assertion
    }

    public val assertions: Map<Class<*>, (LLVMValueRef) -> LLVMValueRef?> = mapOf(
        assertion<Argument> { LLVM.LLVMIsAArgument(it) },
        assertion<BasicBlock> { LLVM.LLVMIsABasicBlock(it) },
        // InlineAsm
        assertion<User> { LLVM.LLVMIsAUser(it) },
        assertion<Constant> { LLVM.LLVMIsAConstant(it) },
        assertion<BlockAddress> { LLVM.LLVMIsABlockAddress(it) },
        assertion<ConstantAggregateZero> { LLVM.LLVMIsAConstantAggregateZero(it) },
        assertion<ConstantArray> { LLVM.LLVMIsAConstantArray(it) },
        assertion<ConstantDataSequential> { LLVM.LLVMIsAConstantDataSequential(it) },
        assertion<ConstantDataArray> { LLVM.LLVMIsAConstantDataArray(it) },
        assertion<ConstantDataVector> { LLVM.LLVMIsAConstantDataVector(it) },
        assertion<ConstantExpression> { LLVM.LLVMIsAConstantExpr(it) },
        assertion<ConstantFP> { LLVM.LLVMIsAConstantFP(it) },
        assertion<ConstantInt> { LLVM.LLVMIsAConstantInt(it) },
        assertion<ConstantPointerNull> { LLVM.LLVMIsAConstantPointerNull(it) },
        assertion<ConstantStruct> { LLVM.LLVMIsAConstantStruct(it) },
        assertion<ConstantTokenNone> { LLVM.LLVMIsAConstantTokenNone(it) },
        assertion<ConstantVector> { LLVM.LLVMIsAConstantVector(it) },
        assertion<GlobalValue> { LLVM.LLVMIsAGlobalValue(it) },
        assertion<GlobalAlias> { LLVM.LLVMIsAGlobalAlias(it) },
        assertion<GlobalIndirectFunction> { LLVM.LLVMIsAGlobalIFunc(it) },
        assertion<GlobalObject> { LLVM.LLVMIsAGlobalObject(it) },
        assertion<Function> { LLVM.LLVMIsAFunction(it) },
        assertion<GlobalVariable> { LLVM.LLVMIsAGlobalVariable(it) },
        assertion<UndefValue> { LLVM.LLVMIsAUndefValue(it) },
        assertion<Instruction> { LLVM.LLVMIsAInstruction(it) },
        assertion<UnaryOperator> { LLVM.LLVMIsAUnaryOperator(it) },
        assertion<BinaryOperatorInstruction> { LLVM.LLVMIsABinaryOperator(it) },
        assertion<CallInstruction> { LLVM.LLVMIsACallInst(it) },
        // IntrinsicInst
        // DebugInfoIntrinsic
        // DebugVariableIntrinsic
        // DebugDeclareInst
        // DebugLabelInst
        // MemIntrinsic
        // MemCpyInst
        // MemMoveInst
        // MemSetInst
        assertion<ComparisonInstruction> { LLVM.LLVMIsACmpInst(it) },
        assertion<FPComparisonInstruction> { LLVM.LLVMIsAFCmpInst(it) },
        assertion<IntComparisonInstruction> { LLVM.LLVMIsAICmpInst(it) },
        assertion<ExtractElementInstruction> { LLVM.LLVMIsAExtractElementInst(it) },
        assertion<GetElementPtrInstruction> { LLVM.LLVMIsAGetElementPtrInst(it) },
        assertion<InsertElementInstruction> { LLVM.LLVMIsAInsertElementInst(it) },
        assertion<InsertValueInstruction> { LLVM.LLVMIsAInsertValueInst(it) },
        assertion<LandingPadInstruction> { LLVM.LLVMIsALandingPadInst(it) },
        assertion<PhiInstruction> { LLVM.LLVMIsAPHINode(it) },
        assertion<SelectInstruction> { LLVM.LLVMIsASelectInst(it) },
        assertion<ShuffleVectorInstruction> { LLVM.LLVMIsAShuffleVectorInst(it) },
        assertion<StoreInstruction> { LLVM.LLVMIsAStoreInst(it) },
        assertion<BranchInstruction> { LLVM.LLVMIsABranchInst(it) },
        assertion<IndirectBrInstruction> { LLVM.LLVMIsAIndirectBrInst(it) },
        assertion<InvokeInstruction> { LLVM.LLVMIsAInvokeInst(it) },
        assertion<ReturnInstruction> { LLVM.LLVMIsAReturnInst(it) },
        assertion<SwitchInstruction> { LLVM.LLVMIsASwitchInst(it) },
        assertion<UnreachableInstruction> { LLVM.LLVMIsAUnreachableInst(it) },
        assertion<ResumeInstruction> { LLVM.LLVMIsAResumeInst(it) },
        assertion<CleanupReturnInstruction> { LLVM.LLVMIsACleanupReturnInst(it) },
        assertion<CatchReturnInstruction> { LLVM.LLVMIsACatchReturnInst(it) },
        assertion<CatchSwitchInstruction> { LLVM.LLVMIsACatchSwitchInst(it) },
        assertion<CallBrInstruction> { LLVM.LLVMIsACallBrInst(it) },
        assertion<FuncletPadInstruction> { LLVM.LLVMIsAFuncletPadInst(it) },
        assertion<CatchPadInstruction> { LLVM.LLVMIsACatchPadInst(it) },
        assertion<CleanupPadInstruction> { LLVM.LLVMIsACleanupPadInst(it) },
        assertion<UnaryInstruction> { LLVM.LLVMIsAUnaryInstruction(it) },
        assertion<AllocaInstruction> { LLVM.LLVMIsAAllocaInst(it) },
        assertion<CastInstruction> { LLVM.LLVMIsACastInst(it) },
        assertion<AddrSpaceCastInstruction> { LLVM.LLVMIsAAddrSpaceCastInst(it) },
        assertion<BitCastInstruction> { LLVM.LLVMIsABitCastInst(it) },
        assertion<FloatExtInstruction> { LLVM.LLVMIsAFPExtInst(it) },
        assertion<FloatToSignedInstruction> { LLVM.LLVMIsAFPToSIInst(it) },
        assertion<FloatToUnsignedInstruction> { LLVM.LLVMIsAFPToUIInst(it) },
        assertion<FloatTruncInstruction> { LLVM.LLVMIsAFPTruncInst(it) },
        assertion<IntToPtrInstruction> { LLVM.LLVMIsAIntToPtrInst(it) },
        assertion<PtrToIntInstruction> { LLVM.LLVMIsAPtrToIntInst(it) },
        assertion<SignedExtInstruction> { LLVM.LLVMIsASExtInst(it) },
        assertion<SignedToFloatInstruction> { LLVM.LLVMIsASIToFPInst(it) },
        assertion<IntTruncInstruction> { LLVM.LLVMIsATruncInst(it) },
        assertion<UnsignedToFloatInstruction> { LLVM.LLVMIsAUIToFPInst(it) },
        assertion<ZeroExtInstruction> { LLVM.LLVMIsAZExtInst(it) },
        assertion<ExtractValueInstruction> { LLVM.LLVMIsAExtractElementInst(it) },
        assertion<LoadInstruction> { LLVM.LLVMIsALoadInst(it) },
        assertion<VAArgInstruction> { LLVM.LLVMIsAVAArgInst(it) },
        assertion<FreezeInstruction> { LLVM.LLVMIsAFreezeInst(it) },
        assertion<AtomicCmpXchgInstruction> { LLVM.LLVMIsAAtomicCmpXchgInst(it) },
        assertion<AtomicRMWInstruction> { LLVM.LLVMIsAAtomicRMWInst(it) },
        assertion<FenceInstruction> { LLVM.LLVMIsAFenceInst(it) },
        assertion<MetadataNode> { LLVM.LLVMIsAMDNode(it) },
        assertion<MetadataString> { LLVM.LLVMIsAMDString(it) }
    )
}

/**
 * Perform a cast from a given Owner to [T] if possible
 *
 * This looks through the TypeCasting facade's directions and determines whether the conversion between the two types
 * are legal. If it is, the owner is "cast" by passing its ref into T's conversion function and Some(T) is returned.
 *
 * This is a Kotlin-only API.
 *
 * @author Mats Larsen
 */
@JvmSynthetic
public inline fun <reified T> cast(from: Owner<*>): Option<T> {
    val conversion = TypeCasting.directions[T::class.java]

    @Suppress("UNCHECKED_CAST")
    val result = (conversion as? (Any) -> Owner<*>)?.invoke(from.ref) as? T

    return Option.of(result)
}

@JvmSynthetic
public inline fun <reified T> cast(from: Owner<*>, default: T): T {
    return cast<T>(from).toNullable() ?: default
}

@JvmSynthetic
public inline fun <reified T> cast(from: Owner<*>, default: () -> T): T {
    return cast<T>(from).toNullable() ?: default.invoke()
}

@JvmSynthetic
public inline fun <reified T> isa(from: Owner<LLVMValueRef>): Boolean {
    val assertion = TypeCasting.assertions[T::class.java]

    return assertion?.let {
        it(from.ref) != null
    } ?: false
}
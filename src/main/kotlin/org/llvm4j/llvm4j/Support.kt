package org.llvm4j.llvm4j

import org.bytedeco.javacpp.BytePointer
import org.bytedeco.llvm.LLVM.LLVMMemoryBufferRef
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM
import org.llvm4j.llvm4j.util.CorrespondsTo
import org.llvm4j.llvm4j.util.Owner
import org.llvm4j.optional.None
import org.llvm4j.optional.Option
import org.llvm4j.optional.Result
import org.llvm4j.optional.Some
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
    private inline fun <reified F> conversion(
        noinline conversion: (LLVMValueRef) -> F,
        noinline assertion: (LLVMValueRef) -> LLVMValueRef?
    ): Pair<Class<F>, Pair<(LLVMValueRef) -> F, (LLVMValueRef) -> LLVMValueRef?>> {
        return F::class.java to (conversion to assertion)
    }

    /**
     * This map contains the valid casts/conversions between LLVM values.
     *
     * Each of the [CorrespondsTo] types from llvm4j correspond to a LLVM value subclass which
     * it can be cast into.
     *
     * All casts are possible because any constructor accepts LLVMValueRef, but that's unsafe because you could
     * suddenly pass a Function into an Instruction and that'd be beyond illegal. This map proves you're only
     * allowed to convert between the allowed types.
     *
     * If a value isa<T>, then it can be cast to T.
     */
    public val conversions: Map<Class<*>, Pair<(LLVMValueRef) -> Any, (LLVMValueRef) -> LLVMValueRef?>> = mapOf(
        conversion(::Argument) { LLVM.LLVMIsAArgument(it) },
        conversion(::BasicBlockAsValue) { LLVM.LLVMIsABasicBlock(it) },
        // InlineAsm
        conversion(::User) { LLVM.LLVMIsAUser(it) },
        conversion(::Constant) { LLVM.LLVMIsAConstant(it) },
        conversion(::BlockAddress) { LLVM.LLVMIsABlockAddress(it) },
        conversion(::ConstantAggregateZero) { LLVM.LLVMIsAConstantAggregateZero(it) },
        conversion(::ConstantArray) { LLVM.LLVMIsAConstantArray(it) },
        conversion(::ConstantDataSequential) { LLVM.LLVMIsAConstantDataSequential(it) },
        conversion(::ConstantDataArray) { LLVM.LLVMIsAConstantDataArray(it) },
        conversion(::ConstantDataVector) { LLVM.LLVMIsAConstantDataVector(it) },
        conversion(::ConstantExpression) { LLVM.LLVMIsAConstantExpr(it) },
        conversion(::ConstantFP) { LLVM.LLVMIsAConstantFP(it) },
        conversion(::ConstantInt) { LLVM.LLVMIsAConstantInt(it) },
        conversion(::ConstantPointerNull) { LLVM.LLVMIsAConstantPointerNull(it) },
        conversion(::ConstantStruct) { LLVM.LLVMIsAConstantStruct(it) },
        conversion(::ConstantTokenNone) { LLVM.LLVMIsAConstantTokenNone(it) },
        conversion(::ConstantVector) { LLVM.LLVMIsAConstantVector(it) },
        conversion(::GlobalValue) { LLVM.LLVMIsAGlobalValue(it) },
        conversion(::GlobalAlias) { LLVM.LLVMIsAGlobalAlias(it) },
        conversion(::GlobalIndirectFunction) { LLVM.LLVMIsAGlobalIFunc(it) },
        conversion(::GlobalObject) { LLVM.LLVMIsAGlobalObject(it) },
        conversion(::Function) { LLVM.LLVMIsAFunction(it) },
        conversion(::GlobalVariable) { LLVM.LLVMIsAGlobalVariable(it) },
        conversion(::UndefValue) { LLVM.LLVMIsAUndefValue(it) },
        conversion(::Instruction) { LLVM.LLVMIsAInstruction(it) },
        conversion(::UnaryOperator) { LLVM.LLVMIsAUnaryOperator(it) },
        conversion(::BinaryOperatorInstruction) { LLVM.LLVMIsABinaryOperator(it) },
        conversion(::CallInstruction) { LLVM.LLVMIsACallInst(it) },
        // IntrinsicInst
        // DebugInfoIntrinsic
        // DebugVariableIntrinsic
        // DebugDeclareInst
        // DebugLabelInst
        // MemIntrinsic
        // MemCpyInst
        // MemMoveInst
        // MemSetInst
        conversion(::ComparisonInstruction) { LLVM.LLVMIsACmpInst(it) },
        conversion(::FPComparisonInstruction) { LLVM.LLVMIsAFCmpInst(it) },
        conversion(::IntComparisonInstruction) { LLVM.LLVMIsAICmpInst(it) },
        conversion(::ExtractElementInstruction) { LLVM.LLVMIsAExtractElementInst(it) },
        conversion(::GetElementPtrInstruction) { LLVM.LLVMIsAGetElementPtrInst(it) },
        conversion(::InsertElementInstruction) { LLVM.LLVMIsAInsertElementInst(it) },
        conversion(::InsertValueInstruction) { LLVM.LLVMIsAInsertValueInst(it) },
        conversion(::LandingPadInstruction) { LLVM.LLVMIsALandingPadInst(it) },
        conversion(::PhiInstruction) { LLVM.LLVMIsAPHINode(it) },
        conversion(::SelectInstruction) { LLVM.LLVMIsASelectInst(it) },
        conversion(::ShuffleVectorInstruction) { LLVM.LLVMIsAShuffleVectorInst(it) },
        conversion(::StoreInstruction) { LLVM.LLVMIsAStoreInst(it) },
        conversion(::BranchInstruction) { LLVM.LLVMIsABranchInst(it) },
        conversion(::IndirectBrInstruction) { LLVM.LLVMIsAIndirectBrInst(it) },
        conversion(::InvokeInstruction) { LLVM.LLVMIsAInvokeInst(it) },
        conversion(::ReturnInstruction) { LLVM.LLVMIsAReturnInst(it) },
        conversion(::SwitchInstruction) { LLVM.LLVMIsASwitchInst(it) },
        conversion(::UnreachableInstruction) { LLVM.LLVMIsAUnreachableInst(it) },
        conversion(::ResumeInstruction) { LLVM.LLVMIsAResumeInst(it) },
        conversion(::CleanupReturnInstruction) { LLVM.LLVMIsACleanupReturnInst(it) },
        conversion(::CatchReturnInstruction) { LLVM.LLVMIsACatchReturnInst(it) },
        conversion(::CatchSwitchInstruction) { LLVM.LLVMIsACatchSwitchInst(it) },
        conversion(::CallBrInstruction) { LLVM.LLVMIsACallBrInst(it) },
        conversion(::FuncletPadInstruction) { LLVM.LLVMIsAFuncletPadInst(it) },
        conversion(::CatchPadInstruction) { LLVM.LLVMIsACatchPadInst(it) },
        conversion(::CleanupPadInstruction) { LLVM.LLVMIsACleanupPadInst(it) },
        conversion(::UnaryInstruction) { LLVM.LLVMIsAUnaryInstruction(it) },
        conversion(::AllocaInstruction) { LLVM.LLVMIsAAllocaInst(it) },
        conversion(::CastInstruction) { LLVM.LLVMIsACastInst(it) },
        conversion(::AddrSpaceCastInstruction) { LLVM.LLVMIsAAddrSpaceCastInst(it) },
        conversion(::BitCastInstruction) { LLVM.LLVMIsABitCastInst(it) },
        conversion(::FloatExtInstruction) { LLVM.LLVMIsAFPExtInst(it) },
        conversion(::FloatToSignedInstruction) { LLVM.LLVMIsAFPToSIInst(it) },
        conversion(::FloatToUnsignedInstruction) { LLVM.LLVMIsAFPToUIInst(it) },
        conversion(::FloatTruncInstruction) { LLVM.LLVMIsAFPTruncInst(it) },
        conversion(::IntToPtrInstruction) { LLVM.LLVMIsAIntToPtrInst(it) },
        conversion(::PtrToIntInstruction) { LLVM.LLVMIsAPtrToIntInst(it) },
        conversion(::SignedExtInstruction) { LLVM.LLVMIsASExtInst(it) },
        conversion(::SignedToFloatInstruction) { LLVM.LLVMIsASIToFPInst(it) },
        conversion(::IntTruncInstruction) { LLVM.LLVMIsATruncInst(it) },
        conversion(::UnsignedToFloatInstruction) { LLVM.LLVMIsAUIToFPInst(it) },
        conversion(::ZeroExtInstruction) { LLVM.LLVMIsAZExtInst(it) },
        conversion(::ExtractValueInstruction) { LLVM.LLVMIsAExtractElementInst(it) },
        conversion(::LoadInstruction) { LLVM.LLVMIsALoadInst(it) },
        conversion(::VAArgInstruction) { LLVM.LLVMIsAVAArgInst(it) },
        conversion(::FreezeInstruction) { LLVM.LLVMIsAFreezeInst(it) },
        conversion(::AtomicCmpXchgInstruction) { LLVM.LLVMIsAAtomicCmpXchgInst(it) },
        conversion(::AtomicRMWInstruction) { LLVM.LLVMIsAAtomicRMWInst(it) },
        conversion(::FenceInstruction) { LLVM.LLVMIsAFenceInst(it) }
    )

    public fun <T> isa(cls: Class<T>, from: Owner<LLVMValueRef>): Boolean {
        return conversions[cls]?.let {
            // Second pair in tuple is assertion
            it.second(from.ref) != null
        } ?: false
    }

    public fun <T> dyncast(cls: Class<T>, from: Owner<LLVMValueRef>): Option<T> {
        val (conversion, assertion) = conversions[cls] ?: return None
        // If the cast is legal
        @Suppress("UNCHECKED_CAST")
        if (assertion(from.ref) != null) {
            val res = conversion as (LLVMValueRef) -> Owner<LLVMValueRef>
            return Some(res.invoke(from.ref) as T)
        }

        return None
    }

    public fun <T> cast(cls: Class<T>, from: Owner<LLVMValueRef>): T {
        return dyncast(cls, from).toNullable() ?: throw ClassCastException(
            "Illegal cast from ${from::class.java.simpleName} to ${cls.simpleName}"
        )
    }
}

@JvmSynthetic
public inline fun <reified T> isa(from: Owner<LLVMValueRef>): Boolean = TypeCasting.isa(T::class.java, from)

@JvmSynthetic
public inline fun <reified T> dyncast(from: Owner<LLVMValueRef>): Option<T> = TypeCasting.dyncast(T::class.java, from)

@JvmSynthetic
public inline fun <reified T> cast(from: Owner<LLVMValueRef>): T = TypeCasting.cast(T::class.java, from)
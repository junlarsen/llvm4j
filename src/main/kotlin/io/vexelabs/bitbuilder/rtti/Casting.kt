package io.vexelabs.bitbuilder.rtti

import io.vexelabs.bitbuilder.llvm.internal.contracts.ContainsReference
import io.vexelabs.bitbuilder.llvm.ir.Instruction
import io.vexelabs.bitbuilder.llvm.ir.User
import io.vexelabs.bitbuilder.llvm.ir.instructions.AllocaInstruction
import io.vexelabs.bitbuilder.llvm.ir.instructions.AtomicCmpXchgInstruction
import io.vexelabs.bitbuilder.llvm.ir.instructions.AtomicRMWInstruction
import io.vexelabs.bitbuilder.llvm.ir.instructions.BrInstruction
import io.vexelabs.bitbuilder.llvm.ir.instructions.CallBrInstruction
import io.vexelabs.bitbuilder.llvm.ir.instructions.CallInstruction
import io.vexelabs.bitbuilder.llvm.ir.instructions.CatchPadInstruction
import io.vexelabs.bitbuilder.llvm.ir.instructions.CatchRetInstruction
import io.vexelabs.bitbuilder.llvm.ir.instructions.CatchSwitchInstruction
import io.vexelabs.bitbuilder.llvm.ir.instructions.CleanupPadInstruction
import io.vexelabs.bitbuilder.llvm.ir.instructions.CleanupRetInstruction
import io.vexelabs.bitbuilder.llvm.ir.instructions.FenceInstruction
import io.vexelabs.bitbuilder.llvm.ir.instructions.IndirectBrInstruction
import io.vexelabs.bitbuilder.llvm.ir.instructions.InvokeInstruction
import io.vexelabs.bitbuilder.llvm.ir.instructions.LandingPadInstruction
import io.vexelabs.bitbuilder.llvm.ir.instructions.LoadInstruction
import io.vexelabs.bitbuilder.llvm.ir.instructions.PhiInstruction
import io.vexelabs.bitbuilder.llvm.ir.instructions.ResumeInstruction
import io.vexelabs.bitbuilder.llvm.ir.instructions.RetInstruction
import io.vexelabs.bitbuilder.llvm.ir.instructions.SelectInstruction
import io.vexelabs.bitbuilder.llvm.ir.instructions.StoreInstruction
import io.vexelabs.bitbuilder.llvm.ir.instructions.SwitchInstruction
import io.vexelabs.bitbuilder.llvm.ir.instructions.UnreachableInstruction
import io.vexelabs.bitbuilder.llvm.ir.instructions.VAArgInstruction
import io.vexelabs.bitbuilder.llvm.ir.types.ArrayType
import io.vexelabs.bitbuilder.llvm.ir.types.FloatType
import io.vexelabs.bitbuilder.llvm.ir.types.FunctionType
import io.vexelabs.bitbuilder.llvm.ir.types.IntType
import io.vexelabs.bitbuilder.llvm.ir.types.LabelType
import io.vexelabs.bitbuilder.llvm.ir.types.MetadataType
import io.vexelabs.bitbuilder.llvm.ir.types.PointerType
import io.vexelabs.bitbuilder.llvm.ir.types.StructType
import io.vexelabs.bitbuilder.llvm.ir.types.TokenType
import io.vexelabs.bitbuilder.llvm.ir.types.VectorType
import io.vexelabs.bitbuilder.llvm.ir.types.VoidType
import io.vexelabs.bitbuilder.llvm.ir.types.X86MMXType
import io.vexelabs.bitbuilder.llvm.ir.values.ConstantValue
import io.vexelabs.bitbuilder.llvm.ir.values.FunctionValue
import io.vexelabs.bitbuilder.llvm.ir.values.GlobalAlias
import io.vexelabs.bitbuilder.llvm.ir.values.GlobalValue
import io.vexelabs.bitbuilder.llvm.ir.values.GlobalVariable
import io.vexelabs.bitbuilder.llvm.ir.values.IndirectFunction
import io.vexelabs.bitbuilder.llvm.ir.values.constants.ConstantArray
import io.vexelabs.bitbuilder.llvm.ir.values.constants.ConstantFloat
import io.vexelabs.bitbuilder.llvm.ir.values.constants.ConstantInt
import io.vexelabs.bitbuilder.llvm.ir.values.constants.ConstantPointer
import io.vexelabs.bitbuilder.llvm.ir.values.constants.ConstantStruct
import io.vexelabs.bitbuilder.llvm.ir.values.constants.ConstantVector
import org.bytedeco.javacpp.Pointer
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.LLVM.LLVMValueRef

/**
 * Helper function to pull the [java.lang.Class] from [T] as well as
 * scaffolding the function which will transform [P] into [T] into a [Pair]
 * to be inserted into [constructorMap]
 */
public inline fun <reified T, P : Pointer> path(
    noinline generator: (P) -> T
): Pair<Class<*>, (P) -> T> {
    val clz = T::class.java

    return clz to generator
}

/**
 * A map listing all the types which can be converted from which, and which
 * function will be capable of doing the transformation
 *
 * @see cast
 * @see path
 *
 * TODO: This Map type is very hacky, should replace Any with an actual
 *   function type.
 */
public val constructorMap: Map<Class<*>, Any> =
    mapOf(
    path<FunctionValue, LLVMValueRef>(::FunctionValue),
    path<IndirectFunction, LLVMValueRef>(::IndirectFunction),
    path<GlobalValue, LLVMValueRef>(::GlobalValue),
    path<GlobalVariable, LLVMValueRef>(::GlobalVariable),
    path<GlobalAlias, LLVMValueRef>(::GlobalAlias),
    // Constant Values
    path<ConstantValue, LLVMValueRef>(::ConstantValue),
    path<ConstantInt, LLVMValueRef>(::ConstantInt),
    path<ConstantFloat, LLVMValueRef>(::ConstantFloat),
    path<ConstantPointer, LLVMValueRef>(::ConstantPointer),
    path<ConstantStruct, LLVMValueRef>(::ConstantStruct),
    path<ConstantVector, LLVMValueRef>(::ConstantVector),
    path<ConstantArray, LLVMValueRef>(::ConstantArray),
    // Types
    path<IntType, LLVMTypeRef>(::IntType),
    path<FloatType, LLVMTypeRef>(::FloatType),
    path<FunctionType, LLVMTypeRef>(::FunctionType),
    path<ArrayType, LLVMTypeRef>(::ArrayType),
    path<VectorType, LLVMTypeRef>(::VectorType),
    path<StructType, LLVMTypeRef>(::StructType),
    path<PointerType, LLVMTypeRef>(::PointerType),
    path<LabelType, LLVMTypeRef>(::LabelType),
    path<MetadataType, LLVMTypeRef>(::MetadataType),
    path<TokenType, LLVMTypeRef>(::TokenType),
    path<VoidType, LLVMTypeRef>(::VoidType),
    path<X86MMXType, LLVMTypeRef>(::X86MMXType),
    // Users
    path<User, LLVMValueRef>(::User),
    // Instructions
    path<Instruction, LLVMValueRef>(::Instruction),
    path<AllocaInstruction, LLVMValueRef>(::AllocaInstruction),
    path<AtomicCmpXchgInstruction, LLVMValueRef>(::AtomicCmpXchgInstruction),
    path<AtomicRMWInstruction, LLVMValueRef>(::AtomicRMWInstruction),
    path<BrInstruction, LLVMValueRef>(::BrInstruction),
    path<CallBrInstruction, LLVMValueRef>(::CallBrInstruction),
    path<CallInstruction, LLVMValueRef>(::CallInstruction),
    path<CatchRetInstruction, LLVMValueRef>(::CatchRetInstruction),
    path<CatchPadInstruction, LLVMValueRef>(::CatchPadInstruction),
    path<CatchSwitchInstruction, LLVMValueRef>(::CatchSwitchInstruction),
    path<CleanupPadInstruction, LLVMValueRef>(::CleanupPadInstruction),
    path<CleanupRetInstruction, LLVMValueRef>(::CleanupRetInstruction),
    path<FenceInstruction, LLVMValueRef>(::FenceInstruction),
    path<IndirectBrInstruction, LLVMValueRef>(::IndirectBrInstruction),
    path<InvokeInstruction, LLVMValueRef>(::InvokeInstruction),
    path<LandingPadInstruction, LLVMValueRef>(::LandingPadInstruction),
    path<LoadInstruction, LLVMValueRef>(::LoadInstruction),
    path<PhiInstruction, LLVMValueRef>(::PhiInstruction),
    path<ResumeInstruction, LLVMValueRef>(::ResumeInstruction),
    path<RetInstruction, LLVMValueRef>(::RetInstruction),
    path<SelectInstruction, LLVMValueRef>(::SelectInstruction),
    path<StoreInstruction, LLVMValueRef>(::StoreInstruction),
    path<StoreInstruction, LLVMValueRef>(::StoreInstruction),
    path<SwitchInstruction, LLVMValueRef>(::SwitchInstruction),
    path<UnreachableInstruction, LLVMValueRef>(::UnreachableInstruction),
    path<VAArgInstruction, LLVMValueRef>(::VAArgInstruction)
)

/**
 * Cast the provided value, [self] into type [T] using unsafe RTTI
 *
 * For this cast to succeed, the type of [self] should be able to be
 * constructed with a single argument [T]
 *
 * @see constructorMap for valid casting paths
 *
 * TODO: Fix unchecked cast warning, find proper type
 */
@Suppress("UNCHECKED_CAST")
public inline fun <reified T> castOrNull(self: ContainsReference<*>): T? {
    val fn = constructorMap.getOrElse(T::class.java) { null }
    val res = (fn as? (Any) -> ContainsReference<*>)?.invoke(self.ref)

    return res as T?
}

/**
 * Cast the provided value, [v] into [T] using unsafe RTTI, if the path for
 * this cast does not exist, return [default]
 */
public inline fun <reified T> castOrElse(
    v: ContainsReference<*>,
    default: () -> T
): T {
    return castOrNull<T>(v) ?: default.invoke()
}

/**
 * Cast the provided value, [v] into [T] using unsafe RTTI, if the path for
 * this cast does not exist, throw an [IllegalArgumentException]
 *
 * @throws IllegalArgumentException
 */
public inline fun <reified T> cast(v: ContainsReference<*>): T {
    return castOrNull<T>(v) ?: throw IllegalArgumentException(
        "Could not find valid path to ${T::class.simpleName}"
    )
}

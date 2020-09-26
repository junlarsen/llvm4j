package io.vexelabs.bitbuilder.rtti

import com.sun.jdi.FloatType
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
import java.lang.reflect.Constructor
import javax.lang.model.type.ArrayType

/**
 * Helper function to retrieve the java.lang.Class<[T]> and the constructor of
 * said class which accepts [P]
 *
 * For this to be valid, type [T] should be able to be constructed from type [P]
 *
 * This is only for internal usage to map out RTTI casting paths
 */
private inline fun <reified T, reified P : Pointer> path():
        Pair<Class<T>, Constructor<T>> {
    val clazz = T::class.java
    val ptr = P::class.java

    return Pair(clazz, clazz.getConstructor(ptr))
}

/**
 * A lazy map listing all the valid type casting paths, meaning that the
 * BitBuilder type can be constructed with the JavaCPP Pointer Type
 *
 * @see path
 */
public val constructorMap: Map<Class<*>, Constructor<*>> by lazy {
    mapOf(
        // Basic Values
        path<FunctionValue, LLVMValueRef>(),
        path<IndirectFunction, LLVMValueRef>(),
        path<GlobalValue, LLVMValueRef>(),
        path<GlobalVariable, LLVMValueRef>(),
        path<GlobalAlias, LLVMValueRef>(),
        // Constant Values
        path<ConstantValue, LLVMValueRef>(),
        path<ConstantInt, LLVMValueRef>(),
        path<ConstantVector, LLVMValueRef>(),
        path<ConstantStruct, LLVMValueRef>(),
        path<ConstantPointer, LLVMValueRef>(),
        path<ConstantFloat, LLVMValueRef>(),
        path<ConstantArray, LLVMValueRef>(),
        // Types
        path<ArrayType, LLVMTypeRef>(),
        path<FloatType, LLVMTypeRef>(),
        path<FunctionType, LLVMTypeRef>(),
        path<IntType, LLVMTypeRef>(),
        path<LabelType, LLVMTypeRef>(),
        path<MetadataType, LLVMTypeRef>(),
        path<PointerType, LLVMTypeRef>(),
        path<StructType, LLVMTypeRef>(),
        path<TokenType, LLVMTypeRef>(),
        path<VectorType, LLVMTypeRef>(),
        path<VoidType, LLVMTypeRef>(),
        path<X86MMXType, LLVMTypeRef>(),
        // Users
        path<User, LLVMValueRef>(),
        // Instructions
        path<Instruction, LLVMValueRef>(),
        path<AllocaInstruction, LLVMValueRef>(),
        path<AtomicCmpXchgInstruction, LLVMValueRef>(),
        path<AtomicRMWInstruction, LLVMValueRef>(),
        path<BrInstruction, LLVMValueRef>(),
        path<CallBrInstruction, LLVMValueRef>(),
        path<CallInstruction, LLVMValueRef>(),
        path<CatchPadInstruction, LLVMValueRef>(),
        path<CatchRetInstruction, LLVMValueRef>(),
        path<CatchSwitchInstruction, LLVMValueRef>(),
        path<CleanupPadInstruction, LLVMValueRef>(),
        path<CleanupRetInstruction, LLVMValueRef>(),
        path<FenceInstruction, LLVMValueRef>(),
        path<IndirectBrInstruction, LLVMValueRef>(),
        path<InvokeInstruction, LLVMValueRef>(),
        path<LandingPadInstruction, LLVMValueRef>(),
        path<LoadInstruction, LLVMValueRef>(),
        path<PhiInstruction, LLVMValueRef>(),
        path<ResumeInstruction, LLVMValueRef>(),
        path<RetInstruction, LLVMValueRef>(),
        path<SelectInstruction, LLVMValueRef>(),
        path<StoreInstruction, LLVMValueRef>(),
        path<SwitchInstruction, LLVMValueRef>(),
        path<UnreachableInstruction, LLVMValueRef>(),
        path<VAArgInstruction, LLVMValueRef>()
    )
}

/**
 * Cast the provided value, [self] into type [T] using unsafe RTTI
 *
 * For this cast to succeed, the type of [self] should be able to be
 * constructed with a single argument [T]
 *
 * @see constructorMap for valid casting paths
 */
public inline fun <reified T> castOrNull(self: ContainsReference<*>): T? {
    return constructorMap.getOrElse(T::class.java) { null }
        ?.newInstance(self.ref) as? T
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
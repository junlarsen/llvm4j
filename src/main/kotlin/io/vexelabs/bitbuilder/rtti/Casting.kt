package io.vexelabs.bitbuilder.rtti

import io.vexelabs.bitbuilder.llvm.internal.contracts.ContainsReference
import io.vexelabs.bitbuilder.llvm.ir.Instruction
import io.vexelabs.bitbuilder.llvm.ir.values.constants.ConstantInt
import io.vexelabs.bitbuilder.llvm.ir.values.constants.ConstantPointer
import io.vexelabs.bitbuilder.llvm.ir.values.constants.ConstantStruct
import io.vexelabs.bitbuilder.llvm.ir.values.constants.ConstantVector
import org.bytedeco.javacpp.Pointer
import org.bytedeco.llvm.LLVM.LLVMValueRef
import java.lang.reflect.Constructor

/**
 * Helper function to retrieve the java.lang.Class<[T]> and the constructor of
 * said class which accepts [P]
 *
 * For this to be valid, type [T] should be able to be constructed from type [P]
 *
 * This is only for internal usage to map out RTTI casting paths
 */
private inline fun <reified T, reified P : Pointer> getConstructor():
        Pair<Class<T>, Constructor<T>> {
    val clazz = T::class.java
    val ptr = P::class.java

    return Pair(clazz, clazz.getConstructor(ptr))
}

/**
 * A lazy map listing all the valid type casting paths, meaning that the
 * BitBuilder type can be constructed with the JavaCPP Pointer Type
 *
 * @see getConstructor
 */
public val constructorMap: Map<Class<*>, Constructor<*>> by lazy {
    mapOf(
        getConstructor<ConstantInt, LLVMValueRef>(),
        getConstructor<ConstantVector, LLVMValueRef>(),
        getConstructor<ConstantStruct, LLVMValueRef>(),
        getConstructor<ConstantPointer, LLVMValueRef>(),
        getConstructor<Instruction, LLVMValueRef>(),
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
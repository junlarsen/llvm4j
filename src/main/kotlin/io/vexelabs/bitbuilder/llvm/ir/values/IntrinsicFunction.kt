package io.vexelabs.bitbuilder.llvm.ir.values

import io.vexelabs.bitbuilder.llvm.internal.util.fromLLVMBool
import io.vexelabs.bitbuilder.llvm.ir.Context
import io.vexelabs.bitbuilder.llvm.ir.Module
import io.vexelabs.bitbuilder.llvm.ir.Type
import io.vexelabs.bitbuilder.llvm.ir.types.FunctionType
import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.javacpp.SizeTPointer
import org.bytedeco.llvm.global.LLVM

public class IntrinsicFunction internal constructor() {
    /**
     * If this value is `0` then no intrinsic was found
     */
    public var id: Int = 0

    /**
     * Find an intrinsic by its name
     *
     * If [throwOnFailure] is true, then this function will throw an
     * [IllegalArgumentException] if the intrinsic does not exist.
     *
     * @throws IllegalArgumentException
     */
    public constructor(name: String, throwOnFailure: Boolean = true) : this() {
        val found = LLVM.LLVMLookupIntrinsicID(name, name.length.toLong())

        if (throwOnFailure && found == 0) {
            throw IllegalArgumentException(
                "Could not find intrinsic named $name"
            )
        }

        id = found
    }

    /**
     * Determine whether an intrinsic with this id exists
     *
     * See https://llvm.org/doxygen/Function_8cpp_source.html#l00549
     */
    public fun exists(): Boolean = id != 0

    /**
     * Determine if this intrinsic has overloads or not
     *
     * @see LLVM.LLVMIntrinsicIsOverloaded
     */
    public fun isOverloaded(): Boolean {
        return LLVM.LLVMIntrinsicIsOverloaded(id).fromLLVMBool()
    }

    /**
     * Get the name of an overloaded intrinsic by its parameter list
     *
     * @see LLVM.LLVMIntrinsicCopyOverloadedName
     */
    public fun getOverloadedName(parameters: List<Type>): String {
        require(isOverloaded()) { "This intrinsic is not overloaded." }

        val len = SizeTPointer(0)
        val ptr = PointerPointer(*parameters.map { it.ref }.toTypedArray())

        return LLVM.LLVMIntrinsicCopyOverloadedName(
            id,
            ptr,
            parameters.size.toLong(),
            len
        )
    }

    /**
     * Get the name of this intrinsic
     *
     * @see LLVM.LLVMIntrinsicGetName
     */
    public fun getName(): String {
        val len = SizeTPointer(0)
        val ptr = LLVM.LLVMIntrinsicGetName(id, len)

        len.deallocate()

        return ptr.string
    }

    /**
     * Get the function value of this intrinsic
     *
     * @see LLVM.LLVMGetIntrinsicDeclaration
     */
    public fun getDeclaration(
        module: Module,
        parameters: List<Type>
    ): FunctionValue {
        val ptr = PointerPointer(*parameters.map { it.ref }.toTypedArray())
        val decl = LLVM.LLVMGetIntrinsicDeclaration(
            module.ref,
            id,
            ptr,
            parameters.size.toLong()
        )

        return FunctionValue(decl)
    }

    /**
     * Get the type declaration for this intrinsic
     *
     * @see LLVM.LLVMIntrinsicGetType
     */
    public fun getType(context: Context, parameters: List<Type>): FunctionType {
        val ptr = PointerPointer(*parameters.map { it.ref }.toTypedArray())
        val type = LLVM.LLVMIntrinsicGetType(
            context.ref,
            id,
            ptr,
            parameters.size.toLong()
        )

        return FunctionType(type)
    }
}

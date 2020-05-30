package dev.supergrecko.kllvm.ir.values

import dev.supergrecko.kllvm.internal.util.fromLLVMBool
import dev.supergrecko.kllvm.ir.Context
import dev.supergrecko.kllvm.ir.Module
import dev.supergrecko.kllvm.ir.Type
import dev.supergrecko.kllvm.ir.types.FunctionType
import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.javacpp.SizeTPointer
import org.bytedeco.llvm.global.LLVM

/**
 * If value is 0 then no intrinsic was found
 *
 * See https://llvm.org/doxygen/Intrinsics_8h_source.html#l00036
 */
public class IntrinsicFunction internal constructor() {
    //region Core::Values::Constants::FunctionValues
    public var id: Int = 0

    /**
     * Find an intrinsic by its name
     *
     * If [throwOnFailure] is true, then this function will throw an
     * [IllegalArgumentException] if the intrinsic does not exist.
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
    public fun exists() = id != 0

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

        val ptr = SizeTPointer(0)
        val arr = ArrayList(parameters.map { it.ref })
            .toTypedArray()

        return LLVM.LLVMIntrinsicCopyOverloadedName(
            id,
            PointerPointer(*arr),
            parameters.size.toLong(),
            ptr
        )
    }

    /**
     * Get the name of this intrinsic
     *
     * @see LLVM.LLVMIntrinsicGetName
     */
    public fun getName(): String {
        val ptr = SizeTPointer(0)

        return LLVM.LLVMIntrinsicGetName(id, ptr).string
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
        val arr = ArrayList(parameters.map { it.ref })
            .toTypedArray()

        val decl = LLVM.LLVMGetIntrinsicDeclaration(
            module.ref,
            id,
            PointerPointer(*arr),
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
        val arr = ArrayList(parameters.map { it.ref })
            .toTypedArray()

        val type = LLVM.LLVMIntrinsicGetType(
            context.ref,
            id,
            PointerPointer(*arr),
            parameters.size.toLong()
        )

        return FunctionType(type)
    }
    //endregion Core::Values::Constants::FunctionValues
}

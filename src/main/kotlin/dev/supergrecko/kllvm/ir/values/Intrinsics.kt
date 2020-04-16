package dev.supergrecko.kllvm.ir.values

import dev.supergrecko.kllvm.internal.util.toBoolean
import dev.supergrecko.kllvm.ir.Context
import dev.supergrecko.kllvm.ir.Module
import dev.supergrecko.kllvm.ir.Type
import dev.supergrecko.kllvm.ir.types.FunctionType
import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.javacpp.SizeTPointer
import org.bytedeco.llvm.global.LLVM
import kotlin.properties.Delegates

public class IntrinsicFunction {
    public val id: Int = -1
}

//region Core::Values::Constants::FunctionValues
public fun lookupIntrinsicId(name: String): Int {
    return LLVM.LLVMLookupIntrinsicID(name, name.length.toLong())
}

public fun intrinsicCopyOverloadedName(
    intrinsicId: Int,
    parameters: List<Type>
): String {
    val arr = ArrayList(parameters.map { it.ref }).toTypedArray()

    return LLVM.LLVMIntrinsicCopyOverloadedName(
        intrinsicId,
        PointerPointer(*arr),
        parameters.size.toLong(),
        SizeTPointer(0)
    )
}

public fun intrinsicIsOverloaded(intrinsicId: Int): Boolean {
    return LLVM.LLVMIntrinsicIsOverloaded(intrinsicId).toBoolean()
}

public fun getIntrinsicDeclaration(
    module: Module,
    intrinsicId: Int,
    parameters: List<Type>
): FunctionValue {
    val arr = ArrayList(parameters.map { it.ref }).toTypedArray()

    val dec = LLVM.LLVMGetIntrinsicDeclaration(
        module.ref,
        intrinsicId,
        PointerPointer(*arr),
        parameters.size.toLong()
    )

    return FunctionValue(dec)
}

public fun getIntrinsicType(
    context: Context,
    intrinsicId: Int,
    parameters: List<Type>
): FunctionType {
    val arr = ArrayList(parameters.map { it.ref }).toTypedArray()

    val type = LLVM.LLVMIntrinsicGetType(
        context.ref,
        intrinsicId,
        PointerPointer(*arr),
        parameters.size.toLong()
    )

    return FunctionType(type)
}

public fun getIntrinsicName(
    intrinsicId: Int
): String {
    val ptr = SizeTPointer(0)

    return LLVM.LLVMIntrinsicGetName(intrinsicId, ptr).string
}
//endregion Core::Values::Constants::FunctionValues

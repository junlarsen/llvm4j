package org.llvm4j.llvm4j

import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.javacpp.SizeTPointer
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM
import org.llvm4j.llvm4j.util.None
import org.llvm4j.llvm4j.util.Option
import org.llvm4j.llvm4j.util.Result
import org.llvm4j.llvm4j.util.Some
import org.llvm4j.llvm4j.util.toBoolean
import org.llvm4j.llvm4j.util.toPointerPointer
import org.llvm4j.llvm4j.util.tryWith

/**
 * Represents an intrinsic function in the LLVM system
 *
 * To get an instance of [IntrinsicFunction], use [lookup]
 *
 * @author Mats Larsen
 */
public class IntrinsicFunction constructor(intrinsic: Int) {
    public val id: Int = intrinsic

    public fun isOverloaded(): Boolean {
        return LLVM.LLVMIntrinsicIsOverloaded(id).toBoolean()
    }

    public fun getName(): String {
        val size = SizeTPointer(1L)
        val ptr = LLVM.LLVMIntrinsicGetName(id, size)
        val copy = ptr.string

        ptr.deallocate()
        size.deallocate()

        return copy
    }

    public fun getDeclaration(target: Module): Result<Function> = tryWith {
        assert(!isOverloaded()) { "Called getDeclaration on overloaded intrinsic" }

        val ptr = PointerPointer<LLVMTypeRef>(1L)
        val function = LLVM.LLVMGetIntrinsicDeclaration(target.ref, id, ptr, 0)

        ptr.deallocate()

        Function(function)
    }

    public fun getType(inContext: Context): Result<FunctionType> = tryWith {
        assert(!isOverloaded()) { "Called getType on overloaded intrinsic" }

        val ptr = PointerPointer<LLVMTypeRef>(1L)
        val function = LLVM.LLVMIntrinsicGetType(inContext.ref, id, ptr, 0)

        ptr.deallocate()

        FunctionType(function)
    }

    public fun getOverloadedName(vararg params: Type): Result<String> = tryWith {
        assert(isOverloaded()) { "Called getOverloadedName on regular intrinsic" }

        val size = SizeTPointer(1L)
        val ptr = params.map { it.ref }.toPointerPointer()
        val name = LLVM.LLVMIntrinsicCopyOverloadedName(id, ptr, params.size.toLong(), size)

        ptr.deallocate()
        size.deallocate()

        name
    }

    public fun getOverloadedDeclaration(target: Module, vararg params: Type): Result<Function> = tryWith {
        assert(isOverloaded()) { "Called getOverloadedName on regular intrinsic" }

        val ptr = params.map { it.ref }.toPointerPointer()
        val function = LLVM.LLVMGetIntrinsicDeclaration(target.ref, id, ptr, params.size.toLong())

        ptr.deallocate()

        Function(function)
    }

    public fun getOverloadedType(inContext: Context, vararg params: Type): Result<FunctionType> = tryWith {
        assert(isOverloaded()) { "Called getOverloadedName on regular intrinsic" }

        val ptr = params.map { it.ref }.toPointerPointer()
        val function = LLVM.LLVMIntrinsicGetType(inContext.ref, id, ptr, params.size.toLong())

        ptr.deallocate()

        FunctionType(function)
    }

    public companion object {
        @JvmStatic
        public fun lookup(name: String): Option<IntrinsicFunction> {
            val id = LLVM.LLVMLookupIntrinsicID(name, name.length.toLong())

            return if (id != 0) {
                Some(IntrinsicFunction(id))
            } else {
                None
            }
        }
    }
}

package dev.supergrecko.kllvm.core

import dev.supergrecko.kllvm.contracts.Unreachable
import dev.supergrecko.kllvm.core.enumerations.LLVMTypeKind
import dev.supergrecko.kllvm.core.message.Message
import dev.supergrecko.kllvm.factories.TypeFactory
import dev.supergrecko.kllvm.utils.*
import dev.supergrecko.kllvm.utils.iterateIntoType
import dev.supergrecko.kllvm.utils.toBoolean
import dev.supergrecko.kllvm.utils.toInt
import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.llvm.LLVM.LLVMContextRef
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM
import java.lang.IllegalArgumentException

/**
 * Higher level wrapper around LLVM Core's types module
 *
 * -[Documentation](https://llvm.org/doxygen/group__LLVMCCoreType.html)
 */
public open class LLVMType internal constructor(
        internal val llvmType: LLVMTypeRef,
        public var kind: LLVMTypeKind = getTypeKind(llvmType)
) {
    //region Core::Types

    /**
     * Get the type kind for this type
     */
    public fun getTypeKind(): LLVMTypeKind {
        return getTypeKind(llvmType)
    }

    /**
     * Determine whether this type has a known size or not
     */
    public fun isSized(): Boolean {
        return LLVM.LLVMTypeIsSized(llvmType).toBoolean()
    }

    /**
     * Dump the type to stderr. Not supported by LLVM 9.x.x
     */
    public fun dumpType(): Unit = TODO("LLVM 9.x.x does not support this method.")

    /**
     * Get the context this type resides in
     */
    public fun getContext(): LLVMContext {
        except(kind, LLVMTypeKind.Function, LLVMTypeKind.Array, LLVMTypeKind.Pointer, LLVMTypeKind.Vector)

        val ctx = LLVM.LLVMGetTypeContext(llvmType)

        return LLVMContext(ctx)
    }

    /**
     * Moves the string representation into a Message
     *
     * This message must be disposed via [Message.dispose] otherwise memory will be leaked.
     */
    public fun getString(): Message {
        val ptr = LLVM.LLVMPrintTypeToString(llvmType)

        return Message(ptr.asBuffer())
    }

    //endregion Core::Types
    //region Core::Types::StructureTypes

    /**
     * Accepts [LLVMTypeKind.Struct]
     */
    public fun isPackedStruct(): Boolean {
        requires(kind, LLVMTypeKind.Struct)

        return LLVM.LLVMIsPackedStruct(llvmType).toBoolean()
    }

    /**
     * Accepts [LLVMTypeKind.Struct]
     */
    public fun isOpaqueStruct(): Boolean {
        requires(kind, LLVMTypeKind.Struct)

        return LLVM.LLVMIsOpaqueStruct(llvmType).toBoolean()
    }

    /**
     * Accepts [LLVMTypeKind.Struct]
     */
    public fun isLiteralStruct(): Boolean {
        requires(kind, LLVMTypeKind.Struct)

        return LLVM.LLVMIsLiteralStruct(llvmType).toBoolean()
    }

    /**
     * Set the struct body for an opaque struct
     * 
     * Accepts [LLVMTypeKind.Struct]
     */
    public fun setStructBody(elementTypes: List<LLVMType>, packed: Boolean) {
        requires(kind, LLVMTypeKind.Struct)
        require(isOpaqueStruct())

        val types = elementTypes.map { it.llvmType }
        val array = ArrayList(types).toTypedArray()
        val ptr = PointerPointer(*array)

        LLVM.LLVMStructSetBody(llvmType, ptr, array.size, packed.toInt())
    }

    /**
     * Get the element type from the struct at index [index]
     *
     * Accepts [LLVMTypeKind.Struct]
     */
    public fun getElementTypeAt(index: Int): LLVMType {
        requires(kind, LLVMTypeKind.Struct)
        require(index <= getElementSize()) { "Requested index $index is out of bounds for this struct" }

        val type = LLVM.LLVMStructGetTypeAtIndex(llvmType, index)

        return LLVMType(type, getTypeKind(type))
    }

    /**
     * Get the struct name if it has a name
     *
     * Accepts [LLVMTypeKind.Struct]
     */
    public fun getStructName(): String? {
        requires(kind, LLVMTypeKind.Struct)

        // TODO: Resolve IllegalStateException
        val name = LLVM.LLVMGetStructName(llvmType)

        return if (name.bool) {
            null
        } else {
            name.string
        }
    }

    /**
     * Get the types of the elements in this struct
     *
     * Accepts [LLVMTypeKind.Struct]
     */
    public fun getStructElementTypes(): List<LLVMType> {
        requires(kind, LLVMTypeKind.Struct)

        val dest = PointerPointer<LLVMTypeRef>(getElementSize().toLong())
        LLVM.LLVMGetStructElementTypes(llvmType, dest)

        return dest.iterateIntoType { LLVMType(it, getTypeKind(it)) }
    }

    //endregion Core::Types::StructureTypes
    //region Core::Types::SequentialTypes

    /**
     * Get the declared address space for this pointer
     *
     * Accepts [LLVMTypeKind.Pointer]
     */
    public fun getAddressSpace(): Int {
        requires(kind, LLVMTypeKind.Pointer)

        return LLVM.LLVMGetPointerAddressSpace(llvmType)
    }

    /**
     * Get the amount of bits this integer type can hold
     *
     * Accepts [LLVMTypeKind.Integer]
     */
    public fun getTypeWidth(): Int {
        requires(kind, LLVMTypeKind.Integer)

        return LLVM.LLVMGetIntTypeWidth(llvmType)
    }

    /**
     * Get the amount of elements for this type
     *
     * Accepts [LLVMTypeKind.Array]: Allocated array size
     * Accepts [LLVMTypeKind.Vector]: Vector size
     * Accepts [LLVMTypeKind.Pointer]: Number of derived types
     * Accepts [LLVMTypeKind.Struct]: Number of elements in struct body
     */
    public fun getElementSize(): Int {
        requires(kind, LLVMTypeKind.Array, LLVMTypeKind.Vector, LLVMTypeKind.Pointer, LLVMTypeKind.Struct)

        return when (kind) {
            LLVMTypeKind.Array -> LLVM.LLVMGetArrayLength(llvmType)
            LLVMTypeKind.Vector -> LLVM.LLVMGetVectorSize(llvmType)
            LLVMTypeKind.Pointer -> LLVM.LLVMGetNumContainedTypes(llvmType)
            LLVMTypeKind.Struct -> LLVM.LLVMCountStructElementTypes(llvmType)
            else -> throw Unreachable()
        }
    }

    /**
     * Get the subtype of this type
     *
     * Accepts [LLVMTypeKind.Array]
     * Accepts [LLVMTypeKind.Pointer]
     * Accepts [LLVMTypeKind.Vector]
     *
     * TODO: Learn how to test this
     */
    public fun getSubtypes(): List<LLVMType> {
        requires(kind, LLVMTypeKind.Array, LLVMTypeKind.Pointer, LLVMTypeKind.Vector)

        val dest = PointerPointer<LLVMTypeRef>(getElementSize().toLong())
        LLVM.LLVMGetSubtypes(llvmType, dest)

        return dest.iterateIntoType { LLVMType(it, getTypeKind(it)) }
    }

    /**
     * Get the underlying element's type
     *
     * Accepts [LLVMTypeKind.Array]
     * Accepts [LLVMTypeKind.Pointer]
     * Accepts [LLVMTypeKind.Vector]
     */
    public fun getElementType(): LLVMType {
        requires(kind, LLVMTypeKind.Array, LLVMTypeKind.Pointer, LLVMTypeKind.Vector)

        val type = LLVM.LLVMGetElementType(llvmType)

        return LLVMType(type, getTypeKind(type))
    }

    //endregion Core::Types::SequentialTypes
    //region Core::Types::FunctionTypes

    /**
     * Determine whether this function accepts variadic arguments
     *
     * Accepts [LLVMTypeKind.Function]
     */
    public fun isVariadic(): Boolean {
        requires(kind, LLVMTypeKind.Function)

        return LLVM.LLVMIsFunctionVarArg(llvmType).toBoolean()
    }

    /**
     * Accepts [LLVMTypeKind.Function]
     */
    public fun getParameterCount(): Int {
        requires(kind, LLVMTypeKind.Function)

        return LLVM.LLVMCountParamTypes(llvmType)
    }

    /**
     * Get the return type of the function
     *
     * Accepts [LLVMTypeKind.Function]
     */
    public fun getReturnType(): LLVMType {
        requires(kind, LLVMTypeKind.Function)

        val type = LLVM.LLVMGetReturnType(llvmType)

        return LLVMType(type, getTypeKind(type))
    }

    /**
     * Get the types of this function's parameters
     *
     * Accepts [LLVMTypeKind.Function]
     */
    public fun getParameterTypes(): List<LLVMType> {
        requires(kind, LLVMTypeKind.Function)

        val dest = PointerPointer<LLVMTypeRef>(getParameterCount().toLong())
        LLVM.LLVMGetParamTypes(llvmType, dest)

        return dest
                .iterateIntoType { LLVMType(it, getTypeKind(it)) }
    }

    //endregion Core::Types::FunctionTypes

    /**
     * "Cast" into another LLVMType
     *
     * This cast is not safe as there is no guarantee that the underlying type matches
     * the requested type.
     */
    public fun cast(into: LLVMTypeKind): LLVMType {
        kind = into

        return this
    }

    /**
     * Wrap this type inside a pointer
     */
    public fun toPointer(addressSpace: Int = 0): LLVMType = TypeFactory.pointer(this, addressSpace)

    /**
     * Wrap this type inside an array
     */
    public fun toArray(size: Int): LLVMType = TypeFactory.array(this, size)

    /**
     * Wrap this type inside a vector
     */
    public fun toVector(size: Int): LLVMType = TypeFactory.vector(this, size)

    companion object {
        @JvmStatic
        public fun getTypeKind(type: LLVMTypeRef): LLVMTypeKind {
            val kind = LLVM.LLVMGetTypeKind(type)

            return LLVMTypeKind.values()
                    .firstOrNull { it.value == kind }
                    // Theoretically unreachable, but kept if wrong LLVM version is used
                    ?: throw IllegalArgumentException("Type $type has invalid type kind")
        }
    }
}

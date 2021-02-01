package org.llvm4j.llvm4j

import org.bytedeco.javacpp.IntPointer
import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.javacpp.SizeTPointer
import org.bytedeco.llvm.LLVM.LLVMAttributeRef
import org.bytedeco.llvm.LLVM.LLVMBasicBlockRef
import org.bytedeco.llvm.LLVM.LLVMValueMetadataEntry
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM
import org.llvm4j.llvm4j.util.CorrespondsTo
import org.llvm4j.llvm4j.util.InternalApi
import org.llvm4j.llvm4j.util.None
import org.llvm4j.llvm4j.util.Option
import org.llvm4j.llvm4j.util.Owner
import org.llvm4j.llvm4j.util.Result
import org.llvm4j.llvm4j.util.Some
import org.llvm4j.llvm4j.util.toBoolean
import org.llvm4j.llvm4j.util.toInt
import org.llvm4j.llvm4j.util.tryWith
import java.nio.file.Path
import java.nio.file.Paths

/**
 * A Value in a computed program
 *
 * This is a core class in the LLVM hierarchy as it is the base class of all values which may be computed and used in
 * a program.
 *
 * Value is also the base class to other important classes such as [Instruction]s or [Function]s.
 *
 * All values have a [Type] which describes which data type the value is and a [Use] list which keeps track of which
 * other [Value]s reference this value.
 *
 * @see Type
 * @see Constant
 * @see Instruction
 * @see Function
 *
 * TODO: Testing - Test [dump] somehow?
 * TODO: LLVM 12.x - LLVMIsPoison
 * TODO: Testing - Test [replace] (asmparsers)
 *
 * @author Mats Larsen
 */
@CorrespondsTo("llvm::Value")
public open class Value constructor(ptr: LLVMValueRef) : Owner<LLVMValueRef> {
    public override val ref: LLVMValueRef = ptr

    public fun getType(): Type {
        val type = LLVM.LLVMTypeOf(ref)

        return Type(type)
    }

    public fun getValueKind(): ValueKind {
        val kind = LLVM.LLVMGetValueKind(ref)

        return ValueKind.from(kind).get()
    }

    public fun getAsString(): String {
        val ptr = LLVM.LLVMPrintValueToString(ref)
        val copy = ptr.string

        ptr.deallocate()

        return copy
    }

    /**
     * Replaces all usages of this value with another value
     *
     * @param other value to replace this with
     */
    public fun replace(other: Value) {
        LLVM.LLVMReplaceAllUsesWith(ref, other.ref)
    }

    public fun isConstant(): Boolean {
        return LLVM.LLVMIsConstant(ref).toBoolean()
    }

    public fun isUndef(): Boolean {
        return LLVM.LLVMIsUndef(ref).toBoolean()
    }

    public fun getFirstUse(): Option<Use> {
        val use = LLVM.LLVMGetFirstUse(ref)

        return use?.let { Some(Use(it)) } ?: None
    }

    public fun asBasicBlock(): BasicBlock {
        val bb = LLVM.LLVMValueAsBasicBlock(ref)

        return BasicBlock(bb)
    }

    public fun asMetadata(): ValueAsMetadata {
        val md = LLVM.LLVMValueAsMetadata(ref)

        return ValueAsMetadata(md)
    }

    /**
     * Common implementation for any value which has a retrievable debug location at compile time.
     *
     * In the C++ API there are 3 different implementations for this, but the C API has thrown all of these under
     * umbrella functions which delegate to the C++ implementations.
     *
     * Known inheritors are [Instruction], [GlobalVariable] and [Function]
     *
     * TODO: Testing - Test once debug metadata is stable (parse bc file? llvm-ir tests)
     *
     * @author Mats Larsen
     */
    @InternalApi
    public interface HasDebugLocation : Owner<LLVMValueRef> {
        public fun getDebugLine(): Int {
            return LLVM.LLVMGetDebugLocLine(ref)
        }

        public fun getDebugColumn(): Int {
            return LLVM.LLVMGetDebugLocColumn(ref)
        }

        public fun getDebugFile(): Path {
            val size = IntPointer(1L)
            val ptr = LLVM.LLVMGetDebugLocFilename(ref, size)
            val copy = ptr.string

            ptr.deallocate()
            size.deallocate()

            return Paths.get(copy)
        }

        public fun getDebugDirectory(): Path {
            val size = IntPointer(1L)
            val ptr = LLVM.LLVMGetDebugLocDirectory(ref, size)
            val copy = ptr.string

            ptr.deallocate()
            size.deallocate()

            return Paths.get(copy)
        }
    }

    /**
     * Common implementation for any value which may have a name.
     *
     * Only a few value kinds in LLVM IR may have a name. These are limited to instructions, basic blocks, functions,
     * global values and function arguments.
     *
     * Inheritors in the LLVM hierarchy are:
     *
     * @see Instruction
     * @see BasicBlock
     * @see Function
     * @see GlobalValue
     * @see Argument
     *
     * @author Mats Larsen
     */
    @InternalApi
    public interface HasName : Owner<LLVMValueRef> {
        public fun getName(): String {
            val size = SizeTPointer(1L)
            val ptr = LLVM.LLVMGetValueName2(ref, size)
            val copy = ptr.string

            ptr.deallocate()
            size.deallocate()

            return copy
        }

        public fun setName(name: String) {
            LLVM.LLVMSetValueName2(ref, name, name.length.toLong())
        }
    }
}

/**
 * Represents any value which may use another value.
 *
 * Each instance of [Value] keeps track of which other values use it, these values are all [User]s. Common users are
 * instructions and constants.
 *
 * @see Value
 * @see Use
 *
 * TODO: Testing - Test once values are more usable (see LLVM test suite, asmparser)
 *
 * @author Mats Larsen
 */
@CorrespondsTo("llvm::User")
public open class User constructor(ptr: LLVMValueRef) : Value(ptr) {
    public fun getOperand(index: Int): Result<Value> = tryWith {
        assert(index < getOperandCount()) { "Index $index is out of bounds for size of ${getOperandCount()}" }

        val ptr = LLVM.LLVMGetOperand(ref, index)

        Value(ptr)
    }

    public fun getOperandUse(index: Int): Result<Use> = tryWith {
        assert(index < getOperandCount()) { "Index $index is out of bounds for size of ${getOperandCount()}" }

        val use = LLVM.LLVMGetOperandUse(ref, index)

        Use(use)
    }

    public fun setOperand(index: Int, value: Value): Result<Unit> = tryWith {
        assert(index < getOperandCount()) { "Index $index is out of bounds for size of ${getOperandCount()}" }
        assert(!isConstant()) { "Cannot mutate a constant with setOperand" }
        // TODO: Api - Replace once isa<> is implemented
        assert(LLVM.LLVMIsAGlobalValue(ref) == null) { "Cannot mutate a constant with setOperand" }

        LLVM.LLVMSetOperand(ref, index, value.ref)
    }

    public fun getOperandCount(): Int {
        return LLVM.LLVMGetNumOperands(ref)
    }
}

/**
 * A single basic block in a function
 *
 * A basic block is a set of instructions which execute sequentially. All basic blocks end with a terminator
 * instruction which indicates where control flow will be continued.
 *
 * Basic blocks are values because other instructions may reference them (branching, switch tables)
 *
 * TODO: API - Implement LLVMBlockAddress to get address of basic block
 * TODO: API - Implement LLVMValueIsBasicBlock through isa
 * TODO: Iterators - Instruction iterator
 *
 * @author Mats Larsen
 */
public class BasicBlock public constructor(ptr: LLVMBasicBlockRef) : Owner<LLVMBasicBlockRef> {
    public override val ref: LLVMBasicBlockRef = ptr

    public fun asValue(): BasicBlockAsValue {
        val value = LLVM.LLVMBasicBlockAsValue(ref)

        return BasicBlockAsValue(value)
    }

    public fun getName(): String {
        val ptr = LLVM.LLVMGetBasicBlockName(ref)
        val copy = ptr.string

        ptr.deallocate()

        return copy
    }

    public fun getFunction(): Option<Function> {
        val fn = LLVM.LLVMGetBasicBlockParent(ref)

        return fn?.let { Some(Function(it)) } ?: None
    }

    public fun moveBefore(block: BasicBlock) {
        LLVM.LLVMMoveBasicBlockBefore(ref, block.ref)
    }

    public fun moveAfter(block: BasicBlock) {
        LLVM.LLVMMoveBasicBlockAfter(ref, block.ref)
    }

    public fun delete() {
        LLVM.LLVMDeleteBasicBlock(ref)
    }

    public fun erase() {
        LLVM.LLVMRemoveBasicBlockFromParent(ref)
    }
}

/**
 * Represents a basic block in the value representation.
 *
 * LLVM basic blocks are also values which means it can be converted to a value in the C API.
 *
 * @author Mats Larsen
 */
public class BasicBlockAsValue public constructor(ptr: LLVMValueRef) : Value(ptr) {
    public fun getBlock(): BasicBlock {
        val bb = LLVM.LLVMValueAsBasicBlock(ref)

        return BasicBlock(bb)
    }
}

/**
 * A Metadata wrapper in LLVMs Value hierarchy
 *
 * This allows a value to reference a metadata node, allowing intrinsics to have metadata nodes as their operands. An
 * equivalent class exists for values wrapped as metadata.
 *
 * @see ValueAsMetadata
 *
 * @author Mats Larsen
 */
@CorrespondsTo("llvm::MetadataAsValue")
public class MetadataAsValue(ptr: LLVMValueRef) : Value(ptr) {
    public fun isString(): Boolean {
        return LLVM.LLVMIsAMDString(ref) != null
    }

    public fun isNode(): Boolean {
        return LLVM.LLVMIsAMDNode(ref) != null
    }
}

/**
 * Base class for all constant values in an LLVM program. Constants are values which are immutable at runtime, such
 * as numbers and other values.
 *
 * Constants may be complex values such as arrays or structures, basic like integers and floating points or
 * expression based such as a the result of a computation (instructions)
 *
 * Functions and global variables are also constants because their addresses are immutable.
 *
 * @see ConstantInt
 * @see ConstantFP
 * @see ConstantVector
 * @see ConstantArray
 * @see ConstantStruct
 * @see ConstantExpression
 * @see Function
 * @see GlobalValue
 *
 * @author Mats Larsen
 */
@CorrespondsTo("llvm::Constant")
public open class Constant constructor(ptr: LLVMValueRef) : User(ptr) {
    public fun isNull(): Boolean {
        return LLVM.LLVMIsNull(ref).toBoolean()
    }
}

/**
 * Base class for composite values with operands.
 *
 * These are aggregate values, meaning they're composed of other values.
 *
 * Inheritors in the LLVM hierarchy are:
 *
 * @see ConstantStruct
 * @see ConstantArray
 * @see ConstantVector
 *
 * @author Mats Larsen
 */
@CorrespondsTo("llvm::ConstantAggregate")
public open class ConstantAggregate constructor(ptr: LLVMValueRef) : Constant(ptr)

/**
 * Base class for constant values with no operands.
 *
 * Constant data are constants which represent their data directly. They can be in use by unrelated modules and
 * because they do not have any operands it does not make sense to replace all uses of them.
 *
 * @see Value.replace
 *
 * Inheritors in the LLVM hierarchy are:
 *
 * @see ConstantAggregateZero
 * @see ConstantDataSequential
 * @see ConstantFP
 * @see ConstantInt
 * @see ConstantPointerNull
 * @see ConstantTokenNone
 * @see UndefValue
 *
 * @author Mats Larsen
 */
@CorrespondsTo("llvm::ConstantData")
public open class ConstantData constructor(ptr: LLVMValueRef) : Constant(ptr)

/**
 * A vector or array constant whose element type is either i1, i2, i4, i8, float or double.
 *
 * Elements of a constant data sequential are simple data values. A constant data sequential does not have any
 * operands because it stores all of its elements as densely packed data instead of Value instances for performance
 * reasons.
 *
 * Inheritors in the LLVM hierarchy are:
 *
 * @see ConstantDataArray
 * @see ConstantDataVector
 *
 * TODO: Research - Index out of bounds testing for [getElement]?
 *
 * @author Mats Larsen
 */
@CorrespondsTo("llvm::ConstantDataSequential")
public open class ConstantDataSequential constructor(ptr: LLVMValueRef) : ConstantData(ptr) {
    public fun getStringValue(): String {
        val size = SizeTPointer(1L)
        val ptr = LLVM.LLVMGetAsString(ref, size)
        val copy = ptr.string

        ptr.deallocate()
        size.deallocate()

        return copy
    }

    public fun getElement(index: Int): Constant {
        val elem = LLVM.LLVMGetElementAsConstant(ref, index)

        return Constant(elem)
    }

    public fun isString(): Boolean {
        return LLVM.LLVMIsConstantString(ref).toBoolean()
    }
}

/**
 * An array constant whose element type is either i1, i2, i4, i8, float or double.
 *
 * @see ConstantDataSequential
 *
 * @author Mats Larsen
 */
@CorrespondsTo("llvm::ConstantDataArray")
public class ConstantDataArray public constructor(ptr: LLVMValueRef) : ConstantDataSequential(ptr)

/**
 * A vector constant whose element type is either i1, i2, i4, i8, float or double.
 *
 * @see ConstantDataSequential
 *
 * @author Mats Larsen
 */
@CorrespondsTo("llvm::ConstantDataVector")
public class ConstantDataVector public constructor(ptr: LLVMValueRef) : ConstantDataSequential(ptr)

/**
 * Base class for any globally defined object in a module.
 *
 * Global values are constants which are defined in a module. These values have special capabilities which other
 * constants do not have. For example, using the address of it as a constant.
 *
 * Inheritors in the LLVM hierarchy are:
 *
 * @see GlobalAlias
 * @see GlobalIndirectFunction
 * @see Function
 * @see GlobalVariable
 *
 * These subtypes inherit these traits through one of these sub classes:
 *
 * @see GlobalIndirectSymbol
 * @see GlobalObject
 *
 * TODO: LLVM 12.x - LLVMIsDeclaration()
 *
 * @author Mats Larsen
 */
@CorrespondsTo("llvm::GlobalValue")
public open class GlobalValue constructor(ptr: LLVMValueRef) :
    Constant(ptr),
    Owner<LLVMValueRef>,
    Value.HasName {
    public fun getModule(): Module {
        val module = LLVM.LLVMGetGlobalParent(ref)

        return Module(module)
    }

    public fun getLinkage(): Linkage {
        val linkage = LLVM.LLVMGetLinkage(ref)

        return Linkage.from(linkage).get()
    }

    public fun setLinkage(linkage: Linkage) {
        LLVM.LLVMSetLinkage(ref, linkage.value)
    }

    public fun getSection(): Option<String> {
        val ptr = LLVM.LLVMGetSection(ref)

        return ptr?.let {
            val copy = ptr.string
            ptr.deallocate()
            Some(copy)
        } ?: None
    }

    public fun setSection(section: String) {
        LLVM.LLVMSetSection(ref, section)
    }

    public fun getVisibility(): Visibility {
        val visibility = LLVM.LLVMGetVisibility(ref)

        return Visibility.from(visibility).get()
    }

    public fun setVisibility(visibility: Visibility) {
        LLVM.LLVMSetVisibility(ref, visibility.value)
    }

    public fun getStorageClass(): DLLStorageClass {
        val storage = LLVM.LLVMGetDLLStorageClass(ref)

        return DLLStorageClass.from(storage).get()
    }

    public fun setStorageClass(storage: DLLStorageClass) {
        LLVM.LLVMSetDLLStorageClass(ref, storage.value)
    }

    public fun getUnnamedAddress(): UnnamedAddress {
        val addr = LLVM.LLVMGetUnnamedAddress(ref)

        return UnnamedAddress.from(addr).get()
    }

    public fun setUnnamedAddress(address: UnnamedAddress) {
        LLVM.LLVMSetUnnamedAddress(ref, address.value)
    }

    /**
     * Get the type of the underlying value. This differs from [getType] because the type of a global value is always
     * a pointer type.
     */
    public fun getValueType(): Type {
        val type = LLVM.LLVMGlobalGetValueType(ref)

        return Type(type)
    }
}

/**
 * An independent global object, a function or a variable, but not an alias.
 *
 * Inheritors in the LLVM hierarchy are:
 *
 * @see Function
 * @see GlobalVariable
 *
 * TODO: Testing - Test metadata once metadata is stable
 *
 * @author Mats Larsen
 */
@CorrespondsTo("llvm::GlobalObject")
public open class GlobalObject constructor(ptr: LLVMValueRef) : GlobalValue(ptr) {
    public fun getPreferredAlignment(): Int {
        return LLVM.LLVMGetAlignment(ref)
    }

    public fun setPreferredAlignment(alignment: Int) {
        LLVM.LLVMSetAlignment(ref, alignment)
    }

    public fun setMetadata(kind: Int, node: Metadata) {
        LLVM.LLVMGlobalSetMetadata(ref, kind, node.ref)
    }

    public fun eraseMetadata(kind: Int) {
        LLVM.LLVMGlobalEraseMetadata(ref, kind)
    }

    public fun clearMetadata() {
        LLVM.LLVMGlobalClearMetadata(ref)
    }

    public fun getAllMetadata(): MetadataEntry {
        val size = SizeTPointer(1L)
        val entries = LLVM.LLVMGlobalCopyAllMetadata(ref, size)

        return MetadataEntry(entries, size)
    }

    /**
     * Wrapper type for an array of metadata nodes which belong to a global object.
     *
     * This is a rather useless type by itself and is only used when copying all the metadata a global object has
     * through [GlobalObject.getAllMetadata]
     *
     * This type is exclusive to the LLVM C API and has no equivalent in the C++ API as it's just a data transfer
     * object.
     *
     * @author Mats Larsen
     */
    public class MetadataEntry public constructor(
        ptr: LLVMValueMetadataEntry,
        private val size: SizeTPointer
    ) : Owner<LLVMValueMetadataEntry> {
        public override val ref: LLVMValueMetadataEntry = ptr

        public fun size(): Long = size.get()

        public fun getKindId(index: Int): Result<Int> = tryWith {
            assert(index < size()) { "Out of bounds index $index, size is ${size()}" }

            LLVM.LLVMValueMetadataEntriesGetKind(ref, index)
        }

        public fun getMetadata(index: Int): Result<Metadata> = tryWith {
            assert(index < size()) { "Out of bounds index $index, size is ${size()}" }

            val node = LLVM.LLVMValueMetadataEntriesGetMetadata(ref, index)

            Metadata(node)
        }

        public override fun deallocate() {
            LLVM.LLVMDisposeValueMetadataEntries(ref)
            size.deallocate()
        }
    }
}

/**
 * An alias to a global value, either a global alias or an indirect function.
 *
 * Inheritors in the LLVM hierarchy are:
 *
 * @see GlobalIndirectFunction
 * @see GlobalAlias
 *
 * @author Mats Larsen
 */
@CorrespondsTo("llvm::GlobalIndirectSymbol")
public open class GlobalIndirectSymbol constructor(ptr: LLVMValueRef) : GlobalValue(ptr)

/**
 * A constant array of values with the same type.
 *
 * To create a constant array, see the [Type.getConstantArray] method
 *
 * @see ConstantAggregate
 *
 * @author Mats Larsen
 */
@CorrespondsTo("llvm::ConstantArray")
public class ConstantArray public constructor(ptr: LLVMValueRef) : ConstantAggregate(ptr)

/**
 * A constant vector of values with the same type
 *
 * To create a constant vector, see the [Type.getConstantVector] method
 *
 * @see ConstantAggregate
 *
 * @author Mats Larsen
 */
@CorrespondsTo("llvm::Vector")
public class ConstantVector public constructor(ptr: LLVMValueRef) : ConstantAggregate(ptr)

/**
 * A constant structure aggregate consisting of values of various types.
 *
 * Both named structs and anonymous structs are represented as constant structs.
 *
 * @see ConstantAggregate
 *
 * @author Mats Larsen
 */
@CorrespondsTo("llvm::ConstantStruct")
public class ConstantStruct public constructor(ptr: LLVMValueRef) : ConstantAggregate(ptr)

/**
 * A single, constant integer value
 *
 * This is a shared class for both integral numbers and booleans in LLVM, because LLVM represents boolean values as
 * single bit integers.
 *
 * @see ConstantData
 *
 * @author Mats Larsen
 */
@CorrespondsTo("llvm::ConstantInt")
public class ConstantInt public constructor(ptr: LLVMValueRef) : ConstantData(ptr) {
    public fun getZeroExtendedValue(): Long {
        return LLVM.LLVMConstIntGetZExtValue(ref)
    }

    public fun getSignExtendedValue(): Long {
        return LLVM.LLVMConstIntGetSExtValue(ref)
    }
}

/**
 * A single, constant floating point value
 *
 * @see ConstantData
 *
 * @author Mats Larsen
 */
@CorrespondsTo("llvm::ConstantFP")
public class ConstantFP public constructor(ptr: LLVMValueRef) : ConstantData(ptr) {
    /**
     * Retrieve the value as a Kotlin double.
     *
     * @return pair of value and boolean indicating if conversion was lossy.
     */
    public fun getValue(): Pair<Double, Boolean> {
        val ptr = IntPointer(1L)
        val double = LLVM.LLVMConstRealGetDouble(ref, ptr)
        val lossy = ptr.get()

        return Pair(double, lossy.toBoolean())
    }
}

@CorrespondsTo("llvm::ConstantAggregateZero")
public class ConstantAggregateZero public constructor(ptr: LLVMValueRef) : ConstantData(ptr)

@CorrespondsTo("llvm::ConstantPointerNull")
public class ConstantPointerNull public constructor(ptr: LLVMValueRef) : ConstantData(ptr)

@CorrespondsTo("llvm::ConstantTokenNone")
public class ConstantTokenNone public constructor(ptr: LLVMValueRef) : ConstantData(ptr)

@CorrespondsTo("llvm::UndefValue")
public class UndefValue public constructor(ptr: LLVMValueRef) : ConstantData(ptr)

@CorrespondsTo("llvm::BlockAddress")
public class BlockAddress public constructor(ptr: LLVMValueRef) : Constant(ptr)

/**
 * A single incoming formal argument to a function
 *
 * Because this is a "formal" value, it doesn't contain an actual value, but instead represents the type, index, name
 * and attributes of the incoming argument.
 *
 * @author Mats Larsen
 */
@CorrespondsTo("llvm::Argument")
public class Argument public constructor(ptr: LLVMValueRef) : Value(ptr), Value.HasName {
    public fun getParent(): Function {
        val fn = LLVM.LLVMGetParamParent(ref)

        return Function(fn)
    }

    public fun setAlignment(alignment: Int) {
        LLVM.LLVMSetParamAlignment(ref, alignment)
    }
}

/**
 * A single function/procedure in an LLVM program
 *
 * A function is a procedure consisting of a set of basic blocks which make up the control flow graph of a program.
 * They also have a list of arguments and a local symbol table.
 *
 * @see Argument
 * @see BasicBlock
 *
 * TODO: Iterators - Parameter iterator
 * TODO: Iterators - BasicBlock iterator
 * TODO: Research - Are AppendBasicBlockInContext, InsertBasicBlockInContext necessary? they are alt constructors
 * TODO: Testing - Test attributes once Builder is stable (see Inkwell tests)
 *
 * @author Mats Larsen
 */
@CorrespondsTo("llvm::Function")
public class Function public constructor(ptr: LLVMValueRef) :
    GlobalObject(ptr),
    Value.HasDebugLocation,
    Value.HasName {
    public fun delete() {
        LLVM.LLVMDeleteFunction(ref)
    }

    public fun hasPersonalityFunction(): Boolean {
        return LLVM.LLVMHasPersonalityFn(ref).toBoolean()
    }

    public fun getPersonalityFunction(): Option<Function> = if (hasPersonalityFunction()) {
        val function = LLVM.LLVMGetPersonalityFn(ref)

        Some(Function(function))
    } else {
        None
    }

    public fun setPersonalityFunction(fn: Function) {
        LLVM.LLVMSetPersonalityFn(ref, fn.ref)
    }

    public fun getCallConvention(): CallConvention {
        val cc = LLVM.LLVMGetFunctionCallConv(ref)

        return CallConvention.from(cc).get()
    }

    public fun setCallConvention(cc: CallConvention) {
        LLVM.LLVMSetFunctionCallConv(ref, cc.value)
    }

    public fun getGC(): Option<String> {
        val gc = LLVM.LLVMGetGC(ref)

        return if (gc != null) {
            val copy = gc.string

            gc.deallocate()

            Some(copy)
        } else {
            None
        }
    }

    public fun setGC(gc: String) {
        LLVM.LLVMSetGC(ref, gc)
    }

    public fun getAttributeCount(index: AttributeIndex): Int {
        return LLVM.LLVMGetAttributeCountAtIndex(ref, index.value)
    }

    public fun getAttributes(index: AttributeIndex): Array<Attribute> {
        val size = getAttributeCount(index)
        val buffer = PointerPointer<LLVMAttributeRef>(size.toLong())

        LLVM.LLVMGetAttributesAtIndex(ref, index.value, buffer)

        return List(size) {
            LLVMAttributeRef(buffer.get(it.toLong()))
        }.map(::Attribute).toTypedArray().also {
            buffer.deallocate()
        }
    }

    public fun getEnumAttribute(index: AttributeIndex, kind: Int): Attribute {
        val attr = LLVM.LLVMGetEnumAttributeAtIndex(ref, index.value, kind)

        return Attribute(attr)
    }

    public fun getStringAttribute(index: AttributeIndex, kind: String): Attribute {
        val attr = LLVM.LLVMGetStringAttributeAtIndex(ref, index.value, kind, kind.length)

        return Attribute(attr)
    }

    public fun addAttribute(index: AttributeIndex, attribute: Attribute) {
        LLVM.LLVMAddAttributeAtIndex(ref, index.value, attribute.ref)
    }

    public fun addTargetDependentAttribute(kind: String, value: String) {
        LLVM.LLVMAddTargetDependentFunctionAttr(ref, kind, value)
    }

    public fun getParameterCount(): Int {
        return LLVM.LLVMCountParams(ref)
    }

    public fun getParameters(): Array<Argument> {
        val size = getParameterCount()
        val buffer = PointerPointer<LLVMValueRef>(size.toLong())

        LLVM.LLVMGetParams(ref, buffer)

        return List(size) {
            LLVMValueRef(buffer.get(it.toLong()))
        }.map(::Argument).toTypedArray().also {
            buffer.deallocate()
        }
    }

    public fun getParameter(index: Int): Result<Argument> = tryWith {
        assert(index < getParameterCount()) { "Index $index out of bounds for size of ${getParameterCount()}" }

        val parameter = LLVM.LLVMGetParam(ref, index)

        Argument(parameter)
    }

    public fun getBasicBlockCount(): Int {
        return LLVM.LLVMCountBasicBlocks(ref)
    }

    public fun getBasicBlocks(): Array<BasicBlock> {
        val size = getBasicBlockCount()
        val buffer = PointerPointer<LLVMBasicBlockRef>(size.toLong())

        LLVM.LLVMGetBasicBlocks(ref, buffer)

        return List(size) {
            LLVMBasicBlockRef(buffer.get(it.toLong()))
        }.map(::BasicBlock).toTypedArray().also {
            buffer.deallocate()
        }
    }

    /**
     * Get the entry block in a function
     *
     * This has some unexpected behavior; functions without basic blocks return a new empty basic block instead of
     * null/none.
     */
    public fun getEntryBasicBlock(): BasicBlock {
        val bb = LLVM.LLVMGetEntryBasicBlock(ref)

        return BasicBlock(bb)
    }

    public fun addBasicBlock(block: BasicBlock) {
        LLVM.LLVMAppendExistingBasicBlock(ref, block.ref)
    }
}

/**
 * A single global indirect function in an LLVM program
 *
 * This represents an indirect function in the LLVM IR of a program. Indirect functions use ELF symbol type extension
 * to mark that6 the address of a declaration should be resolved at runtime by calling a resolver function.
 *
 * @author Mats Larsen
 */
@CorrespondsTo("llvm::GlobalIFunc")
public class GlobalIndirectFunction public constructor(ptr: LLVMValueRef) : GlobalIndirectSymbol(ptr) {
    public fun getResolver(): Option<Function> {
        val resolver = LLVM.LLVMGetGlobalIFuncResolver(ref)

        return resolver?.let { Some(Function(it)) } ?: None
    }

    public fun setResolver(resolver: Function) {
        LLVM.LLVMSetGlobalIFuncResolver(ref, resolver.ref)
    }

    public fun detach() {
        LLVM.LLVMEraseGlobalIFunc(ref)
    }

    public fun delete() {
        LLVM.LLVMRemoveGlobalIFunc(ref)
    }

    public fun hasResolver(): Boolean = getResolver().isDefined()
}

/**
 * A single global alias in an LLVM program
 *
 * Global aliases are essentially pointers to other global objects in an LLVM program. A global alias points to a
 * single function or global variable.
 *
 * @see Function
 * @see GlobalVariable
 *
 * TODO: Research - Replace getValue/setValue return type with GlobalObject?
 *
 * @author Mats Larsen
 */
@CorrespondsTo("llvm::GlobalAlias")
public class GlobalAlias public constructor(ptr: LLVMValueRef) : GlobalIndirectSymbol(ptr) {
    public fun getValue(): Constant {
        val value = LLVM.LLVMAliasGetAliasee(ref)

        return Constant(value)
    }

    public fun setValue(value: Constant) {
        LLVM.LLVMAliasSetAliasee(ref, value.ref)
    }
}

/**
 * A single global variable in an LLVM program
 *
 * This is a single constant value which are constant pointers to a value allocated by either the VM or the linker in
 * a static compiler.
 *
 * Global variables may have initial values which are then copied into the .data section of executables.
 *
 * Global constants must have initializers to form a well-formed program.
 *
 * @author Mats Larsen
 */
@CorrespondsTo("llvm::GlobalVariable")
public class GlobalVariable public constructor(ptr: LLVMValueRef) :
    GlobalObject(ptr),
    Value.HasDebugLocation {
    public fun delete() {
        LLVM.LLVMDeleteGlobal(ref)
    }

    public fun getInitializer(): Option<Constant> {
        val value = LLVM.LLVMGetInitializer(ref)

        return value?.let { Some(Constant(it)) } ?: None
    }

    public fun setInitializer(value: Constant) {
        LLVM.LLVMSetInitializer(ref, value.ref)
    }

    public fun isThreadLocal(): Boolean {
        return LLVM.LLVMIsThreadLocal(ref).toBoolean()
    }

    public fun setThreadLocal(isThreadLocal: Boolean) {
        LLVM.LLVMSetThreadLocal(ref, isThreadLocal.toInt())
    }

    public fun isImmutable(): Boolean {
        return LLVM.LLVMIsGlobalConstant(ref).toBoolean()
    }

    public fun setImmutable(isImmutable: Boolean) {
        LLVM.LLVMSetGlobalConstant(ref, isImmutable.toInt())
    }

    public fun getThreadLocalMode(): ThreadLocalMode {
        val mode = LLVM.LLVMGetThreadLocalMode(ref)

        return ThreadLocalMode.from(mode).get()
    }

    public fun setThreadLocalMode(mode: ThreadLocalMode) {
        LLVM.LLVMSetThreadLocal(ref, mode.value)
    }

    public fun isExternallyInitialized(): Boolean {
        return LLVM.LLVMIsExternallyInitialized(ref).toBoolean()
    }

    public fun setExternallyInitialized(isExternallyInitialized: Boolean) {
        LLVM.LLVMSetExternallyInitialized(ref, isExternallyInitialized.toInt())
    }
}

/**
 * A constant value which is initialized with an expression using other constant values.
 *
 * This is a constant value which is the result of a computation of other constant values. The available computations
 * are the instructions defined in LLVMs instruction set.
 *
 * Constant expression types can be recognized using [ConstantExpression.getOpcode].
 *
 * @author Mats Larsen
 */
public class ConstantExpression constructor(ptr: LLVMValueRef) : Constant(ptr) {
    public fun getOpcode(): Opcode {
        val opcode = LLVM.LLVMGetConstOpcode(ref)

        return Opcode.from(opcode).get()
    }
}

/**
 * TODO: Research - LLVMSetAlignment and GetAlignment on Alloca, Load and Store
 */
public open class Instruction constructor(ptr: LLVMValueRef) : User(ptr), Value.HasDebugLocation {
    public interface Atomic : Owner<LLVMValueRef>
    public interface CallBase : Owner<LLVMValueRef>
    public interface FuncletPad : Owner<LLVMValueRef>
    public interface MemoryAccessor : Owner<LLVMValueRef>
    public interface Terminator : Owner<LLVMValueRef>

    public fun hasMetadata(): Boolean {
        return LLVM.LLVMHasMetadata(ref).toBoolean()
    }

    public fun getMetadata(kindId: Int): Option<MetadataAsValue> {
        val md = LLVM.LLVMGetMetadata(ref, kindId)

        return md?.let { Some(MetadataAsValue(it)) } ?: None
    }

    public fun setMetadata(kindId: Int, node: MetadataAsValue) {
        LLVM.LLVMSetMetadata(ref, kindId, node.ref)
    }
}

public class AtomicCmpXchgInstruction public constructor(ptr: LLVMValueRef) : Instruction(ptr)
public class AtomicRMWInstruction
// public class BinaryOperatorInstruction

public open class BranchInst
public class InvokeInstruction
public class CallBrInstruction
public class CallInstruction

public class CatchReturnInstruction
public class CatchSwitchInstruction
public class CleanupReturnInstruction

// @interface CmpInst
public class ICmpInstruction
public class FCmpInstruction

public class ExtractElementInstruction
public class FenceInstruction

// @interface FuncletPadInst
public class CatchPadInstruction
public class CleanupPadInstruction

public class GetElementPtrInstruction
public class IndirectBrInstruction
public class InsertElementInstruction
public class InsertValueInstruction
public class LandingPadInstruction
public class PhiInstruction
public class ResumeInstruction
public class ReturnInstruction
public class SelectInstruction
public class ShuffleVectorInstruction
public class StoreInstruction
public class SwitchInstruction

// @interface UnaryInstruction - UnaryOperator
public class AllocaInstruction
public class LoadInstruction
public class VAArgInstruction

public class UnreachableInstruction

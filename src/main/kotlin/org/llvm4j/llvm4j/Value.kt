package org.llvm4j.llvm4j

import org.bytedeco.javacpp.IntPointer
import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.javacpp.SizeTPointer
import org.bytedeco.llvm.LLVM.LLVMAttributeRef
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
 * A core object in LLVMs object model
 *
 * This is a base implementation for all values computed by a program that may be used as operands to other values.
 *
 * All values have a [Use] list which keeps track of which other [Value]s use this value.
 *
 * TODO: Testing - Test [dump] somehow?
 * TODO: LLVM 12.x - LLVMIsPoison
 * TODO: Testing - Test [replace]
 *
 * @author Mats Larsen
 */
@CorrespondsTo("llvm::Value")
public sealed class Value constructor(ptr: LLVMValueRef) : Owner<LLVMValueRef> {
    public override val ref: LLVMValueRef = ptr

    public fun getType(): AnyType {
        val type = LLVM.LLVMTypeOf(ref)

        return AnyType(type)
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

    /**
     * Represents any [Value] which has retrievable debug locations
     *
     * Known inheritors are [Instruction], [GlobalVariable] and [Function]
     *
     * TODO: Testing - Test once debug metadata is stable
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
     * Represents a value which may have a name
     *
     * Known inheritors are [Constant.GlobalValue], [Argument] and [Instruction]. Function also inherits this trait
     * implicitly through GlobalValue.
     *
     * @author Mats Larsen
     */
    @InternalApi
    public interface Nameable : Owner<LLVMValueRef> {
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

    public fun toAnyValue(): AnyValue = AnyValue(ref)
}

public class AnyValue public constructor(ptr: LLVMValueRef) : Value(ptr)

/**
 * A value which might use another value
 *
 * @see Use
 *
 * TODO: Testing - Test once values are more usable (see LLVM test suite, asmparser)
 *
 * @author Mats Larsen
 */
@CorrespondsTo("llvm::User")
public sealed class User constructor(ptr: LLVMValueRef) : Value(ptr) {
    public fun toAnyUser(): AnyUser = AnyUser(ref)

    public fun getOperand(index: Int): Result<AnyValue> = tryWith {
        assert(index < getOperandCount()) { "Index $index is out of bounds for size of ${getOperandCount()}" }

        val ptr = LLVM.LLVMGetOperand(ref, index)

        AnyValue(ptr)
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

public class AnyUser public constructor(ptr: LLVMValueRef) : User(ptr)

@CorrespondsTo("llvm::BasicBlock")
public class BasicBlock public constructor(ptr: LLVMValueRef) : Value(ptr)

/**
 * A [Metadata] disguised as a [Value]
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

@CorrespondsTo("llvm::Constant")
public sealed class Constant constructor(ptr: LLVMValueRef) : User(ptr) {
    public fun isNull(): Boolean {
        return LLVM.LLVMIsNull(ref).toBoolean()
    }

    @InternalApi
    public interface Aggregate : Owner<LLVMValueRef>
}

public class AnyConstant public constructor(ptr: LLVMValueRef) : Constant(ptr)

/**
 * Represents any value which is globally declared inside a [Module]
 *
 * TODO: LLVM 12.x - LLVMIsDeclaration()
 * TODO: Testing - Test metadata once metadata is stable
 *
 * @author Mats Larsen
 */
@CorrespondsTo("llvm::GlobalValue", "llvm::GlobalObject")
public sealed class GlobalValue constructor(ptr: LLVMValueRef) :
    Constant(ptr),
    Owner<LLVMValueRef>,
    Value.Nameable {
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

    public fun getValueType(): AnyType {
        val type = LLVM.LLVMGlobalGetValueType(ref)

        return AnyType(type)
    }

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

public class ConstantArray public constructor(ptr: LLVMValueRef) : Constant(ptr), Constant.Aggregate
public class ConstantVector public constructor(ptr: LLVMValueRef) : Constant(ptr), Constant.Aggregate
public class ConstantStruct public constructor(ptr: LLVMValueRef) : Constant(ptr), Constant.Aggregate

/**
 * Representation of a constant integer or boolean
 *
 * Booleans in LLVM are integers of type i1
 *
 * @author Mats Larsen
 */
@CorrespondsTo("llvm::ConstantInt")
public class ConstantInt public constructor(ptr: LLVMValueRef) : Constant(ptr) {
    public fun getZeroExtendedValue(): Long {
        return LLVM.LLVMConstIntGetZExtValue(ref)
    }

    public fun getSignExtendedValue(): Long {
        return LLVM.LLVMConstIntGetSExtValue(ref)
    }
}

/**
 * Representation of a floating point constant
 *
 * @author Mats Larsen
 */
@CorrespondsTo("llvm::ConstantFP")
public class ConstantFloat public constructor(ptr: LLVMValueRef) : Constant(ptr) {
    public fun getValue(): Pair<Double, Boolean> {
        val ptr = IntPointer(1L)
        val double = LLVM.LLVMConstRealGetDouble(ref, ptr)
        val lossy = ptr.get()

        return Pair(double, lossy.toBoolean())
    }
}

public class ConstantAggregateZero public constructor(ptr: LLVMValueRef) : Constant(ptr)
public class ConstantPointerNull public constructor(ptr: LLVMValueRef) : Constant(ptr)
public class ConstantTokenNone public constructor(ptr: LLVMValueRef) : Constant(ptr)
public class UndefValue public constructor(ptr: LLVMValueRef) : Constant(ptr)

public class BlockAddress public constructor(ptr: LLVMValueRef) : Constant(ptr)

@CorrespondsTo("llvm::Argument")
public class Argument public constructor(ptr: LLVMValueRef) : Value(ptr), Value.Nameable {
    public fun getParent(): Function {
        val fn = LLVM.LLVMGetParamParent(ref)

        return Function(fn)
    }

    public fun setAlignment(alignment: Int) {
        LLVM.LLVMSetParamAlignment(ref, alignment)
    }
}

/**
 * Represents a single procedure in a [Module]
 *
 * TODO: Iterators - Parameter iterator
 * TODO: Testing - Test attributes once Builder is stable (see Inkwell tests)
 *
 * @author Mats Larsen
 */
@CorrespondsTo("llvm::Function")
public class Function public constructor(ptr: LLVMValueRef) :
    GlobalValue(ptr),
    Value.HasDebugLocation,
    Value.Nameable {
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

    public fun getAttributes(index: AttributeIndex): Array<AnyAttribute> {
        val size = getAttributeCount(index)
        val buffer = PointerPointer<LLVMAttributeRef>(size.toLong())

        LLVM.LLVMGetAttributesAtIndex(ref, index.value, buffer)

        return List(size) {
            LLVMAttributeRef(buffer.get(it.toLong()))
        }.map(::AnyAttribute).toTypedArray().also {
            buffer.deallocate()
        }
    }

    public fun getEnumAttribute(index: AttributeIndex, kind: Int): EnumAttribute {
        val attr = LLVM.LLVMGetEnumAttributeAtIndex(ref, index.value, kind)

        return EnumAttribute(attr)
    }

    public fun getStringAttribute(index: AttributeIndex, kind: String): StringAttribute {
        val attr = LLVM.LLVMGetStringAttributeAtIndex(ref, index.value, kind, kind.length)

        return StringAttribute(attr)
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
}

public class GlobalIndirectFunction public constructor(ptr: LLVMValueRef) : GlobalValue(ptr) {
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

@CorrespondsTo("llvm::GlobalAlias")
public class GlobalAlias public constructor(ptr: LLVMValueRef) : GlobalValue(ptr) {
    public fun getValue(): AnyConstant {
        val value = LLVM.LLVMAliasGetAliasee(ref)

        return AnyConstant(value)
    }

    public fun setValue(value: Constant) {
        LLVM.LLVMAliasSetAliasee(ref, value.ref)
    }
}

public class GlobalVariable public constructor(ptr: LLVMValueRef) :
    GlobalValue(ptr),
    Value.HasDebugLocation {
    public fun delete() {
        LLVM.LLVMDeleteGlobal(ref)
    }

    public fun getInitializer(): Option<AnyConstant> {
        val value = LLVM.LLVMGetInitializer(ref)

        return value?.let { Some(AnyConstant(it)) } ?: None
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
 * TODO: Research - LLVMSetAlignment and GetAlignment on Alloca, Load and Store
 */
public sealed class Instruction constructor(ptr: LLVMValueRef) : User(ptr), Value.HasDebugLocation {
    public interface Atomic : Owner<LLVMValueRef>
    public interface CallBase : Owner<LLVMValueRef>
    public interface FuncletPad : Owner<LLVMValueRef>
    public interface MemoryAccessor : Owner<LLVMValueRef>
    public interface Terminator : Owner<LLVMValueRef>
}

public class AllocaInstruction
public class AtomicCmpXchgInstruction
public class AtomicRMWInstruction
public class BrInstruction
public class CallBrInstruction
public class CatchPadInstruction
public class CatchRetInstruction
public class CatchSwitchInstruction
public class CleanupPadInstruction
public class CleanupRetInstruction
public class FenceInstruction
public class IndirectBrInstruction
public class InvokeInstruction
public class LandingPadInstruction
public class LoadInstruction
public class PhiInstruction
public class ResumeInstruction
public class RetInstruction
public class SelectInstruction
public class StoreInstruction
public class SwitchInstruction
public class UnreachableInstruction
public class VAArgInstruction
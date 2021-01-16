package org.llvm4j.llvm4j

import org.bytedeco.javacpp.IntPointer
import org.bytedeco.javacpp.SizeTPointer
import org.bytedeco.llvm.LLVM.LLVMUseRef
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
import org.llvm4j.llvm4j.util.tryWith
import java.nio.file.Path

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
 * TODO: Testing - Ensure GlobalValue names are properly set
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

            return Path.of(copy)
        }

        public fun getDebugDirectory(): Path {
            val size = IntPointer(1L)
            val ptr = LLVM.LLVMGetDebugLocDirectory(ref, size)
            val copy = ptr.string

            ptr.deallocate()
            size.deallocate()

            return Path.of(copy)
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
 * TODO: Testing - Test once values are more usable (see LLVM test suite)
 *
 * @author Mats Larsen
 */
@CorrespondsTo("llvm::User")
public sealed class User constructor(ptr: LLVMValueRef) : Value(ptr) {
    public fun toAnyUser(): AnyUser = AnyUser(ref)

    public fun getOperand(index: Int): Result<AnyValue> = tryWith {
        assert(index < getOperandCount()) { "Index $index is out of bounds for size of ${getOperandCount()}"}

        val ptr = LLVM.LLVMGetOperand(ref, index)

        AnyValue(ptr)
    }

    public fun getOperandUse(index: Int): Result<Use> = tryWith {
        assert(index < getOperandCount()) { "Index $index is out of bounds for size of ${getOperandCount()}"}

        val use = LLVM.LLVMGetOperandUse(ref, index)

        Use(use)
    }

    public fun setOperand(index: Int, value: Value): Result<Unit> = tryWith {
        assert(index < getOperandCount()) { "Index $index is out of bounds for size of ${getOperandCount()}"}
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

    @CorrespondsTo("llvm::GlobalValue")
    public interface GlobalValue : Owner<LLVMValueRef>
}

public class AnyConstant public constructor(ptr: LLVMValueRef) : Constant(ptr)

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

public class Function public constructor(ptr: LLVMValueRef) : Constant(ptr), Constant.GlobalValue, Value.HasDebugLocation
public class GlobalIndirectFunction public constructor(ptr: LLVMValueRef) : Constant(ptr), Constant.GlobalValue
public class GlobalAlias public constructor(ptr: LLVMValueRef) : Constant(ptr), Constant.GlobalValue
public class GlobalVariable public constructor(ptr: LLVMValueRef) : Constant(ptr), Constant.GlobalValue, Value.HasDebugLocation

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
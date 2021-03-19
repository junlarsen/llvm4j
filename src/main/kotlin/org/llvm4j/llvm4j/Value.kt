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
import org.llvm4j.llvm4j.util.Enumeration
import org.llvm4j.llvm4j.util.InternalApi
import org.llvm4j.llvm4j.util.Owner
import org.llvm4j.llvm4j.util.toBoolean
import org.llvm4j.llvm4j.util.toInt
import org.llvm4j.optional.None
import org.llvm4j.optional.Option
import org.llvm4j.optional.Result
import org.llvm4j.optional.Some
import org.llvm4j.optional.result
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

        return ValueKind.from(kind).unwrap()
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

        return Option.of(use).map { Use(it) }
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
    public fun getOperand(index: Int): Result<Value, AssertionError> = result {
        assert(index < getOperandCount()) { "Index $index is out of bounds for size of ${getOperandCount()}" }

        val ptr = LLVM.LLVMGetOperand(ref, index)

        Value(ptr)
    }

    public fun getOperandUse(index: Int): Result<Use, AssertionError> = result {
        assert(index < getOperandCount()) { "Index $index is out of bounds for size of ${getOperandCount()}" }

        val use = LLVM.LLVMGetOperandUse(ref, index)

        Use(use)
    }

    public fun setOperand(index: Int, value: Value): Result<Unit, AssertionError> = result {
        assert(index < getOperandCount()) { "Index $index is out of bounds for size of ${getOperandCount()}" }
        assert(!isConstant()) { "Cannot mutate a constant with setOperand" }
        assert(isa<GlobalValue>(this)) { "Cannot mutate a constant with setOperand" }

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

        return Option.of(fn).map { Function(it) }
    }

    /**
     * Moves this block relative to the other provided block. New position is based off of [order].
     *
     * @see MoveOrder
     */
    public fun move(order: MoveOrder, target: BasicBlock) {
        when (order) {
            MoveOrder.Before -> LLVM.LLVMMoveBasicBlockBefore(ref, target.ref)
            MoveOrder.After -> LLVM.LLVMMoveBasicBlockAfter(ref, target.ref)
        }
    }

    public fun delete() {
        LLVM.LLVMDeleteBasicBlock(ref)
    }

    public fun erase() {
        LLVM.LLVMRemoveBasicBlockFromParent(ref)
    }
}

/**
 * Enumeration representing where two objects are moved relative to eachother.
 *
 * @author Mats Larsen
 */
public enum class MoveOrder(public override val value: Int) : Enumeration.EnumVariant {
    Before(0),
    After(1);
    public companion object : Enumeration<MoveOrder>(values())
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

    /**
     * Common implementation for any constant value which can perform integer based constant expression operations on
     * them.
     *
     * This exists because most of LLVMs instruction set operations which are performable on integers may also be
     * used on vectors of integers.
     *
     * Known inheritors are [ConstantInt] and [ConstantVector]
     *
     * @author Mats Larsen
     */
    @InternalApi
    public interface IntegerMathConstant : Owner<LLVMValueRef> {
        public fun getIntNeg(rhs: Constant, semantics: WrapSemantics): Constant = TODO("helper api")

        /**
         * Create an addition constexpr
         *
         * The `add` instruction adds two integer or vector-of-integer operands
         *
         * The [semantics] decide how LLVM should handle integer overflow. If a semantic rule is specified and the value
         * does overflow, a poison value is returned
         *
         * @param rhs       right hand side integer to add
         * @param semantics wrapping semantics upon overflow
         */
        public fun getIntAdd(rhs: Constant, semantics: WrapSemantics): Constant {
            val res = when (semantics) {
                WrapSemantics.NoUnsigned -> LLVM.LLVMConstNUWAdd(ref, rhs.ref)
                WrapSemantics.NoSigned -> LLVM.LLVMConstNSWAdd(ref, rhs.ref)
                WrapSemantics.Unspecified -> LLVM.LLVMConstAdd(ref, rhs.ref)
            }

            return Constant(res)
        }

        /**
         * Create a subtraction constexpr
         *
         * The `sub` instruction subtracts to integer or vector-of-integer operands
         *
         * The [semantics] decide how LLVM should handle integer overflow. If a semantic rule is specified and the value
         * does overflow, a poison value is returned
         *
         * @param rhs       how much to subtract
         * @param semantics wrapping semantics upon overflow
         */
        public fun getIntSub(rhs: Constant, semantics: WrapSemantics): Constant {
            val res = when (semantics) {
                WrapSemantics.NoUnsigned -> LLVM.LLVMConstNUWSub(ref, rhs.ref)
                WrapSemantics.NoSigned -> LLVM.LLVMConstNSWSub(ref, rhs.ref)
                WrapSemantics.Unspecified -> LLVM.LLVMConstSub(ref, rhs.ref)
            }

            return Constant(res)
        }

        /**
         * Create a multiplication constexpr
         *
         * The `mul` instruction multiplies two integer or vector-of-integer operands
         *
         * The [semantics] decide how LLVM should handle integer overflow. If a semantic rule is specified and the value
         * does overflow, a poison value is returned
         *
         * @param rhs       right hand side integer to multiply
         * @param semantics wrapping semantics upon overflow
         */
        public fun getIntMul(rhs: Constant, semantics: WrapSemantics): Constant {
            val res = when (semantics) {
                WrapSemantics.NoUnsigned -> LLVM.LLVMConstNUWMul(ref, rhs.ref)
                WrapSemantics.NoSigned -> LLVM.LLVMConstNSWMul(ref, rhs.ref)
                WrapSemantics.Unspecified -> LLVM.LLVMConstMul(ref, rhs.ref)
            }

            return Constant(res)
        }

        /**
         * Create an unsigned integer division constexpr
         *
         * The `udiv` instruction divides two integer or vector-of-integer operands. The `udiv` instruction yields the
         * unsigned quotient of the two operands. Signed division is done with [getSignedDiv]
         *
         * @param divisor divisor integer value (the number dividend is being divided by)
         * @param exact   use llvm "exact" division (see language reference)
         */
        public fun getUnsignedDiv(divisor: Constant, exact: Boolean): Constant {
            val res = if (exact) {
                LLVM.LLVMConstExactUDiv(ref, divisor.ref)
            } else {
                LLVM.LLVMConstUDiv(ref, divisor.ref)
            }

            return Constant(res)
        }

        /**
         * Create a signed integer division constexpr
         *
         * The `sdiv` instruction divides the two integer or vector-of-integer operands. The `sdiv` instruction yields
         * the signed quotient of the two operands. Unsigned division is done with [getUnsignedDiv]
         *
         * @param divisor divisor integer value (the number dividend is being divided by)
         * @param exact   use llvm "exact" division (see language reference)
         */
        public fun getSignedDiv(divisor: Constant, exact: Boolean): Constant {
            val res = if (exact) {
                LLVM.LLVMConstExactSDiv(ref, divisor.ref)
            } else {
                LLVM.LLVMConstSDiv(ref, divisor.ref)
            }

            return Constant(res)
        }

        /**
         * Create an unsigned integer remainder constexpr
         *
         * The `urem` instruction returns the remainder from the unsigned division of its two integer or
         * vector-of-integer operands.
         *
         * @param divisor  divisor integer value (the number dividend is being divided by)
         */
        public fun getUnsignedRem(divisor: Constant): Constant {
            val res = LLVM.LLVMConstURem(ref, divisor.ref)

            return Constant(res)
        }

        /**
         * Create a signed integer remainder constexpr
         *
         * The `srem` instruction returns the remainder from the signed division of its two integer or vector-of-integer
         * operands.
         *
         * @param divisor divisor integer value (the number dividend is being divided by)
         */
        public fun getSignedRem(divisor: Constant): Constant {
            val res = LLVM.LLVMConstSRem(ref, divisor.ref)

            return Constant(res)
        }

        /**
         * Create a left shift constexpr
         *
         * The `shl` instruction shifts its first integer or vector-of-integer operand to the left a specified number of
         * bits
         *
         * @param bits number of bits to shift this to the left
         */
        public fun getLeftShift(bits: Constant): Constant {
            val res = LLVM.LLVMConstShl(ref, bits.ref)

            return Constant(res)
        }

        /**
         * Create a logical shift right constexpr
         *
         * The `lshr` instruction logically shifts its first integer or vector-of-integer operand to the right a
         * specified number of bits with zero fill.
         *
         * @param bits number of bits to shift this to the right
         */
        public fun getLogicalShiftRight(bits: Constant): Constant {
            val res = LLVM.LLVMConstLShr(ref, bits.ref)

            return Constant(res)
        }

        /**
         * Create an arithmetic shift right constexpr
         *
         * The `ashr` instruction arithmetically shifts its first integer or vector-of-integer operand to the right a
         * specified number of bits with sign extension.
         *
         * @param bits number of bits to shift this to the right
         */
        public fun getArithmeticShiftRight(bits: Constant): Constant {
            val res = LLVM.LLVMConstAShr(ref, bits.ref)

            return Constant(res)
        }

        /**
         * Create a logical and constexpr
         *
         * The `and` instruction returns the bitwise logical and of its two integer or vector-of-integer operands.
         *
         * @param rhs right hand side integer
         */
        public fun getLogicalAnd(rhs: Constant): Constant {
            val res = LLVM.LLVMConstAnd(ref, rhs.ref)

            return Constant(res)
        }

        /**
         * Create a logical or constexpr
         *
         * The `or` instruction returns the bitwise logical or of its two integer or vector-of-integer operands.
         *
         * @param rhs right hand side integer
         */
        public fun getLogicalOr(rhs: Constant): Constant {
            val res = LLVM.LLVMConstOr(ref, rhs.ref)

            return Constant(res)
        }

        /**
         * Create a logical xor constexpr
         *
         * The `xor` instruction returns the bitwise logical xor of its two integer or vector-of-integer operands.
         *
         * @param rhs right hand side integer
         */
        public fun getLogicalXor(rhs: Constant): Constant {
            val res = LLVM.LLVMConstXor(ref, rhs.ref)

            return Constant(res)
        }

        /**
         * Create an integer comparison constexpr
         *
         * The `icmp` instruction returns a boolean (i1) value based on comparison of two integer, vector-of-integer,
         * pointer or vector-of-pointer operands.
         *
         * @param predicate comparison operator to use
         * @param rhs       right hand side of comparison
         */
        public fun getIntCompare(predicate: IntPredicate, rhs: Constant): Constant {
            val res = LLVM.LLVMConstICmp(predicate.value, ref, rhs.ref)

            return Constant(res)
        }
    }

    /**
     * Common implementation for any constant value which can perform floating-point based constant expression
     * operations on them.
     *
     * This exists because most of LLVMs instruction set operations which are performable on floating-points may
     * also be used on vectors of floating-points.
     *
     * Known inheritors are [ConstantFP] and [ConstantVector]
     *
     * @author Mats Larsen
     */
    @InternalApi
    public interface FloatingPointMathConstant : Owner<LLVMValueRef> {
        /**
         * Create a float negation constexpr
         *
         * The `fneg` instruction negates a floating-point or a vector-of-floating-point operand
         *
         * The produced value is a copy of its operand with the sign bit flipped.
         */
        public fun getFloatNeg(): Constant {
            val res = LLVM.LLVMConstFNeg(ref)

            return Constant(res)
        }

        /**
         * Create a floating-point addition constexpr
         *
         * The `fadd` instruction adds two floating-point or vector-of-floating-point operands
         *
         * @param rhs right hand side floating-point to add
         */
        public fun getFloatAdd(rhs: Constant): Constant {
            val res = LLVM.LLVMConstFAdd(ref, rhs.ref)

            return Constant(res)
        }

        /**
         * Create a floating-point subtraction constexpr
         *
         * The `fsub` instruction subtracts two floating-point or vector-of-floating-point operands
         *
         * @param rhs how much to subtract
         */
        public fun getFloatSub(rhs: Constant): Constant {
            val res = LLVM.LLVMConstFSub(ref, rhs.ref)

            return Constant(res)
        }

        /**
         * Create a floating-point multiplication constexpr
         *
         * The `fmul` instruction multiplies two floating-point or vector-of-floating-point operands
         *
         * @param rhs right hand side floating-point to multiply
         */
        public fun getFloatMul(rhs: Constant): Constant {
            val res = LLVM.LLVMConstFMul(ref, rhs.ref)

            return Constant(res)
        }

        /**
         * Create a floating-point division constexpr
         *
         * The `fdiv` instruction divides the two floating-point or vector-of-floating-point operands.
         *
         * @param divisor divisor floating-point value (the number divided is being divided by)
         */
        public fun getFloatDiv(divisor: Constant): Constant {
            val res = LLVM.LLVMConstFDiv(ref, divisor.ref)

            return Constant(res)
        }

        /**
         * Create a floating-point remainder constexpr
         *
         * The `frem` instruction returns the remainder from the division of its floating-point or
         * vector-of-floating-point operands.
         *
         * @param divisor divisor floating-point value (the number dividend is being divided by)
         */
        public fun getFloatRem(divisor: Constant): Constant {
            val res = LLVM.LLVMConstFRem(ref, divisor.ref)

            return Constant(res)
        }

        /**
         * Create a floating-point comparison constexpr
         *
         * The `fcmp` instruction returns a boolean (i1) value based on comparison of two floating-point or
         * vector-of-floating-point operands.
         *
         * @param predicate comparison operator to use
         * @param rhs       right hand side of comparison
         */
        public fun getFloatCompare(predicate: FloatPredicate, rhs: Constant): Constant {
            val res = LLVM.LLVMConstFCmp(predicate.value, ref, rhs.ref)

            return Constant(res)
        }
    }

    /**
     * Common implementation for any constant value which can perform aggregate constant expression operations on them.
     *
     * Known inheritors are [ConstantStruct] and [ConstantArray]
     *
     * @author Mats Larsen
     */
    @InternalApi
    public interface AggregateConstant {
        public fun getExtractValue(index: Int): Constant = TODO()
        public fun getInsertValue(value: Constant, index: Int): Constant = TODO()
        public fun getGetElementPtr(vararg indices: Constant, isInBounds: Boolean): Constant = TODO()
    }

    /**
     * Common implementation for any constant value which is first-class.
     *
     * See LLVM documentation for first-class types https://llvm.org/docs/LangRef.html#t-firstclass
     *
     * Known inheritors are [ConstantInt], [ConstantFP], [ConstantVector]
     *
     * @author Mats Larsen
     */
    @InternalApi
    public interface FirstClassConstant {
        public fun getBitCast(type: Type): Constant = TODO()
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

        return Linkage.from(linkage).unwrap()
    }

    public fun setLinkage(linkage: Linkage) {
        LLVM.LLVMSetLinkage(ref, linkage.value)
    }

    public fun getSection(): Option<String> {
        val ptr = LLVM.LLVMGetSection(ref)

        return Option.of(ptr).map {
            val copy = it.string
            it.deallocate()
            copy
        }
    }

    public fun setSection(section: String) {
        LLVM.LLVMSetSection(ref, section)
    }

    public fun getVisibility(): Visibility {
        val visibility = LLVM.LLVMGetVisibility(ref)

        return Visibility.from(visibility).unwrap()
    }

    public fun setVisibility(visibility: Visibility) {
        LLVM.LLVMSetVisibility(ref, visibility.value)
    }

    public fun getStorageClass(): DLLStorageClass {
        val storage = LLVM.LLVMGetDLLStorageClass(ref)

        return DLLStorageClass.from(storage).unwrap()
    }

    public fun setStorageClass(storage: DLLStorageClass) {
        LLVM.LLVMSetDLLStorageClass(ref, storage.value)
    }

    public fun getUnnamedAddress(): UnnamedAddress {
        val addr = LLVM.LLVMGetUnnamedAddress(ref)

        return UnnamedAddress.from(addr).unwrap()
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

        public fun getKindId(index: Int): Result<Int, AssertionError> = result {
            assert(index < size()) { "Out of bounds index $index, size is ${size()}" }

            LLVM.LLVMValueMetadataEntriesGetKind(ref, index)
        }

        public fun getMetadata(index: Int): Result<Metadata, AssertionError> = result {
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
public class ConstantArray public constructor(ptr: LLVMValueRef) : ConstantAggregate(ptr), Constant.AggregateConstant

/**
 * A constant vector of values with the same type
 *
 * To create a constant vector, see the [Type.getConstantVector] method
 *
 * @see ConstantAggregate
 *
 * TODO: Research/ConstExpr - Find out where to place getPointerToInt overload
 *
 * @author Mats Larsen
 */
@CorrespondsTo("llvm::Vector")
public class ConstantVector public constructor(ptr: LLVMValueRef) :
    ConstantAggregate(ptr),
    Constant.IntegerMathConstant,
    Constant.FloatingPointMathConstant,
    Constant.FirstClassConstant {
    public fun getExtractElement(index: Constant): Constant = TODO()
    public fun getInsertElement(value: Constant, index: Constant): Constant = TODO()
    public fun getShuffleVector(rhs: ConstantVector, mask: ConstantVector): Constant = TODO()
    public fun getIntTrunc(type: VectorType): Constant = TODO()
    public fun getZeroExt(type: VectorType): Constant = TODO()
    public fun getSignExt(type: VectorType): Constant = TODO()
    public fun getFloatTrunc(type: VectorType): Constant = TODO()
    public fun getFloatExt(type: VectorType): Constant = TODO()
    public fun getFloatToSigned(type: VectorType): Constant = TODO()
    public fun getFloatToUnsigned(type: VectorType): Constant = TODO()
    public fun getSignedToFloat(type: VectorType): Constant = TODO()
    public fun getUnsignedToFloat(type: VectorType): Constant = TODO()
    public fun getIntToPointer(type: VectorType): Constant = TODO()
    public fun getPointerToInt(type: VectorType): Constant = TODO()
}

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
public class ConstantStruct public constructor(ptr: LLVMValueRef) : ConstantAggregate(ptr), Constant.AggregateConstant

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
public class ConstantInt public constructor(ptr: LLVMValueRef) :
    ConstantData(ptr),
    Constant.IntegerMathConstant,
    Constant.FirstClassConstant {
    public fun getZeroExtendedValue(): Long {
        return LLVM.LLVMConstIntGetZExtValue(ref)
    }

    public fun getSignExtendedValue(): Long {
        return LLVM.LLVMConstIntGetSExtValue(ref)
    }

    public fun getIntTrunc(type: IntegerType): Constant = TODO()
    public fun getZeroExt(type: IntegerType): Constant = TODO()
    public fun getSignExt(type: IntegerType): Constant = TODO()
    public fun getSignedToFloat(type: IntegerType): Constant = TODO()
    public fun getUnsignedToFloat(type: IntegerType): Constant = TODO()
    public fun getIntToPointer(type: PointerType): Constant = TODO()
    public fun getSelect(isTrue: Constant, isFalse: Constant): Constant = TODO()
}

/**
 * A single, constant floating point value
 *
 * @see ConstantData
 *
 * @author Mats Larsen
 */
@CorrespondsTo("llvm::ConstantFP")
public class ConstantFP public constructor(ptr: LLVMValueRef) :
    ConstantData(ptr),
    Constant.FloatingPointMathConstant,
    Constant.FirstClassConstant {
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

    public fun getFloatTrunc(type: FloatingPointType): Constant = TODO()
    public fun getFloatExt(type: FloatingPointType): Constant = TODO()
    public fun getFloatToSigned(type: IntegerType): Constant = TODO()
    public fun getFloatToUnsigned(type: IntegerType): Constant = TODO()
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

        return CallConvention.from(cc).unwrap()
    }

    public fun setCallConvention(cc: CallConvention) {
        LLVM.LLVMSetFunctionCallConv(ref, cc.value)
    }

    public fun getGC(): Option<String> {
        val gc = LLVM.LLVMGetGC(ref)

        return Option.of(gc).map {
            val copy = gc.string
            gc.deallocate()
            copy
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

    public fun getParameter(index: Int): Result<Argument, AssertionError> = result {
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

        return Option.of(resolver).map { Function(it) }
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

    public fun hasResolver(): Boolean = getResolver().isSome()
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

        return Option.of(value).map { Constant(it) }
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

        return ThreadLocalMode.from(mode).unwrap()
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

        return Opcode.from(opcode).unwrap()
    }
}

/**
 * TODO: Research - LLVMSetAlignment and GetAlignment on Alloca, Load and Store
 */
public open class Instruction constructor(ptr: LLVMValueRef) : User(ptr), Value.HasDebugLocation {
    public interface Atomic : Owner<LLVMValueRef>
    public interface CallBase : Owner<LLVMValueRef>
    public interface MemoryAccessor : Owner<LLVMValueRef>
    public interface Terminator : Owner<LLVMValueRef>

    public fun hasMetadata(): Boolean {
        return LLVM.LLVMHasMetadata(ref).toBoolean()
    }

    public fun getMetadata(kindId: Int): Option<MetadataAsValue> {
        val md = LLVM.LLVMGetMetadata(ref, kindId)

        return Option.of(md).map { MetadataAsValue(it) }
    }

    public fun setMetadata(kindId: Int, node: MetadataAsValue) {
        LLVM.LLVMSetMetadata(ref, kindId, node.ref)
    }

    /**
     * Insert the instruction at the given [builder]'s insertion point.
     *
     * The instruction may optionally receive a [name]
     */
    public fun insert(builder: IRBuilder, name: Option<String>) {
        when (name) {
            is Some -> LLVM.LLVMInsertIntoBuilderWithName(builder.ref, ref, name.unwrap())
            is None -> LLVM.LLVMInsertIntoBuilder(builder.ref, ref)
        }
    }
}

public class BinaryOperatorInstruction public constructor(ptr: LLVMValueRef) : Instruction(ptr)

public open class ComparisonInstruction public constructor(ptr: LLVMValueRef) : Instruction(ptr)
public class IntComparisonInstruction public constructor(ptr: LLVMValueRef) : ComparisonInstruction(ptr)
public class FPComparisonInstruction public constructor(ptr: LLVMValueRef) : ComparisonInstruction(ptr)

public open class FuncletPadInstruction public constructor(ptr: LLVMValueRef) : Instruction(ptr)
public class CatchPadInstruction public constructor(ptr: LLVMValueRef) : FuncletPadInstruction(ptr)
public class CleanupPadInstruction public constructor(ptr: LLVMValueRef) : FuncletPadInstruction(ptr)

public open class UnaryInstruction public constructor(ptr: LLVMValueRef) : Instruction(ptr)
public class AllocaInstruction public constructor(ptr: LLVMValueRef) : UnaryInstruction(ptr)
public open class CastInstruction public constructor(ptr: LLVMValueRef) : UnaryInstruction(ptr)
public class ExtractValueInstruction public constructor(ptr: LLVMValueRef) : UnaryInstruction(ptr)
public class LoadInstruction public constructor(ptr: LLVMValueRef) : UnaryInstruction(ptr)
public class VAArgInstruction public constructor(ptr: LLVMValueRef) : UnaryInstruction(ptr)
public class FreezeInstruction public constructor(ptr: LLVMValueRef) : UnaryInstruction(ptr)
public class UnaryOperator public constructor(ptr: LLVMValueRef) : UnaryInstruction(ptr)

public class AddrSpaceCastInstruction public constructor(ptr: LLVMValueRef) : CastInstruction(ptr)
public class BitCastInstruction public constructor(ptr: LLVMValueRef) : CastInstruction(ptr)
public class FloatExtInstruction public constructor(ptr: LLVMValueRef) : CastInstruction(ptr)
public class FloatToSignedInstruction public constructor(ptr: LLVMValueRef) : CastInstruction(ptr)
public class FloatToUnsignedInstruction public constructor(ptr: LLVMValueRef) : CastInstruction(ptr)
public class FloatTruncInstruction public constructor(ptr: LLVMValueRef) : CastInstruction(ptr)
public class IntToPtrInstruction public constructor(ptr: LLVMValueRef) : CastInstruction(ptr)
public class PtrToIntInstruction public constructor(ptr: LLVMValueRef) : CastInstruction(ptr)
public class SignedExtInstruction public constructor(ptr: LLVMValueRef) : CastInstruction(ptr)
public class SignedToFloatInstruction public constructor(ptr: LLVMValueRef) : CastInstruction(ptr)
public class IntTruncInstruction public constructor(ptr: LLVMValueRef) : CastInstruction(ptr)
public class UnsignedToFloatInstruction public constructor(ptr: LLVMValueRef) : CastInstruction(ptr)
public class ZeroExtInstruction public constructor(ptr: LLVMValueRef) : CastInstruction(ptr)

public class AtomicCmpXchgInstruction public constructor(ptr: LLVMValueRef) : Instruction(ptr)
public class AtomicRMWInstruction public constructor(ptr: LLVMValueRef) : Instruction(ptr)

public open class BranchInstruction public constructor(ptr: LLVMValueRef) : Instruction(ptr)
public class InvokeInstruction public constructor(ptr: LLVMValueRef) : BranchInstruction(ptr)
public class CallBrInstruction public constructor(ptr: LLVMValueRef) : BranchInstruction(ptr)
public class CallInstruction public constructor(ptr: LLVMValueRef) : BranchInstruction(ptr)

public class CatchReturnInstruction public constructor(ptr: LLVMValueRef) : Instruction(ptr)
public class CatchSwitchInstruction public constructor(ptr: LLVMValueRef) : Instruction(ptr)
public class CleanupReturnInstruction public constructor(ptr: LLVMValueRef) : Instruction(ptr)

public class ExtractElementInstruction public constructor(ptr: LLVMValueRef) : Instruction(ptr)
public class FenceInstruction public constructor(ptr: LLVMValueRef) : Instruction(ptr)

public class GetElementPtrInstruction public constructor(ptr: LLVMValueRef) : Instruction(ptr)
public class IndirectBrInstruction public constructor(ptr: LLVMValueRef) : Instruction(ptr)
public class InsertElementInstruction public constructor(ptr: LLVMValueRef) : Instruction(ptr)
public class InsertValueInstruction public constructor(ptr: LLVMValueRef) : Instruction(ptr)
public class LandingPadInstruction public constructor(ptr: LLVMValueRef) : Instruction(ptr)
public class PhiInstruction public constructor(ptr: LLVMValueRef) : Instruction(ptr)
public class ResumeInstruction public constructor(ptr: LLVMValueRef) : Instruction(ptr)
public class ReturnInstruction public constructor(ptr: LLVMValueRef) : Instruction(ptr)
public class SelectInstruction public constructor(ptr: LLVMValueRef) : Instruction(ptr)
public class ShuffleVectorInstruction public constructor(ptr: LLVMValueRef) : Instruction(ptr)
public class StoreInstruction public constructor(ptr: LLVMValueRef) : Instruction(ptr)
public class SwitchInstruction public constructor(ptr: LLVMValueRef) : Instruction(ptr)
public class UnreachableInstruction public constructor(ptr: LLVMValueRef) : Instruction(ptr)

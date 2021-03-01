package org.llvm4j.llvm4j

import org.bytedeco.llvm.LLVM.LLVMBuilderRef
import org.bytedeco.llvm.global.LLVM
import org.llvm4j.llvm4j.util.None
import org.llvm4j.llvm4j.util.Option
import org.llvm4j.llvm4j.util.Owner
import org.llvm4j.llvm4j.util.Some

public class IRBuilder public constructor(ptr: LLVMBuilderRef) : Owner<LLVMBuilderRef>, IRBuilderBase {
    public override val ref: LLVMBuilderRef = ptr

    /**
     * Move the builder's insertion point after the given [instruction] in a basic block, [label].
     */
    public fun position(label: BasicBlock, instruction: Instruction) {
        LLVM.LLVMPositionBuilder(ref, label.ref, instruction.ref)
    }

    /**
     * Move the builder's insertion point before the given [instruction]
     */
    public fun positionBefore(instruction: Instruction) {
        LLVM.LLVMPositionBuilderBefore(ref, instruction.ref)
    }

    /**
     * Move the builder's insertion point after the given [label]
     */
    public fun positionAfter(label: BasicBlock) {
        LLVM.LLVMPositionBuilderAtEnd(ref, label.ref)
    }

    /**
     * Get the basic bloc kof the insertion point for the builder
     *
     * Returns [None] if the insertion point was cleared using [clear] or is yet to be set.
     */
    public fun getInsertionBlock(): Option<BasicBlock> {
        val point = LLVM.LLVMGetInsertBlock(ref)

        return point?.let { Some(BasicBlock(it)) } ?: None
    }

    /**
     * Retrieve the current debug location, if set
     *
     * TODO: Research/DebugInfo - Find a more precise type to return (llvm::DebugLoc)
     */
    public fun getDebugLocation(): Option<Metadata> {
        val debugLocation = LLVM.LLVMGetCurrentDebugLocation2(ref)

        return debugLocation?.let { Some(Metadata(it)) } ?: None
    }

    /**
     * Set the current debug location
     *
     * TODO: Research/DebugInfo - Find a more precise type to return (llvm::DebugLoc)
     */
    public fun setDebugLocation(location: Metadata) {
        LLVM.LLVMSetCurrentDebugLocation2(ref, location.ref)
    }

    /**
     * Attempt to use the current debug location to set the debug location for the provided instruction.
     *
     * TODO: Research - Does this fail if there is no debug location?
     */
    public fun attachDebugLocation(instruction: Instruction) {
        LLVM.LLVMSetInstDebugLocation(ref, instruction.ref)
    }

    /**
     * Get the default floating-point math metadata
     *
     * TODO: Research - Does this return null when there is no default fpmath tag set?
     * TODO: Research - Can this type be narrowed down?
     */
    public fun getDefaultFPMathTag(): Metadata {
        val flags = LLVM.LLVMBuilderGetDefaultFPMathTag(ref)

        return Metadata(flags)
    }

    public fun setDefaultFPMathTag(flags: Metadata) {
        LLVM.LLVMBuilderSetDefaultFPMathTag(ref, flags.ref)
    }

    /**
     * Clears the insertion point of the builder
     */
    public fun clear() {
        LLVM.LLVMClearInsertionPosition(ref)
    }

    public override fun deallocate() {
        LLVM.LLVMDisposeBuilder(ref)
    }
}

/**
 * A base implementation of a subset of the basic LLVM instruction set
 *
 * The instructions which are not implemented are the oddly specific `callbr` and exception handling ones:
 *
 * - callbr
 * - cleanuppad
 * - catchpad
 * - landingpad
 * - cleanupret
 * - catchret
 * - catchswitch
 * - resume
 * - invoke
 *
 * This implementation does not implement most of the helper functions the LLVM C API provide, this is a lower level
 * interface implementing the instructions defined in the language reference.
 *
 * Any parent implementors are free to implement the helper functions or exception handling instructions mentioned.
 *
 * This class does not directly correspond to LLVMs IRBuilderBase, but is almost analogous as they have a lot of
 * similarities.
 *
 * **Note:** A lot of the functions return [Value] instead of instruction types. This goes for all the
 * constant expression values which are not guaranteed to become add instructions as the constant folder and optimizer
 * may fold them for optimization purposes.
 *
 * @author Mats Larsen
 */
public interface IRBuilderBase : Owner<LLVMBuilderRef> {
    /**
     * Build a return instruction
     *
     * The `ret` instruction exits control flow from the current function, optionally with a value. If you wish to
     * return a value from the terminator, pass in a [value]. If no value is passed, a `ret void` is made.
     *
     * The return type must be a first class type. See https://llvm.org/docs/LangRef.html#t-firstclass
     *
     * @param value value to return, returns void if [None]
     */
    public fun buildReturn(value: Option<Value>): ReturnInstruction = TODO()

    /**
     * Build an unconditional branch instruction
     *
     * The `br` instruction is used to cause control flow transfer to a different label in the current function. This
     * is an unconditional jump, see [buildConditionalBranch] for conditional jumps
     *
     * @param label label to jump to
     */
    public fun buildBranch(label: BasicBlock): BranchInstruction = TODO()

    /**
     * Build a conditional branch instruction
     *
     * The `br` instruction is used to cause control flow transfer to a different label in the current function. This
     * overload consumes a condition and two blocks. The condition is used to decide where control flow will transfer.
     *
     * @param condition condition value, must be i1 and not undef or poison
     * @param isTrue    label to jump to if the condition is true
     * @param isFalse   label to jump to if the condition is false
     */
    public fun buildConditionalBranch(
        condition: Value,
        isTrue: BasicBlock,
        isFalse: BasicBlock
    ): BranchInstruction = TODO()

    /**
     * Build a switch instruction
     *
     * The `switch` instruction selects a destination to transfer control flow to based on an integer comparison. The
     * instruction is a generalization of the branching instruction, allowing a branch to occur to one of many
     * possible destinations.
     *
     * The C API does not consume all the cases upon construction, instead we provide an expected amount of
     * destinations which LLVM will pre-allocate for optimization purposes. Cases can be appended to the returned
     * [SwitchInstruction] instance.
     *
     * @param condition     conditional integer value to compare
     * @param default       label to jump to if none of the cases match
     * @param expectedCases expected amount of switch cases to be appended
     */
    public fun buildSwitch(condition: Value, default: BasicBlock, expectedCases: Int): SwitchInstruction = TODO()

    /**
     * Build an indirect branch instruction
     *
     * The `indirectbr` instruction implements an indirect branch to a label within the current function.
     *
     * The C API does not consume all the possible destinations upon construction, instead we provide an expected
     * amount of possible destinations which LLVM will pre-allocate for optimization purposes. Destinations can be
     * appended to the returned [IndirectBrInstruction] instance.
     *
     * @param address       label to jump to
     * @param expectedCases expected amount of possible destinations to be appended
     */
    public fun buildIndirectBranch(address: BasicBlock, expectedCases: Int): IndirectBrInstruction = TODO()

    /**
     * Build an unreachable instruction
     *
     * The `unreachable` instruction does not have any semantics, it is a terminator which informs the optimizer that a
     * portion of the code is not reachable. This may be used to indicate that the code after a no-return function
     * cannot be reached.
     */
    public fun buildUnreachable(): UnreachableInstruction = TODO()

    /**
     * Build a float negation instruction
     *
     * The `fneg` instruction negates a floating-point or a vector-of-floating-point operand
     *
     * The produced value is a copy of its operand with the sign bit flipped.
     *
     * @param op1  floating-point or vector-of-floating-point to negate
     * @param name optional name for the instruction
     */
    public fun buildFloatNeg(op1: Value, name: Option<String>): Value = TODO()

    /**
     * Build an addition instruction
     *
     * The `add` instruction adds two integer or vector-of-integer operands
     *
     * The [semantics] decide how LLVM should handle integer overflow. If a semantic rule is specified and the value
     * does overflow, a poison value is returned
     *
     * @param op1       left hand side integer to add
     * @param op2       right hand side integer to add
     * @param semantics wrapping semantics upon overflow
     * @param name      optional name for the instruction
     */
    public fun buildIntAdd(op1: Value, op2: Value, semantics: WrapSemantics, name: Option<String>): Value = TODO()

    /**
     * Build a floating-point addition instruction
     *
     * The `fadd` instruction adds two floating-point or vector-of-floating-point operands
     *
     * @param op1  left hand side floating-point to add
     * @param op2  right hand side floating-point to add
     * @param name optional name for the instruction
     */
    public fun buildFloatAdd(op1: Value, op2: Value, name: Option<String>): Value = TODO()

    /**
     * Build a subtraction instruction
     *
     * The `sub` instruction subtracts to integer or vector-of-integer operands
     *
     * The [semantics] decide how LLVM should handle integer overflow. If a semantic rule is specified and the value
     * does overflow, a poison value is returned
     *
     * @param op1       integer to subtract from
     * @param op2       how much to subtract from [op1]
     * @param semantics wrapping semantics upon overflow
     * @param name      optional name for the instruction
     */
    public fun buildIntSub(op1: Value, op2: Value, semantics: WrapSemantics, name: Option<String>): Value = TODO()

    /**
     * Build a floating-point subtraction instruction
     *
     * The `fsub` instruction subtracts two floating-point or vector-of-floating-point operands
     *
     * @param op1  floating-point to subtract from
     * @param op2  how much to subtract from [op1]
     * @param name optional name for the instruction
     */
    public fun buildFloatSub(op1: Value, op2: Value, name: Option<String>): Value = TODO()

    /**
     * Build a multiplication instruction
     *
     * The `mul` instruction multiplies two integer or vector-of-integer operands
     *
     * The [semantics] decide how LLVM should handle integer overflow. If a semantic rule is specified and the value
     * does overflow, a poison value is returned
     *
     * @param op1  left hand side integer to multiply
     * @param op2  right hand side integer to multiply
     * @param semantics wrapping semantics upon overflow
     * @param name optional name for the instruction
     */
    public fun buildIntMul(op1: Value, op2: Value, semantics: WrapSemantics, name: Option<String>): Value = TODO()

    /**
     * Build a floating-point multiplication instruction
     *
     * The `fmul` instruction multiplies two floating-point or vector-of-floating-point operands
     *
     * @param op1  left hand side floating-point to multiply
     * @param op2  right hand side floating-point to multiply
     * @param name optional name for the instruction
     */
    public fun buildFloatMul(op1: Value, op2: Value, name: Option<String>): Value = TODO()

    /**
     * Build an unsigned integer division instruction
     *
     * The `udiv` instruction divides two integer or vector-of-integer operands. The `udiv` instruction yields the
     * unsigned quotient of the two operands. Signed division is done with [buildSignedDiv]
     *
     * @param op1  dividend integer value (value being divided)
     * @param op2  divisor integer value (the number dividend is being divided by)
     * @param name optional name for the instruction
     */
    public fun buildUnsignedDiv(op1: Value, op2: Value, name: Option<String>): Value = TODO()

    /**
     * Build a signed integer division instruction
     *
     * The `sdiv` instruction divides the two integer or vector-of-integer operands. The `sdiv` instruction yields
     * the signed quotient of the two operands. Unsigned division is done with [buildUnsignedDiv]
     *
     * @param op1  dividend integer value (value being divided)
     * @param op2  divisor integer value (the number dividend is being divided by)
     * @param name optional name for the instruction
     */
    public fun buildSignedDiv(op1: Value, op2: Value, name: Option<String>): Value = TODO()

    /**
     * Build a floating-point division instruction
     *
     * The `fdiv` instruction divides the two floating-point or vector-of-floating-point operands.
     *
     * @param op1  dividend floating-point value (value being divided)
     * @param op2  divisor floating-point value (the number divided is being divided by)
     * @param name optional name for the instruction
     */
    public fun buildFloatDiv(op1: Value, op2: Value, name: Option<String>): Value = TODO()

    /**
     * Build an unsigned integer remainder instruction
     *
     * The `urem` instruction returns the remainder from the unsigned division of its two integer or
     * vector-of-integer operands.
     *
     * @param op1  dividend integer value (value being divided)
     * @param op2  divisor integer value (the number dividend is being divided by)
     * @param name optional name for the instruction
     */
    public fun buildUnsignedRem(op1: Value, op2: Value, name: Option<String>): Value = TODO()

    /**
     * Build a signed integer remainder instruction
     *
     * The `srem` instruction returns the remainder from the signed division of its two integer or vector-of-integer
     * operands.
     *
     * @param op1  dividend integer value (value being divided)
     * @param op2  divisor integer value (the number dividend is being divided by)
     * @param name optional name for the instruction
     */
    public fun buildSignedRem(op1: Value, op2: Value, name: Option<String>): Value = TODO()

    /**
     * Build a floating-point remainder instruction
     *
     * The `frem` instruction returns the remainder from the division of its floating-point or
     * vector-of-floating-point operands.
     *
     * @param op1  dividend floating-point value (value being divided)
     * @param op2  divisor floating-point value (the number dividend is being divided by)
     * @param name optional name for the instruction
     */
    public fun buildFloatRem(op1: Value, op2: Value, name: Option<String>): Value = TODO()

    /**
     * Build a left shift instruction
     *
     * The `shl` instruction shifts its first integer or vector-of-integer operand to the left a specified number of
     * bits
     *
     * @param op1  integer value to shift left
     * @param op2  number of bits to shift [op1] to the left
     * @param name optional name for the instruction
     */
    public fun buildLeftShift(op1: Value, op2: Value, name: Option<String>): Value = TODO()

    /**
     * Build a logical shift right instruction
     *
     * The `lshr` instruction logically shifts its first integer or vector-of-integer operand to the right a
     * specified number of bits with zero fill.
     *
     * @param op1  integer value to logically shift right
     * @param op2  number of bits to shift [op1] to the right
     * @param name optional name for the instruction
     */
    public fun buildLogicalShiftRight(op1: Value, op2: Value, name: Option<String>): Value = TODO()

    /**
     * Build an arithmetic shift right instruction
     *
     * The `ashr` instruction arithmetically shifts its first integer or vector-of-integer operand to the right a
     * specified number of bits with sign extension.
     *
     * @param op1  integer value to arithmetically shift right
     * @param op2  number of bits to shift [op1] to the right
     * @param name optional name for the instruction
     */
    public fun buildArithmeticShiftRight(op1: Value, op2: Value, name: Option<String>): Value = TODO()

    /**
     * Build a logical and instruction
     *
     * The `and` instruction returns the bitwise logical and of its two integer or vector-of-integer operands.
     *
     * @param op1  left hand side integer
     * @param op2  right hand side integer
     * @param name optional name for the instruction
     */
    public fun buildLogicalAnd(op1: Value, op2: Value, name: Option<String>): Value = TODO()

    /**
     * Build a logical or instruction
     *
     * The `or` instruction returns the bitwise logical or of its two integer or vector-of-integer operands.
     *
     * @param op1  left hand side integer
     * @param op2  right hand side integer
     * @param name optional name for the instruction
     */
    public fun buildLogicalOr(op1: Value, op2: Value, name: Option<String>): Value = TODO()

    /**
     * Build a logical xor instruction
     *
     * The `xor` instruction returns the bitwise logical xor of its two integer or vector-of-integer operands.
     *
     * @param op1  left hand side integer
     * @param op2  right hand side integer
     * @param name optional name for the instruction
     */
    public fun buildLogicalXor(op1: Value, op2: Value, name: Option<String>): Value = TODO()

    /**
     * Build an extract element instruction
     *
     * The `extractelement` instruction extracts a single element from a vector at a specified index.
     *
     * @param vector value to extract an element from
     * @param index  index of element to extract
     * @param name   optional name for the instruction
     */
    public fun buildExtractElement(vector: Value, index: Value, name: Option<String>): Value = TODO()

    /**
     * Build an insert element instruction
     *
     * The `insertelement` instruction inserts a single element into a vector at a specified index.
     *
     * @param vector value to insert an element into
     * @param value  the item to insert into the vector
     * @param index  the index to store the element
     * @param name   optional name for the instruction
     */
    public fun buildInsertElement(vector: Value, value: Value, index: Value, name: Option<String>): Value = TODO()

    /**
     * Build a shuffle vector instruction
     *
     * The `shufflevector` instruction constructs a permutation of elements from two input vectors, returning a
     * vector with the same element type as the input and length that is the same as the shuffle mask.
     *
     * @param op1  first vector operand
     * @param op2  second vector operand
     * @param mask the shuffle mask
     * @param name optional name for the instruction
     */
    public fun buildShuffleVector(op1: Value, op2: Value, mask: Value, name: Option<String>): Value = TODO()

    /**
     * Build an extract value instruction
     *
     * The `extractvalue` instruction extracts the value of a member field from an aggregate value.
     *
     * The LLVM C API only allows for a single index to be used.
     *
     * @param aggregate struct or array value to extract value from
     * @param index     index in [aggregate] to retrieve
     * @param name      optional name for the instruction
     */
    public fun buildExtractValue(aggregate: Value, index: Int, name: Option<String>): Value = TODO()

    /**
     * Build an insert value instruction
     *
     * The `insertvalue` instruction sets the value of a member field of an aggregate value.
     *
     * The LLVM C API only allows for a single index to be used.
     *
     * @param aggregate struct or array value to insert value into
     * @param value     value to insert at index
     * @param index     index in [aggregate] to insert element into
     * @param name      optional name for the instruction
     */
    public fun buildInsertValue(aggregate: Value, value: Value, index: Int, name: Option<String>): Value = TODO()

    /**
     * Build an alloca instruction
     *
     * The `alloca` instruction allocates memory on the stack frame of the currently executing function. This pointer
     * is automatically freed once the function returns to its caller. This instruction is used in
     * conjunction with `load` and `store`.
     *
     * @param type type to allocate
     * @param name optional name for the instruction
     */
    public fun buildAlloca(type: Type, name: Option<String>): AllocaInstruction = TODO()

    /**
     * Build a load instruction
     *
     * The `load` instruction reads from a pointer in memory. This instruction is used in
     * conjunction with `alloca` and `store`.
     *
     * @param ptr  pointer value to read from
     * @param name optional name for the instruction
     */
    public fun buildLoad(ptr: Value, name: Option<String>): LoadInstruction = TODO()

    /**
     * Build a store instruction
     *
     * The `store` instruction writes to a pointer in memory. This instruction is used in
     * conjunction with `alloca` and `load`.
     *
     * TODO: Research - Why does this not require the type?
     *
     * @param ptr   pointer value to write to
     * @param value value to write to pointer
     * @param name optional name for the instruction
     */
    public fun buildStore(ptr: Value, value: Value, name: Option<String>): StoreInstruction = TODO()

    /**
     * Build a fence instruction
     *
     * The `fence` instruction is used to introduce happens-before edges between operations.
     *
     * TODO: Research - Find out what fence instruction is used for
     */
    public fun buildFence(ordering: AtomicOrdering, singleThread: Boolean, name: Option<String>): FenceInstruction = TODO()

    /**
     * Build a comparison exchange instruction
     *
     * The `cmpxchg` instruction is used to atomically modify memory. It loads a value in memory and compares it to a
     * given value. If these values are equal, it tries to store a new value into the memory.
     *
     * TODO: Research - Find out what cmpxchg is used for
     */
    public fun buildCmpXchg(
        ptr: Value,
        comparison: Value,
        new: Value,
        successOrdering: AtomicOrdering,
        failureOrdering: AtomicOrdering,
        singleThread: Boolean
    ): AtomicCmpXchgInstruction = TODO()

    /**
     * Build an atomic rmw instruction
     *
     * The `atomicrmw` instruction is used to atomically modify memory?
     *
     * TODO: Research - Find out what atomicrmw is used for
     */
    public fun buildAtomicRMW(
        op: AtomicRMWBinaryOperation,
        ptr: Value,
        value: Value,
        ordering: AtomicOrdering,
        singleThread: Boolean
    ): AtomicRMWInstruction = TODO()

    /**
     * Build a get element pointer instruction
     *
     * The `getelementptr` instruction is used to calculate the address of a sub-element of an aggregate data
     * structure. This is just a calculation and it does not access memory.
     *
     * If [isInBounds] is true, the instruction will yield a poison value if one of the following rules are violated:
     * See semantics for instruction: https://llvm.org/docs/LangRef.html#id233
     *
     * @param aggregate  struct or array type to calculate element address of
     * @param indices    directions/indices in the aggregate value to navigate through to find wanted element
     * @param isInBounds whether
     */
    public fun buildGetElementPtr(
        aggregate: Value,
        vararg indices: Value,
        isInBounds: Boolean,
        name: Option<String>
    ): Value = TODO()

    /**
     * Build an integer trunc instruction
     *
     * The `trunc` instruction truncates its integer or vector-of-integer operand to the provided type.
     *
     * The bit size of the operand's type must be larger than the bit size of the destination type. Equal sized types
     * are not allowed.
     *
     * @param op   integer value to truncate
     * @param type type to truncate down to
     * @param name optional name for the instruction
     */
    public fun buildIntTrunc(op: Value, type: IntegerType, name: Option<String>): Value = TODO()

    /**
     * Build a zero extension instruction
     *
     * The `zext` instruction zero extends its integer or vector-of-integer operand to the provided type.
     *
     * The bit size of the operand's type must be smaller than the bit size of the destination type.
     *
     * @param op   integer value to zero extend
     * @param type type to zero extend to
     * @param name optional name for the instruction
     */
    public fun buildZeroExt(op: Value, type: IntegerType, name: Option<String>): Value = TODO()

    /**
     * Build a sign extension instruction
     *
     * The `sext` instruction sign extends its integer or vector-of-integer operand to the provided type.
     *
     * The bit size of the operand's type must be smaller than the bit size of the destination type.
     *
     * @param op   integer value to sign extend
     * @param type type to sign extend to
     * @param name optional name for the instruction
     */
    public fun buildSignExt(op: Value, type: IntegerType, name: Option<String>): Value = TODO()

    /**
     * Build a floating-point trunc instruction
     *
     * The `fptrunc` instruction truncates its floating-point or vector-of-floating-point operand to the provided type.
     *
     * The size of the operand's type must be larger than the destination type. Equal sized types are not allowed.
     *
     * @param op   floating-point value to truncate
     * @param type type to truncate down to
     * @param name optional name for the instruction
     */
    public fun buildFloatTrunc(op: Value, type: FloatingPointType, name: Option<String>): Value = TODO()

    /**
     * Build a float extension instruction
     *
     * The `fpext` instruction casts a floating-point or vector-of-floating-point operand to the provided type.
     *
     * The size of the operand's type must be smaller than the destination type.
     *
     * @param op   floating-point value to extend
     * @param type the type to extend to
     * @param name optional name for the instruction
     */
    public fun buildFloatExt(op: Value, type: FloatingPointType, name: Option<String>): Value = TODO()

    /**
     * Build a float to unsigned int cast instruction
     *
     * The `fptoui` instruction converts a floating-point or a vector-of-floating-point operand to its unsigned
     * integer equivalent.
     *
     * @param op   floating-point value to cast
     * @param type integer type to cast to
     * @param name optional name for the instruction
     */
    public fun buildFloatToUnsigned(op: Value, type: IntegerType, name: Option<String>): Value = TODO()

    /**
     * Build a float to signed int cast instruction
     *
     * The `fptosi` instruction converts a floating-point or a vector-of-floating-point operand to its signed integer
     * equivalent.
     *
     * @param op   floating-point value to cast
     * @param type integer type to cast to
     * @param name optional name for the instruction
     */
    public fun buildFloatToSigned(op: Value, type: IntegerType, name: Option<String>): Value = TODO()

    /**
     * Build an unsigned int to float cast instruction
     *
     * The `uitofp` instruction converts an unsigned integer or vector-of-integer operand to the floating-point type
     * equivalent.
     *
     * @param op   integer value to cast
     * @param type floating-point type to cast to
     * @param name optional name for the instruction
     */
    public fun buildUnsignedToFloat(op: Value, type: FloatingPointType, name: Option<String>): Value = TODO()

    /**
     * Build a signed int to float cast instruction
     *
     * The `sitofp` instruction converts a signed integer or vector-of-integer operand to the floating-point type
     * equivalent.
     *
     * @param op   integer value to cast
     * @param type floating-point type to cast to
     * @param name optional name for the instruction
     */
    public fun buildSignedToFloat(op: Value, type: FloatingPointType, name: Option<String>): Value = TODO()

    /**
     * Build a pointer to int cast instruction
     *
     * The `ptrtoint` instruction converts a pointer or vector-of-pointer operand to the provided integer type.
     *
     * @param op   pointer to cast
     * @param type integer type to cast to
     * @param name optional name for the instruction
     */
    public fun buildPointerToInt(op: Value, type: IntegerType, name: Option<String>): Value = TODO()

    /**
     * Build a int to pointer cast instruction
     *
     * The `inttoptr` instruction converts an integer operand and casts it to the provided pointer type.
     *
     * @param op   integer to cast
     * @param type pointer type to cast to
     * @param name optional name for the instruction
     */
    public fun buildIntToPointer(op: Value, type: PointerType, name: Option<String>): Value = TODO()

    /**
     * Build a bit cast instruction
     *
     * The `bitcast` instruction converts its operand to the provided type without changing any bits.
     *
     * @param op   value to cast
     * @param type type to cast to
     * @param name optional name for the instruction
     */
    public fun buildBitCast(op: Value, type: Type, name: Option<String>): Value = TODO()

    /**
     * Build an address space cast instruction
     *
     * The `addrspacecast` instruction converts a pointer value with a type in address space A to a pointer type in
     * address space B which must have a different address space.
     *
     * @param op   pointer value to cast
     * @param type pointer type to cast address space cast into
     * @param name optional name for the instruction
     */
    public fun buildAddressSpaceCast(op: Value, type: PointerType, name: Option<String>): Value = TODO()

    /**
     * Build an integer comparison instruction
     *
     * The `icmp` instruction returns a boolean (i1) value based on comparison of two integer, vector-of-integer,
     * pointer or vector-of-pointer operands.
     *
     * @param predicate comparison operator to use
     * @param op1       left hand side of comparison
     * @param op2       right hand side of comparison
     * @param name      optional name for the instruction
     */
    public fun buildIntCompare(predicate: IntPredicate, op1: Value, op2: Value, name: Option<String>): Value = TODO()

    /**
     * Build a floating-point comparison instruction
     *
     * The `fcmp` instruction returns a boolean (i1) value based on comparison of two floating-point or
     * vector-of-floating-point operands.
     *
     * @param predicate comparison operator to use
     * @param op1       left hand side of comparison
     * @param op2       right hand side of comparison
     * @param name      optional name for the instruction
     */
    public fun buildFloatCompare(predicate: RealPredicate, op1: Value, op2: Value, name: Option<String>): Value = TODO()

    /**
     * Build a phi instruction
     *
     * The `phi` instruction is used to implement the \phi node in SSA-form
     *
     * The C API does not consume all the cases upon construction, instead we provide an expected amount of
     * destinations which LLVM will pre-allocate for optimization purposes. Cases can be appended to the returned
     * [PhiInstruction] instance.
     *
     * @param type the expected resolving type
     * @param name optional name for the instruction
     */
    public fun buildPhi(type: Type, name: Option<String>): PhiInstruction = TODO()

    /**
     * Build a select instruction
     *
     * The `select` instruction is used to pick a value based on a boolean condition. It is analogous to the ternary
     * operator in C. The condition is either a 1-bit integer or a vector of 1-bit integers
     *
     * @param condition boolean (i1) condition
     * @param isTrue    value to select if [condition] is true
     * @param isFalse   value to select if [condition] is false
     * @param name      optional name for the instruction
     */
    public fun buildSelect(condition: Value, isTrue: Value, isFalse: Value, name: Option<String>): Value = TODO()

    /**
     * Build a freeze instruction
     *
     * The `freeze` instruction is used to stop propagation of an undef or poison value.
     *
     * @param op   poison or undef value
     * @param name optional name for the instruction
     */
    public fun buildFreeze(op: Value, name: Option<String>): Value = TODO()

    /**
     * Build a call instruction
     *
     * The `call` instruction invokes a control flow jump into another function.
     *
     * @param function  function to invoke
     * @param arguments list of arguments to pass into function
     * @param name      optional name for the instruction
     */
    public fun buildCall(function: Function, vararg arguments: Value, name: Option<String>): Value = TODO()

    /**
     * Build a variadic arguments instruction
     *
     * The `va_arg` instruction is used to access arguments passed through as variadic. It's also used to implement
     * the va_arg macro in C. The va_arg instruction returns the current item in the list and increases the pointer.
     *
     * See the LLVM documentation for details regarding va_arg: https://llvm.org/docs/LangRef.html#int-varargs
     *
     * @param list va_arg list to access
     * @param type expected type of the current element
     * @param name optional name for the instruction
     */
    public fun buildVAArg(list: Value, type: Type, name: Option<String>): Value = TODO()
}

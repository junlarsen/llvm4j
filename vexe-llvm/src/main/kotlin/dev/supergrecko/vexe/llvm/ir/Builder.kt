package dev.supergrecko.vexe.llvm.ir

import dev.supergrecko.vexe.llvm.internal.contracts.ContainsReference
import dev.supergrecko.vexe.llvm.internal.contracts.Disposable
import dev.supergrecko.vexe.llvm.internal.contracts.Validatable
import dev.supergrecko.vexe.llvm.ir.instructions.AllocaInstruction
import dev.supergrecko.vexe.llvm.ir.instructions.BrInstruction
import dev.supergrecko.vexe.llvm.ir.instructions.CallInstruction
import dev.supergrecko.vexe.llvm.ir.instructions.ExtractValueInstruction
import dev.supergrecko.vexe.llvm.ir.instructions.IndirectBrInstruction
import dev.supergrecko.vexe.llvm.ir.instructions.InvokeInstruction
import dev.supergrecko.vexe.llvm.ir.instructions.LandingPadInstruction
import dev.supergrecko.vexe.llvm.ir.instructions.LoadInstruction
import dev.supergrecko.vexe.llvm.ir.instructions.ResumeInstruction
import dev.supergrecko.vexe.llvm.ir.instructions.RetInstruction
import dev.supergrecko.vexe.llvm.ir.instructions.StoreInstruction
import dev.supergrecko.vexe.llvm.ir.instructions.SwitchInstruction
import dev.supergrecko.vexe.llvm.ir.instructions.UnreachableInstruction
import dev.supergrecko.vexe.llvm.ir.types.FunctionType
import dev.supergrecko.vexe.llvm.ir.values.FunctionValue
import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.llvm.LLVM.LLVMBuilderRef
import org.bytedeco.llvm.global.LLVM

public class Builder public constructor(
    context: Context = Context.getGlobalContext()
) : AutoCloseable, Validatable, Disposable, ContainsReference<LLVMBuilderRef> {
    public override var ref: LLVMBuilderRef = LLVM.LLVMCreateBuilderInContext(
        context.ref
    )

    public override var valid: Boolean = true

    /**
     * Construct a new Type from an LLVM pointer reference
     */
    public constructor(builder: LLVMBuilderRef) : this() {
        ref = builder
    }

    //region InstructionBuilders
    /**
     * LLVMPositionBuilder
     */
    public fun positionBefore(instruction: Instruction) {
        // TODO: Test
        LLVM.LLVMPositionBuilderBefore(ref, instruction.ref)
    }

    /**
     * LLVMPositionBuilderAtEnd
     */
    public fun positionAtEnd(basicBlock: BasicBlock) {
        LLVM.LLVMPositionBuilderAtEnd(ref, basicBlock.ref)
    }

    /**
     * LLVMGetInsertBlock
     */
    public fun getInsertBlock(): BasicBlock? {
        val ref = LLVM.LLVMGetInsertBlock(ref) ?: return null
        return BasicBlock(ref)
    }

    /**
     * LLVMClearInsertionPosition
     */
    public fun clearInsertPosition() {
        LLVM.LLVMClearInsertionPosition(ref)
    }

    /**
     * LLVMInsertIntoBuilderWithName
     */
    public fun insert(instruction: Instruction, name: String?) {
        // TODO: Test
        LLVM.LLVMInsertIntoBuilderWithName(
            ref,
            instruction.ref,
            name
        )
    }

    /**
     * Contains the singleton instance for the instruction builder
     */
    private val builder: InstructionBuilder = InstructionBuilder()

    /**
     * Get the singleton instruction builder
     */
    public fun getInstructionBuilder(): InstructionBuilder {
        return builder
    }

    /**
     * An instruction builder is a wrapper class for building instructions
     * for a builder.
     *
     * To prevent polluting autocomplete for the [Builder] all the
     * instruction creation functions are declared in here.
     *
     * Each [Builder] has one of these, retrievable by [getBuilder]
     */
    public inner class InstructionBuilder {
        /**
         * Build a return instruction
         *
         * Returns the given [value]
         *
         * @see LLVM.LLVMBuildRet
         */
        public fun createRet(
            value: Value
        ): RetInstruction {
            val inst = LLVM.LLVMBuildRet(ref, value.ref)

            return RetInstruction(inst)
        }

        /**
         * Build a return instruction
         *
         * This instruction returns void instead of a value
         *
         * @see LLVM.LLVMBuildRetVoid
         */
        public fun createRetVoid(): RetInstruction {
            val inst = LLVM.LLVMBuildRetVoid(ref)

            return RetInstruction(inst)
        }

        /**
         * Build a return instruction
         *
         * Create a sequence of N insertvalue instructions, with one Value
         * from the [values] array each, that build a aggregate
         * return value one value at a time, and a ret instruction to return
         * the resulting aggregate value.
         *
         * @see LLVM.LLVMBuildAggregateRet
         */
        public fun createAggregateRet(
            values: List<Value>
        ): RetInstruction {
            val ptr = PointerPointer(*values.map { it.ref }.toTypedArray())
            val inst = LLVM.LLVMBuildAggregateRet(ref, ptr, values.size)

            return RetInstruction(inst)
        }

        /**
         * Build a branch instruction
         *
         * This is an unconditional branch. Use [createCondBr]
         *
         * @see LLVM.LLVMBuildBr
         */
        public fun createBr(
            destination: BasicBlock
        ): BrInstruction {
            val inst = LLVM.LLVMBuildBr(ref, destination.ref)

            return BrInstruction(inst)
        }

        /**
         * Build a branch instruction
         *
         * Evaluates [condition], if it's truthy it will branch to [truthy]
         * otherwise it will branch to [falsy]
         *
         * @see LLVM.LLVMBuildCondBr
         */
        public fun createCondBr(
            condition: Value,
            truthy: BasicBlock,
            falsy: BasicBlock
        ): BrInstruction {
            val inst = LLVM.LLVMBuildCondBr(
                ref,
                condition.ref,
                truthy.ref,
                falsy.ref
            )

            return BrInstruction(inst)
        }

        /**
         * Build a switch instruction
         *
         * A switch is a glorified branch, it takes a single integer
         * [condition] and a [default] case which will be branched if none of
         * the other cases match.
         *
         * You may add cases to the instruction after construction. You may
         * hint at how many cases the switch expects with [expectedCases]
         *
         * @see LLVM.LLVMBuildSwitch
         */
        public fun createSwitch(
            condition: Value,
            default: BasicBlock,
            expectedCases: Int = 10
        ): SwitchInstruction {
            val inst = LLVM.LLVMBuildSwitch(
                ref,
                condition.ref,
                default.ref,
                expectedCases
            )

            return SwitchInstruction(inst)
        }

        /**
         * Build an indirect branch instruction
         *
         * Create an indirect break to the [destination] address. Each
         * destination added to this instruction are destinations the
         * [destination] address may point to.
         *
         * Destinations can be added on the returned instruction
         *
         * @see LLVM.LLVMBuildIndirectBr
         */
        public fun createIndirectBr(
            destination: Value,
            expectedCases: Int = 10
        ): IndirectBrInstruction {
            val inst = LLVM.LLVMBuildIndirectBr(
                ref,
                destination.ref,
                expectedCases
            )

            return IndirectBrInstruction(inst)
        }

        /**
         * Build an invoke instruction
         *
         * Invoke moves control to the target [function] and continues with
         * either a [normal] or a [catch] label. If the [function] returns,
         * then control is moved to [normal]. If the callee, or any indirect
         * callees returns via resume then control is moved to [catch].
         *
         * If the callee did return, it will be moved into [variable]
         *
         * @see LLVM.LLVMBuildInvoke2
         */
        public fun createInvoke(
            functionType: FunctionType,
            function: FunctionValue,
            arguments: List<Value>,
            normal: BasicBlock,
            catch: BasicBlock,
            variable: String
        ): InvokeInstruction {
            val args = PointerPointer(*arguments.map { it.ref }.toTypedArray())
            val inst = LLVM.LLVMBuildInvoke2(
                ref,
                functionType.ref,
                function.ref,
                args,
                arguments.size,
                normal.ref,
                catch.ref,
                variable
            )

            return InvokeInstruction(inst)
        }

        /**
         * Build an unreachable instruction
         *
         * Unreachable instructions inform the optimizer that code below the
         * instruction are not reachable. This can be used to implement noreturn
         *
         * @see LLVM.LLVMBuildUnreachable
         */
        public fun createUnreachable(): UnreachableInstruction {
            val inst = LLVM.LLVMBuildUnreachable(ref)

            return UnreachableInstruction(inst)
        }

        /**
         * Build a resume instruction
         *
         * Resume resumes the propagation of an in-flight exception which was
         * unwinded at a landingpad
         *
         * @see LLVM.LLVMBuildResume
         */
        public fun createResume(
            value: Value
        ): ResumeInstruction {
            val inst = LLVM.LLVMBuildResume(ref, value.ref)

            return ResumeInstruction(inst)
        }

        /**
         * Create a landing pad instruction
         *
         * A landing pad is a catch clause for LLVM's exception handling
         * system. The result of the landingpad is stored in [variable]
         *
         * Clauses can be added on the returned instructions
         *
         * @see LLVM.LLVMBuildLandingPad
         */
        public fun createLandingPad(
            catchesType: Type,
            personalityFunction: FunctionValue,
            expectedClauses: Int = 10,
            variable: String
        ): LandingPadInstruction {
            val inst = LLVM.LLVMBuildLandingPad(
                ref,
                catchesType.ref,
                personalityFunction.ref,
                expectedClauses,
                variable
            )

            return LandingPadInstruction(inst)
        }

        /**
         * Build a call instruction
         *
         * This will call [function] with the supplied [arguments]. The
         * result will be bound to the a variable with the name [variable].
         *
         * To prevent binding the result to a variable, either omit the
         * [variable] argument, or set it to `""`
         *
         * @see LLVM.LLVMBuildCall2
         */
        public fun createCall(
            function: FunctionValue,
            arguments: List<Value>,
            variable: String = ""
        ): CallInstruction {
            val args = PointerPointer(*arguments.map { it.ref }.toTypedArray())
            val inst = LLVM.LLVMBuildCall2(
                ref,
                function.getType().ref,
                function.ref,
                args,
                arguments.size,
                variable
            )

            return CallInstruction(inst)
        }

        /**
         * Build an alloca instruction
         *
         * The passed [type] must be sized. You may specify an amount of
         * objects to be allocated, default is one. This is done by
         * specifying the [elementCount] argument.
         *
         * @see LLVM.LLVMBuildAlloca
         */
        public fun createAlloca(
            type: Type,
            name: String,
            elementCount: Value? = null
        ): AllocaInstruction {
            val inst = if (elementCount != null) {
                LLVM.LLVMBuildArrayAlloca(ref, type.ref, elementCount.ref, name)
            } else {
                LLVM.LLVMBuildAlloca(ref, type.ref, name)
            }

            return AllocaInstruction(inst)
        }

        /**
         * Build a load instruction
         *
         * Loads [dereference] into memory and assigns it the type [type].
         * The result is stored in a new variable in the IR named [variable]
         *
         * @see LLVM.LLVMBuildLoad2
         */
        public fun createLoad(
            type: Type,
            dereference: Value,
            variable: String
        ): LoadInstruction {
            val inst = LLVM.LLVMBuildLoad2(
                ref,
                type.ref,
                dereference.ref,
                variable
            )

            return LoadInstruction(inst)
        }

        /**
         * Build a store instruction
         *
         * Writes [value] into memory at [destination]
         */
        public fun createStore(
            value: Value,
            destination: Value
        ): StoreInstruction {
            val inst = LLVM.LLVMBuildStore(ref, value.ref, destination.ref)

            return StoreInstruction(inst)
        }

        /**
         * Build a extract-value instruction
         *
         * Extract the element at [index] in the [aggregate] and store it
         * into a new IR variable named [variable]
         */
        public fun createExtractValue(
            aggregate: Value,
            index: Int,
            variable: String
        ): ExtractValueInstruction {
            val inst = LLVM.LLVMBuildExtractValue(
                ref,
                aggregate.ref,
                index,
                variable
            )

            return ExtractValueInstruction(inst)
        }
    }
    //endregion InstructionBuilders

    override fun dispose() {
        require(valid) { "This builder has already been disposed." }

        valid = false

        LLVM.LLVMDisposeBuilder(ref)
    }

    override fun close() = dispose()
}

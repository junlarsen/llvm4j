package dev.supergrecko.vexe.llvm.executionengine

import dev.supergrecko.vexe.llvm.internal.contracts.ContainsReference
import dev.supergrecko.vexe.llvm.internal.util.fromLLVMBool
import dev.supergrecko.vexe.llvm.internal.util.wrap
import dev.supergrecko.vexe.llvm.ir.Module
import dev.supergrecko.vexe.llvm.ir.TargetData
import dev.supergrecko.vexe.llvm.ir.Value
import dev.supergrecko.vexe.llvm.ir.values.FunctionValue
import dev.supergrecko.vexe.llvm.target.TargetMachine
import org.bytedeco.javacpp.Pointer
import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.llvm.LLVM.LLVMExecutionEngineRef
import org.bytedeco.llvm.global.LLVM

public class ExecutionEngine public constructor() :
    ContainsReference<LLVMExecutionEngineRef> {
    public override val ref: LLVMExecutionEngineRef = LLVMExecutionEngineRef()

    //region ExecutionEngine
    /**
     * Runs the llvm.global_ctors global variable
     *
     * @see LLVM.LLVMRunStaticConstructors
     */
    public fun runStaticConstructors() {
        LLVM.LLVMRunStaticConstructors(ref)
    }

    /**
     * Runs the llvm.global_dtors global variable
     *
     * @see LLVM.LLVMRunStaticDestructors
     */
    public fun runStaticDestructors() {
        LLVM.LLVMRunStaticDestructors(ref)
    }

    /**
     * Run a given [function] with a list of arguments
     *
     * The execution result of the function is returned
     *
     * @see LLVM.LLVMRunFunction
     */
    public fun runFunction(
        function: FunctionValue,
        values: List<GenericValue>
    ): GenericValue {
        val args = PointerPointer(*values.map { it.ref }.toTypedArray())
        val res = LLVM.LLVMRunFunction(
            ref,
            function.ref,
            values.size,
            args
        )

        return GenericValue(res)
    }

    /**
     * Run a given [function] as main with the provided argv and argc.
     *
     * The function's "exit code" is returned.
     *
     * @see LLVM.LLVMRunFunctionAsMain
     */
    public fun runFunctionAsMain(
        function: FunctionValue,
        argv: String,
        envp: String,
        argc: Int
    ): Int {
        return LLVM.LLVMRunFunctionAsMain(
            ref,
            function.ref,
            argc,
            argv.toByteArray(),
            envp.toByteArray()
        )
    }

    /**
     * Free machine code for the function?????
     *
     * No idea what this actually does, there is no implementation in the
     * source code for this, so at the moment it's most likely a no-op
     *
     * @see LLVM.LLVMFreeMachineCodeForFunction
     */
    public fun freeMachineCode(function: FunctionValue) {
        LLVM.LLVMFreeMachineCodeForFunction(ref, function.ref)
    }

    /**
     * Add a module to the execution context
     *
     * @see LLVM.LLVMAddModule
     */
    public fun addModule(module: Module) {
        LLVM.LLVMAddModule(ref, module.ref)
    }

    /**
     * Unlink the given module
     *
     * @see LLVM.LLVMRemoveModule
     */
    public fun removeModule(module: Module) {
        val err = ByteArray(0)
        val success = LLVM.LLVMRemoveModule(ref, module.ref, module.ref, err)

        if (!success.fromLLVMBool()) {
            throw RuntimeException(err.contentToString())
        }
    }

    /**
     * Relink and recompile the function??????
     *
     * No idea what this actually does, there is no implementation in the
     * source code for this, so at the moment it's most likely a no-op
     *
     * @see LLVM.LLVMRecompileAndRelinkFunction
     */
    public fun relinkAndRecompileFunction(function: FunctionValue) {
        LLVM.LLVMRecompileAndRelinkFunction(ref, function.ref)
    }

    /**
     * Get the execution engine target data if it exists
     *
     * @see LLVM.LLVMGetExecutionEngineTargetData
     */
    public fun getTargetData(): TargetData? {
        val td = LLVM.LLVMGetExecutionEngineTargetData(ref)

        return wrap(td) { TargetData(it) }
    }

    /**
     * Get the execution engine target machine if it exists
     *
     * @see LLVM.LLVMGetExecutionEngineTargetMachine
     */
    public fun getTargetMachine(): TargetMachine? {
        val tm = LLVM.LLVMGetExecutionEngineTargetMachine(ref)

        return wrap(tm) { TargetMachine(it) }
    }

    /**
     * Create a global mapping to a value
     *
     * @see LLVM.LLVMAddGlobalMapping
     */
    public fun addGlobalMapping(value: Value, address: Pointer) {
        LLVM.LLVMAddGlobalMapping(ref, value.ref, address)
    }

    /**
     * Get a pointer to a global value
     *
     * @see LLVM.LLVMGetPointerToGlobal
     */
    public fun getPointerToGlobal(value: Value): Pointer {
        return LLVM.LLVMGetPointerToGlobal(ref, value.ref)
    }

    /**
     * Get the address of a function
     *
     * @see LLVM.LLVMGetFunctionAddress
     */
    public fun getFunctionAddress(function: String): Long {
        return LLVM.LLVMGetFunctionAddress(ref, function)
    }
    //endregion ExecutionEngine
}
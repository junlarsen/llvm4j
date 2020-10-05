package io.vexelabs.bitbuilder.llvm.executionengine

import io.vexelabs.bitbuilder.llvm.internal.contracts.ContainsReference
import io.vexelabs.bitbuilder.llvm.internal.contracts.Disposable
import io.vexelabs.bitbuilder.llvm.ir.Module
import io.vexelabs.bitbuilder.llvm.ir.Value
import io.vexelabs.bitbuilder.llvm.ir.values.FunctionValue
import io.vexelabs.bitbuilder.llvm.target.TargetData
import io.vexelabs.bitbuilder.llvm.target.TargetMachine
import org.bytedeco.javacpp.BytePointer
import org.bytedeco.javacpp.Pointer
import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.llvm.LLVM.LLVMExecutionEngineRef
import org.bytedeco.llvm.global.LLVM

/**
 * Interface to llvm::ExecutionEngine
 *
 * An execution engine is an interpreter or a JIT compiler for LLVM IR which
 * may be invoked at runtime.
 *
 * @see LLVMExecutionEngineRef
 */
public class ExecutionEngine public constructor() :
    ContainsReference<LLVMExecutionEngineRef>, Disposable {
    public override val ref: LLVMExecutionEngineRef = LLVMExecutionEngineRef()
    public override var valid: Boolean = true

    /**
     * Runs the llvm.global_ctors global variable
     *
     * This is similar to how we might need to acquire resources for values
     * at startup, eg: initializing/constructing a global variable
     *
     * @see LLVM.LLVMRunStaticConstructors
     */
    public fun runStaticConstructors() {
        LLVM.LLVMRunStaticConstructors(ref)
    }

    /**
     * Runs the llvm.global_dtors global variable
     *
     * This is similar to how we will destroy resources right before the
     * program ends, eg: destructing a global variable
     *
     * @see LLVM.LLVMRunStaticDestructors
     */
    public fun runStaticDestructors() {
        LLVM.LLVMRunStaticDestructors(ref)
    }

    /**
     * Run a given [function] with a list of arguments
     *
     * The argument list is an array of [GenericValue]s which is the
     * Execution Engine's way to interfere with the outside world.
     *
     * The execution result of the function is returned in the form of a
     * [GenericValue].
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
     * Run a given [function] a program's "main" function
     *
     * The caller may optionally provide an [argc], [argv] and [envp] which
     * all correspond to a typical C++ application's entry point
     *
     * The [envp] defaults to all the environment variables, in a key=value
     * format.
     *
     * The "exit code" from the function is returned.
     *
     * @see LLVM.LLVMRunFunctionAsMain
     */
    public fun runFunctionAsMain(
        function: FunctionValue,
        argv: List<String> = emptyList(),
        argc: Int = argv.size,
        envp: List<String> = System.getenv().map { "${it.key}=${it.value}" }
    ): Int {
        val env = PointerPointer(*envp.map { BytePointer(it) }.toTypedArray())
        val arg = PointerPointer(*argv.map { BytePointer(it) }.toTypedArray())

        return LLVM.LLVMRunFunctionAsMain(ref, function.ref, argc, arg, env)
    }

    /**
     * Free machine code for the function?
     *
     * This function lacks documentation in the LLVM-C API and its function
     * body is empty at its implementation. Possibly kept for binary
     * backwards compatibility
     *
     * @see LLVM.LLVMFreeMachineCodeForFunction
     */
    public fun freeMachineCode(function: FunctionValue) {
        LLVM.LLVMFreeMachineCodeForFunction(ref, function.ref)
    }

    /**
     * Add a module to the execution context
     *
     * TODO: Research behavior of symbol collision
     *
     * @see LLVM.LLVMAddModule
     */
    public fun addModule(module: Module) {
        LLVM.LLVMAddModule(ref, module.ref)
    }

    /**
     * Unlink the given module from the execution context
     *
     * @see LLVM.LLVMRemoveModule
     */
    public fun removeModule(module: Module) {
        val err = BytePointer(0L)
        val result = LLVM.LLVMRemoveModule(ref, module.ref, module.ref, err)

        if (result != 0) {
            throw RuntimeException(err.string)
        }

        err.deallocate()
    }

    /**
     * Relink and recompile the function?
     *
     * This function lacks documentation in the LLVM-C API and its function
     * body is empty at its implementation. Possibly kept for binary
     * backwards compatibility
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

        return td?.let { TargetData(it) }
    }

    /**
     * Get the execution engine target machine if it exists
     *
     * @see LLVM.LLVMGetExecutionEngineTargetMachine
     */
    public fun getTargetMachine(): TargetMachine? {
        val tm = LLVM.LLVMGetExecutionEngineTargetMachine(ref)

        return tm?.let { TargetMachine(it) }
    }

    /**
     * Create a global mapping to a value
     *
     * This adds a mapping to the provided [value] at the given [address]. An
     * address should be fetched from [getPointerToGlobal]
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

    public override fun dispose() {
        require(valid) { "Cannot dispose object twice" }

        valid = false

        LLVM.LLVMDisposeExecutionEngine(ref)
    }
}

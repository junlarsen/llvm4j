package io.vexelabs.examples

import io.vexelabs.bitbuilder.llvm.executionengine.GenericValue
import io.vexelabs.bitbuilder.llvm.ir.Builder
import io.vexelabs.bitbuilder.llvm.ir.CallConvention
import io.vexelabs.bitbuilder.llvm.ir.Context
import io.vexelabs.bitbuilder.llvm.ir.IntPredicate
import io.vexelabs.bitbuilder.llvm.ir.Module
import io.vexelabs.bitbuilder.llvm.ir.PassManager
import io.vexelabs.bitbuilder.llvm.ir.types.FunctionType
import io.vexelabs.bitbuilder.llvm.ir.types.IntType
import io.vexelabs.bitbuilder.llvm.ir.values.FunctionValue
import io.vexelabs.bitbuilder.llvm.ir.values.constants.ConstantInt
import io.vexelabs.bitbuilder.llvm.support.VerifierFailureAction
import org.bytedeco.llvm.global.LLVM

/**
 * Example project using LLVM to find the factorial of 20
 *
 * This sample creates a module with a factorial function inside it. The
 * function is built using the [Builder] class
 *
 * After the function has been built we create a JIT Compiler for this module
 * and executes the function with an argument of 20
 */
fun main() {
    // LLVM initialization routines
    // As of right now, these are not abstracted by Bitbuilder
    LLVM.LLVMLinkInMCJIT()
    LLVM.LLVMInitializeNativeAsmPrinter()
    LLVM.LLVMInitializeNativeAsmParser()
    LLVM.LLVMInitializeNativeDisassembler()
    LLVM.LLVMInitializeNativeTarget()

    // Create a Context for our LLVM types and our module
    val context = Context()
    val module = Module("my_sample", context)
    // Let's create our factorial function in [createFactorialFunction]
    val function = createFactorialFunction(module)

    // Lets build our factorial function in [buildFactorialFunction]
    buildFactorialFunction(function, context)

    // Ensure that the module we just built is valid
    module.verify(VerifierFailureAction.PrintMessage)

    // Create the jit compiler
    val compiler = module.createJITCompiler(optimizationLevel = 2)
    // internal(ignore): TODO: Make this ctor public
    // Create our pass manager with the optimization passes we want
    val pass = PassManager(LLVM.LLVMCreatePassManager()).also {
        LLVM.LLVMAddConstantPropagationPass(it.ref)
        LLVM.LLVMAddInstructionCombiningPass(it.ref)
        LLVM.LLVMAddPromoteMemoryToRegisterPass(it.ref)
        LLVM.LLVMAddGVNPass(it.ref)
        LLVM.LLVMAddCFGSimplificationPass(it.ref)
    }
    // Run the pass manager
    LLVM.LLVMRunPassManager(pass.ref, module.ref)

    // Create our 20 value to call the function with
    val argument = GenericValue(
        type = IntType(32),
        number = 20L,
        isSigned = false
    )

    val result = compiler.runFunction(
        function = function,
        values = listOf(argument)
    ).toInt(false)

    println("We just calculated the factorial of 20 with LLVM's JIT.")
    println("factorial(20) = $result")
    println("The LLVM module we just created:")
    println(module.getIR())

    // Dispose objects
    pass.dispose()
    module.dispose()
    context.dispose()
}

fun createFactorialFunction(module: Module): FunctionValue {
    // We can get the context the module was created in
    val context = module.getContext()

    // Create the int32 type
    val i32 = IntType(32, context)

    // This is the signature of our factorial function.
    //
    // declare i32 @factorial(i32 %n)
    val functionType = FunctionType(
        returns = i32,
        types = listOf(i32),
        variadic = false
    )

    // Create the function inside our module
    return module.createFunction("factorial", functionType).also {
        // Set the call convention to the C Calling Convention
        it.setCallConvention(CallConvention.CCall)
    }
}

fun buildFactorialFunction(
    function: FunctionValue,
    context: Context
) {
    // LLVM types are cached per context, so us "creating" another does not
    // actually create another type, we are simply pulling the one we created
    // in the createFactorialFunction function.
    val i32 = IntType(32, context)

    // Get the first parameter the function receives
    val number = function.getParameter(0)

    // Create the entry point and exit point basic blocks
    val entry = function.createBlock("entry")
    val exit = function.createBlock("exit")

    Builder(context).apply {
        setPositionAtEnd(entry)

        // Create the "number == 0" condition to simulate an if block
        val condition = createICmp(
            lhs = number,
            predicate = IntPredicate.EQ,
            rhs = ConstantInt(i32, 0),
            variable = "number == 0"
        )

        // Create the basic blocks for the if statement's then and else branches
        val then = function.createBlock("then")
        val otherwise = function.createBlock("otherwise")

        // Jump based on whether the condition was true or not
        createCondBr(condition, then, otherwise)

        // Enter the if true block
        setPositionAtEnd(then)
        // If number == 0 then we return by jumping to the exit
        createBr(exit)

        // Enter the otherwise block
        setPositionAtEnd(otherwise)

        // Subtract 1 from number
        val numberMinusOne = createSub(
            lhs = number,
            rhs = ConstantInt(i32, 1),
            variable = "number - 1"
        )
        // Call itself recursively with number - 1
        val callResult = createCall(
            function = function,
            arguments = listOf(numberMinusOne),
            variable = "factorial(number - 1)"
        )
        // This is the function result if we ended up in the otherwise block
        val resultFromOtherwise = createMul(
            lhs = number,
            rhs = callResult,
            variable = "number * factorial(number - 1)"
        )
        // This is the function result if we ended up in the then block (1)
        val resultFromThen = ConstantInt(i32, 1)

        // Jump to the exit block
        createBr(exit)

        setPositionAtEnd(exit)

        // Get the actual result with a phi node
        val finalResult = createPhi(
            incoming = i32,
            variable = "result"
        ).apply {
            // See https://llvm.org/docs/LangRef.html#phi-instruction
            addIncoming(
                values = listOf(resultFromThen, resultFromOtherwise),
                blocks = listOf(then, otherwise)
            )
        }

        // Return from the function
        createRet(finalResult)
    }
}
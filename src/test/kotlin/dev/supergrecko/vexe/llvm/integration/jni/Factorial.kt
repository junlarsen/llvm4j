package dev.supergrecko.vexe.llvm.integration.jni

import dev.supergrecko.vexe.llvm.executionengine.GenericValue
import dev.supergrecko.vexe.llvm.ir.Builder
import dev.supergrecko.vexe.llvm.ir.CallConvention
import dev.supergrecko.vexe.llvm.ir.IntPredicate
import dev.supergrecko.vexe.llvm.ir.Module
import dev.supergrecko.vexe.llvm.ir.PassManager
import dev.supergrecko.vexe.llvm.ir.types.FunctionType
import dev.supergrecko.vexe.llvm.ir.types.IntType
import dev.supergrecko.vexe.llvm.ir.values.constants.ConstantInt
import dev.supergrecko.vexe.llvm.support.VerifierFailureAction
import org.bytedeco.llvm.global.LLVM
import org.spekframework.spek2.Spek
import kotlin.test.assertEquals

internal object Factorial : Spek({
    test("translated factorial example") {
        // any call to LLVM.x is not implemented for llvm4kt yet
        LLVM.LLVMLinkInMCJIT()
        LLVM.LLVMInitializeNativeAsmPrinter()
        LLVM.LLVMInitializeNativeAsmParser()
        LLVM.LLVMInitializeNativeDisassembler()
        LLVM.LLVMInitializeNativeTarget()

        val module = Module("factorial")

        val factorialType = FunctionType(
            returns = IntType(32),
            types = listOf(IntType(32)),
            variadic = false
        )
        val factorial = module.addFunction("factorial", factorialType).apply {
            setCallConvention(CallConvention.CCall)
        }

        val n = factorial.getParameter(0)
        val entry = factorial.createBlock("entry")
        val then = factorial.createBlock("then")
        val otherwise = factorial.createBlock("otherwise")
        val exit = factorial.createBlock("exit")

        val builder = Builder().apply {
            setPositionAtEnd(entry) // enter function

            val condition = build().createICmp(
                lhs = n,
                predicate = IntPredicate.EQ,
                rhs = ConstantInt(IntType(32), 0),
                variable = "n == 0"
            ) // compare param n with 0

            val resultIfTrue = ConstantInt(IntType(32), 1)

            // jump based on condition
            build().createCondBr(condition, then, otherwise)
            setPositionAtEnd(then) // enter then block
            build().createBr(exit) // jump to exit
            setPositionAtEnd(otherwise) // enter otherwise block

            val nMinusOne = build().createSub(
                lhs = n,
                rhs = ConstantInt(IntType(32), 1),
                variable = "n - 1"
            ) // subtract 1 from n
            val recursiveCall = build().createCall(
                function = factorial,
                arguments = listOf(nMinusOne),
                variable = "factorial(n - 1)"
            ) // call self recursively
            val resultIfFalse = build().createMul(
                lhs = n,
                rhs = recursiveCall,
                variable = "n * factorial(n - 1)"
            )

            build().createBr(exit) // jump to exit block
            setPositionAtEnd(exit)

            val result = build().createPhi(
                incoming = IntType(32),
                variable = "result"
            ).apply {
                addIncoming(
                    values = listOf(resultIfTrue, resultIfFalse),
                    blocks = listOf(then, otherwise)
                )
            }
            build().createRet(result)
        }

        module.verify(VerifierFailureAction.PrintMessage)

        val compiler = module.createJITCompiler(2)
        val pass = PassManager(
            LLVM.LLVMCreatePassManager()
        )

        LLVM.LLVMAddConstantPropagationPass(pass.ref)
        LLVM.LLVMAddInstructionCombiningPass(pass.ref)
        LLVM.LLVMAddPromoteMemoryToRegisterPass(pass.ref)
        LLVM.LLVMAddGVNPass(pass.ref)
        LLVM.LLVMAddCFGSimplificationPass(pass.ref)
        LLVM.LLVMRunPassManager(pass.ref, module.ref)

        val args = GenericValue(
            type = IntType(32),
            number = 10L, // factorial(10),
            isSigned = false
        )

        val genericValueResult = compiler.runFunction(
            function = factorial,
            values = listOf(args)
        )
        val result = genericValueResult.toInt(true)

        assertEquals(3628800, result)
    }
})
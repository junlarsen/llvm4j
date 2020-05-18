package dev.supergrecko.kllvm.jni

import kotlin.system.exitProcess
import kotlin.test.assertEquals
import org.bytedeco.javacpp.BytePointer
import org.bytedeco.javacpp.Pointer
import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.llvm.LLVM.LLVMExecutionEngineRef
import org.bytedeco.llvm.global.LLVM
import org.junit.jupiter.api.Test

class JNIFactorialTest {
    @Test
    fun `Factorial example from bytedeco`() {
        val error = BytePointer(null as Pointer?)

        LLVM.LLVMLinkInMCJIT()
        LLVM.LLVMInitializeNativeAsmPrinter()
        LLVM.LLVMInitializeNativeAsmParser()
        LLVM.LLVMInitializeNativeDisassembler()
        LLVM.LLVMInitializeNativeTarget()

        val mod = LLVM.LLVMModuleCreateWithName("fac_module")
        val facArgs = arrayOf(LLVM.LLVMInt32Type())
        val fac = LLVM.LLVMAddFunction(mod, "fac", LLVM.LLVMFunctionType(LLVM.LLVMInt32Type(), facArgs[0], 1, 0))

        LLVM.LLVMSetFunctionCallConv(fac, LLVM.LLVMCCallConv)

        val n = LLVM.LLVMGetParam(fac, 0)
        val entry = LLVM.LLVMAppendBasicBlock(fac, "entry")
        val iftrue = LLVM.LLVMAppendBasicBlock(fac, "iftrue")
        val iffalse = LLVM.LLVMAppendBasicBlock(fac, "iffalse")
        val end = LLVM.LLVMAppendBasicBlock(fac, "end")
        val builder = LLVM.LLVMCreateBuilder()

        LLVM.LLVMPositionBuilderAtEnd(builder, entry)

        val If = LLVM.LLVMBuildICmp(builder, LLVM.LLVMIntEQ, n, LLVM.LLVMConstInt(LLVM.LLVMInt32Type(), 0, 0), "n == 0")

        LLVM.LLVMBuildCondBr(builder, If, iftrue, iffalse)
        LLVM.LLVMPositionBuilderAtEnd(builder, iftrue)

        val res_iftrue = LLVM.LLVMConstInt(LLVM.LLVMInt32Type(), 1, 0)

        LLVM.LLVMBuildBr(builder, end)
        LLVM.LLVMPositionBuilderAtEnd(builder, iffalse)

        val n_minus = LLVM.LLVMBuildSub(builder, n, LLVM.LLVMConstInt(LLVM.LLVMInt32Type(), 1, 0), "n - 1")
        val call_fac_args = arrayOf(n_minus)
        val call_fac = LLVM.LLVMBuildCall(builder, fac, PointerPointer(*call_fac_args), 1, "fac(n - 1)")
        val res_iffalse = LLVM.LLVMBuildMul(builder, n, call_fac, "n * fac(n - 1)")

        LLVM.LLVMBuildBr(builder, end)
        LLVM.LLVMPositionBuilderAtEnd(builder, end)

        val res = LLVM.LLVMBuildPhi(builder, LLVM.LLVMInt32Type(), "result")
        val phi_vals = arrayOf(res_iftrue, res_iffalse)
        val phi_blocks = arrayOf(iftrue, iffalse)

        LLVM.LLVMAddIncoming(res, PointerPointer(*phi_vals), PointerPointer(*phi_blocks), 2)
        LLVM.LLVMBuildRet(builder, res)
        LLVM.LLVMVerifyModule(mod, LLVM.LLVMAbortProcessAction, error)
        LLVM.LLVMDisposeMessage(error) // Handler == LLVMAbortProcessAction -> No need to check errors

        val engine = LLVMExecutionEngineRef()

        if (LLVM.LLVMCreateJITCompilerForModule(engine, mod, 2, error) != 0) {
            System.err.println(error.string)
            LLVM.LLVMDisposeMessage(error)
            exitProcess(-1)
        }

        val pass = LLVM.LLVMCreatePassManager()

        LLVM.LLVMAddConstantPropagationPass(pass)
        LLVM.LLVMAddInstructionCombiningPass(pass)
        LLVM.LLVMAddPromoteMemoryToRegisterPass(pass)
        LLVM.LLVMAddGVNPass(pass)
        LLVM.LLVMAddCFGSimplificationPass(pass)
        LLVM.LLVMRunPassManager(pass, mod)

        val exec_args = LLVM.LLVMCreateGenericValueOfInt(LLVM.LLVMInt32Type(), 10, 0)
        val exec_res = LLVM.LLVMRunFunction(engine, fac, 1, exec_args)

        val result = LLVM.LLVMGenericValueToInt(exec_res, 0)

        assertEquals(3628800, result)

        LLVM.LLVMDisposePassManager(pass)
        LLVM.LLVMDisposeBuilder(builder)
        LLVM.LLVMDisposeExecutionEngine(engine)
    }
}

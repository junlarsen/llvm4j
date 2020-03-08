package dev.supergrecko.ktools.llvm.basic

import org.bytedeco.llvm.LLVM.LLVMModuleRef
import org.bytedeco.llvm.global.LLVM
import org.junit.jupiter.api.Test

/**
 * This test is only here temporarily and is primarily used to
 * test whether the LLVM bindings are working or not.
 */
class JNITest {
    @Test
    fun `test llvm bindings work`() {
        LLVM.LLVMLinkInMCJIT()
        LLVM.LLVMInitializeNativeAsmPrinter()
        LLVM.LLVMInitializeNativeAsmParser()
        LLVM.LLVMInitializeNativeDisassembler()
        LLVM.LLVMInitializeNativeTarget()

        val mod = LLVM.LLVMModuleCreateWithName("test_module")
        val builder = LLVM.LLVMCreateBuilder()

        assert(mod is LLVMModuleRef)

        LLVM.LLVMDisposeModule(mod)
        LLVM.LLVMDisposeBuilder(builder)
    }
}
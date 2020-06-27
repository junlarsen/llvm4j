package dev.supergrecko.vexe.llvm.integration.jni

import org.bytedeco.llvm.global.LLVM
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

internal object JNITest : Spek({
    describe("Using the raw LLVM bindigs") {
        LLVM.LLVMShutdown()
    }
})
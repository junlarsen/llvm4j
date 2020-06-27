package dev.supergrecko.vexe.llvm.integration.jni

import org.bytedeco.llvm.global.LLVM
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

internal object JNITest : Spek({
    group("using the raw LLVM bindings") {
        test("shuts down without any problems") {
            LLVM.LLVMShutdown()
        }
    }
})
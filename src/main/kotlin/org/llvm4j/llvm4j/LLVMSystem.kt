package org.llvm4j.llvm4j

import org.bytedeco.javacpp.BytePointer
import org.bytedeco.llvm.LLVM.LLVMFatalErrorHandler
import org.llvm4j.llvm4j.util.Callback

public object LLVMSystem {
    public class FatalErrorHandler(private val closure: (Payload) -> Unit) :
        LLVMFatalErrorHandler(),
        Callback<Unit, FatalErrorHandler.Payload> {
        public override fun invoke(ctx: Payload): Unit = closure(ctx)

        public override fun call(p0: BytePointer) {
            val copy = p0.string
            val data = Payload(copy)

            p0.deallocate()

            return invoke(data)
        }

        public data class Payload(public val details: String)
    }
}

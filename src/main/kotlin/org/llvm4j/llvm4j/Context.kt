package org.llvm4j.llvm4j

import org.bytedeco.javacpp.Pointer
import org.bytedeco.llvm.LLVM.LLVMContextRef
import org.bytedeco.llvm.LLVM.LLVMDiagnosticHandler
import org.bytedeco.llvm.LLVM.LLVMDiagnosticInfoRef
import org.bytedeco.llvm.LLVM.LLVMYieldCallback
import org.bytedeco.llvm.global.LLVM
import org.llvm4j.llvm4j.util.Callback
import org.llvm4j.llvm4j.util.None
import org.llvm4j.llvm4j.util.Option
import org.llvm4j.llvm4j.util.Owner
import org.llvm4j.llvm4j.util.Some

public class Context public constructor(
    ptr: LLVMContextRef = LLVM.LLVMContextCreate()
) : Owner<LLVMContextRef> {
    public override val ref: LLVMContextRef = ptr

    public class DiagnosticHandler(public override val closure: (Payload) -> Unit) : LLVMDiagnosticHandler(),
        Callback<Unit, DiagnosticHandler.Payload> {
        public override fun call(p0: LLVMDiagnosticInfoRef, p1: Pointer?) {
            val info = DiagnosticInfo(p0)
            val payload = p1?.let { Some(it) } ?: None
            val data = Payload(info, payload)

            return closure.invoke(data)
        }

        public data class Payload(
            public val info: DiagnosticInfo,
            public val payload: Option<Pointer>
        )
    }

    public class YieldCallback(public override val closure: (Payload) -> Unit) : LLVMYieldCallback(),
        Callback<Unit, YieldCallback.Payload> {
        public override fun call(p0: LLVMContextRef, p1: Pointer?) {
            val context = Context(p0)
            val payload = p1?.let { Some(it) } ?: None
            val data = Payload(context, payload)

            return closure.invoke(data)
        }

        public data class Payload(
            public val context: Context,
            public val payload: Option<Pointer>
        )
    }
}

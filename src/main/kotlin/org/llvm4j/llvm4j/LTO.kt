package org.llvm4j.llvm4j

import org.bytedeco.javacpp.BytePointer
import org.bytedeco.javacpp.Pointer
import org.bytedeco.llvm.LLVM.lto_code_gen_t
import org.bytedeco.llvm.LLVM.lto_diagnostic_handler_t
import org.bytedeco.llvm.LLVM.lto_input_t
import org.bytedeco.llvm.LLVM.lto_module_t
import org.llvm4j.llvm4j.util.Callback
import org.llvm4j.llvm4j.util.None
import org.llvm4j.llvm4j.util.Option
import org.llvm4j.llvm4j.util.Owner
import org.llvm4j.llvm4j.util.Some

public typealias LTOCodeGenRef = lto_code_gen_t
public typealias LTOInputRef = lto_input_t
public typealias LTOModuleRef = lto_module_t
public typealias LTODiagnosticHandler = lto_diagnostic_handler_t

public class LTOCodeGen public constructor(ptr: LTOCodeGenRef) : Owner<LTOCodeGenRef> {
    public override val ref: LTOCodeGenRef = ptr

    public class DiagnosticHandler(private val closure: (Payload) -> Unit) :
        LTODiagnosticHandler(),
        Callback<Unit,
            DiagnosticHandler.Payload> {
        public override fun invoke(ctx: Payload): Unit = closure(ctx)

        public override fun call(p0: Int, p1: BytePointer, p2: Pointer?) {
            val message = p1.string
            val payload = p2?.let { Some(it) } ?: None
            val data = Payload(p0, message, payload)

            p1.deallocate()

            return invoke(data)
        }

        public data class Payload(
            public val severity: Int,
            public val message: String,
            public val payload: Option<Pointer>
        )
    }
}

public class LTOInput public constructor(ptr: LTOInputRef) : Owner<LTOInputRef> {
    override val ref: LTOInputRef = ptr
}

public class LTOModule public constructor(ptr: LTOModuleRef) : Owner<LTOModuleRef> {
    public override val ref: LTOModuleRef = ptr
}

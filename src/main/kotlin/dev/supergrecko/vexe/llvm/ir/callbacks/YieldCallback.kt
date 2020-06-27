package dev.supergrecko.vexe.llvm.ir.callbacks

import dev.supergrecko.vexe.llvm.internal.contracts.Callback
import dev.supergrecko.vexe.llvm.internal.util.wrap
import dev.supergrecko.vexe.llvm.ir.Context
import org.bytedeco.javacpp.Pointer
import org.bytedeco.llvm.LLVM.LLVMContextRef
import org.bytedeco.llvm.LLVM.LLVMYieldCallback

/**
 * The yield callback function may be called by LLVM to transfer control back
 * to the client that invoked the LLVM compilation. There is no guarantee
 * that this callback ever goes off.
 *
 * [Context] The context this was set to
 * [Pointer] The payload which was sent with the setter for this callback
 */
public typealias YieldCallback = (YieldCallbackContext) -> Unit

public data class YieldCallbackContext(
    public val context: Context,
    public val payload: Pointer?
)

public class YieldCallbackBase(
    private val callback: YieldCallback
) : LLVMYieldCallback(), Callback {
    public override fun call(arg0: LLVMContextRef, arg1: Pointer?) {
        val data = YieldCallbackContext(
            context = Context(arg0),
            payload = arg1
        )

        callback.invoke(data)
    }
}

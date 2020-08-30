package io.vexelabs.bitbuilder.llvm.ir.callbacks

import io.vexelabs.bitbuilder.llvm.internal.contracts.Callback
import io.vexelabs.bitbuilder.llvm.ir.Context
import org.bytedeco.javacpp.Pointer
import org.bytedeco.llvm.LLVM.LLVMContextRef
import org.bytedeco.llvm.LLVM.LLVMYieldCallback

/**
 * Kotlin lambda type for [LLVMYieldCallback]
 *
 * The yield callback function may be called by LLVM to transfer control back
 * to the client that invoked the LLVM compilation. There is no guarantee
 * that this callback ever goes off.
 *
 * @see LLVMYieldCallback
 */
public typealias YieldCallback = (YieldCallbackContext) -> Unit

/**
 * Data payload for [YieldCallback]
 *
 * @property context The context callback was triggered on
 * @property payload Opaque pointer value assigned at callback registration
 */
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

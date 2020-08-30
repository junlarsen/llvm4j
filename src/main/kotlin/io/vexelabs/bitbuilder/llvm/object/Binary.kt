package io.vexelabs.bitbuilder.llvm.`object`

import io.vexelabs.bitbuilder.llvm.internal.contracts.Disposable
import org.bytedeco.llvm.LLVM.LLVMBinaryRef
import org.bytedeco.llvm.global.LLVM

/**
 * Interface to llvm::object::Binary
 *
 * TODO: Implement API bridge
 *
 * @see LLVMBinaryRef
 */
public class Binary internal constructor() : Disposable {
    public override var valid: Boolean = true
    public lateinit var ref: LLVMBinaryRef
        internal set

    public constructor(llvmRef: LLVMBinaryRef) : this() {
        ref = llvmRef
    }

    public override fun dispose() {
        require(valid) { "Cannot dispose object twice" }

        valid = false

        LLVM.LLVMDisposeBinary(ref)
    }
}

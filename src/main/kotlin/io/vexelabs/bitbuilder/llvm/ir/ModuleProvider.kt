package io.vexelabs.bitbuilder.llvm.ir

import io.vexelabs.bitbuilder.llvm.internal.contracts.ContainsReference
import io.vexelabs.bitbuilder.llvm.internal.contracts.Disposable
import org.bytedeco.llvm.LLVM.LLVMModuleProviderRef
import org.bytedeco.llvm.global.LLVM

/**
 * Changes the type of a module so it can be passed to FunctionPassManagers and
 * the JIT. They take ModuleProviders for historical reasons.
 *
 * Every function which accepts a [ModuleProvider] optionally accepts a
 * [Module] as well.
 */
@Deprecated("Deprecated, use Module instead")
public class ModuleProvider internal constructor() :
    Disposable,
    ContainsReference<LLVMModuleProviderRef> {
    private lateinit var module: Module
    public override var valid: Boolean = true
    public override lateinit var ref: LLVMModuleProviderRef
        internal set

    /**
     * Create a Module provider from a module
     *
     * @see LLVMModuleProviderRef
     */
    public constructor(parent: Module) : this() {
        require(parent.valid) {
            "Cannot retrieve Module Provider of deleted " +
                "module"
        }
        module = parent
        ref = LLVM.LLVMCreateModuleProviderForExistingModule(parent.ref)
    }

    public override fun dispose() {
        require(valid) { "Cannot dispose object twice" }

        valid = false

        LLVM.LLVMDisposeModuleProvider(ref)
    }
}

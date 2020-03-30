package dev.supergrecko.kllvm.core.enumerations

import dev.supergrecko.kllvm.contracts.OrderedEnum
import org.bytedeco.llvm.global.LLVM

/**
 * Support type matching LLVMModuleFlagBehavior
 *
 * [Documentation](https://llvm.org/doxygen/group__LLVMCCoreTypes.html)
 */
public enum class ModuleFlagBehavior(public override val value: Int) : OrderedEnum<Int> {
    Error(LLVM.LLVMModuleFlagBehaviorError),
    Warning(LLVM.LLVMModuleFlagBehaviorWarning),
    Require(LLVM.LLVMModuleFlagBehaviorRequire),
    Override(LLVM.LLVMModuleFlagBehaviorOverride),
    Append(LLVM.LLVMModuleFlagBehaviorAppend),
    AppendUnique(LLVM.LLVMModuleFlagBehaviorAppendUnique)
}

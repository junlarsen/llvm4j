package io.vexelabs.bitbuilder.llvm.debuginfo

import io.vexelabs.bitbuilder.internal.toLLVMBool
import io.vexelabs.bitbuilder.llvm.internal.contracts.ContainsReference
import io.vexelabs.bitbuilder.llvm.internal.contracts.Disposable
import org.bytedeco.llvm.LLVM.LLVMDIBuilderRef
import org.bytedeco.llvm.global.LLVM

public class DIBuilder internal constructor() :
    ContainsReference<LLVMDIBuilderRef>, Disposable {
    public override var valid: Boolean = true
    public override lateinit var ref: LLVMDIBuilderRef

    /**
     * A CompileUnit provides an anchor for all debugging information
     * generated during this instance of compilation.
     *
     * @param language Source programming language
     * @param file File info
     * @param producer Identify the producer of debugging information and
     * code. Usually this is a compiler version string.
     * @param isOptimized A boolean flag which indicates whether optimization
     * is enabled or not
     * @param flags This string lists command line options. This string is
     * directly embedded in debug info output which may be used by a tool
     * analyzing generated debugging information
     * @param runtimeVersion This indicates runtime version for languages
     * like Objective-C
     * @param splitName The name of the file that we'll split debug info out
     * into
     * @param emissionKind The kind of debug information info to generate
     * @param DWOId The DWOId if this is a split skeleton compile unit
     * @param splitDebugInlining Whether to emit inline debug info
     * @param debugInfoForProfiling Whether to emit extra debug info for
     * profile collection
     * @param sysRoot The clang system root (value of -isysroot)
     * @param sdk The SDK. On Darwin, the last component of the sysroot
     */
    public fun createCompileUnit(
        language: DWARFSourceLanguage,
        file: DIMetadata,
        producer: String,
        isOptimized: Boolean,
        flags: String,
        runtimeVersion: Int,
        splitName: String,
        emissionKind: DWARFEmission,
        DWOId: Int,
        splitDebugInlining: Boolean,
        debugInfoForProfiling: Boolean,
        sysRoot: String,
        sdk: String
    ):
    DIMetadata {
        val unit = LLVM.LLVMDIBuilderCreateCompileUnit(
            ref,
            language.value,
            file.ref,
            producer,
            producer.length.toLong(),
            isOptimized.toLLVMBool(),
            flags,
            flags.length.toLong(),
            runtimeVersion,
            splitName,
            splitName.length.toLong(),
            emissionKind.value,
            DWOId,
            splitDebugInlining.toLLVMBool(),
            debugInfoForProfiling.toLLVMBool(),
            sysRoot,
            sysRoot.length.toLong(),
            sdk,
            sdk.length.toLong()
        )

        return DIMetadata(unit)
    }

    public override fun dispose() {
        require(valid) { "Cannot dispose object twice" }

        valid = false

        LLVM.LLVMDisposeDIBuilder(ref)
    }
}

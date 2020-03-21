package dev.supergrecko.kllvm.annotation

import dev.supergrecko.kllvm.core.enumerations.LLVMValueKind

/**
 * Informs that this method expects some kind [kinds]
 */
@Target(AnnotationTarget.FUNCTION)
public annotation class ExpectsValue(public vararg val kinds: LLVMValueKind)

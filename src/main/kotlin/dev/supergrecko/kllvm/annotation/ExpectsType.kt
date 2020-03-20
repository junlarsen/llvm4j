package dev.supergrecko.kllvm.annotation

import dev.supergrecko.kllvm.core.enumerations.LLVMTypeKind

/**
 * Informs that this method expects some kind [kinds]
 */
@Target(AnnotationTarget.FUNCTION)
public annotation class ExpectsType(public vararg val kinds: LLVMTypeKind)

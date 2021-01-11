package org.llvm4j.llvm4j.testing

import org.llvm4j.llvm4j.util.Option
import org.llvm4j.llvm4j.util.Result
import kotlin.test.assertTrue

internal fun <T> assertIsNone(opt: Option<T>) = assertTrue(opt.isEmpty(), "Expected $opt to be None")
internal fun <T> assertIsSome(opt: Option<T>) = assertTrue(opt.isDefined(), "Expected $opt to be Some")
internal fun <T> assertIsOk(res: Result<T>) = assertTrue(res.isOk(), "Expected $res to be Ok")
internal fun <T> assertIsErr(res: Result<T>) = assertTrue(res.isErr(), "Expected $res to be Err")
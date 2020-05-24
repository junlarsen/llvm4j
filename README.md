# Kotlin LLVM

<p align="center">
  <img alt="Logo" src="assets/kllvm.png" width="480" height="480" />
</p>

![Build & Test for Ubuntu + Windows + OSX](https://github.com/supergrecko/kllvm/workflows/Build%20&%20Test%20for%20Ubuntu%20+%20Windows%20+%20OSX/badge.svg)

There were no decent options to utilize the LLVM from JVM. KLLVM is the missing link between Kotlin/JVM and LLVM.

KLLVM aims to cover most of the LLVM-C API. This is made possible using the [generated LLVM bindings for Java](https://github.com/bytedeco/javacpp-presets/tree/master/llvm). 
While the JNI bindings allow you to use the LLVM from Kotlin it is not done in a very pretty way. KLLVM wraps around these bindings to provide you a more natural way of working
with the LLVM. 

### Meta

KLLVM is in active development. There are no public releases yet.

Interested in contributing? Shoot me an email at [(me at supergrecko dot dev)](mailto:me@supergrecko.dev), open an issue or join [the Discord](https://discord.gg/MPWD84h)
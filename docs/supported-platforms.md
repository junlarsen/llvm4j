# Supported Platforms

Bitbuilder runs on a lot of the popular platforms nowadays, but its test suite
is only ran on `macos-x86_64`, `windows-x86_64` and `linux-x86_64` as these are
the architectures GitHub Actions' hosted runners provide. That being said, the
library should perform on each platform Bytedeco's LLVM presets is compiled for
([see bytedeco llvm presets][bytedeco-presets-cppbuild]). These platforms
include:

- linux-x86
- linux-x86_64
- linux-armhf
- linux-arm64
- linux-ppc64le
- macos-x86_64
- windows-x86
- windows-x86_64

In other words, no, the library won't run on every host LLVM itself runs on.

If the platform you wish to run the library on is not supported, your best bet
would be to open a PR to [the javacpp-presets@llvm][bytedeco-presets-llvm]
repository, adding a build script for your target platform. Once this is done,
open an issue at this repository requesting a dependency update.

[bytedeco-presets-cppbuild]: https://github.com/bytedeco/javacpp-presets/blob/master/llvm/cppbuild.sh
[bytedeco-presets-llvm]: https://github.com/bytedeco/javacpp-presets/tree/master/llvm
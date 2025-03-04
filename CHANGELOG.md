<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# Salesforce Commerce Cloud (SFCC) Studio

## [Unreleased]

## [2024.2.1] - 2025-03-04

### Fixed

- Breakpoint path not being set correctly when the cartridge path did not start at the root of the project

## [2024.2.0]

### Updated

- Updated compatibility

### Fixed

- Inability to set breakpoints due to behavior change in xLineBreakpoint.presentableFilePath

## [2024.1.0]

- Updated compatibility

## [2023.1.0]

- Updated compatibility

## [2022.1.0]

- Updated compatibility

## [2021.2.1-beta.1]

### Added

- Improved Debugger breakpoint console output

## [2021.2.1-beta.0]

### Added

- Individual cartridge upload
- Improved Sync log formatting

### Fixed

- Require references and completion for repos with nested cartridges

## [2021.2.0]

### Updated

- Updated to work with the 2021.2 release

## [2020.1.0]

### Updated

- Moved to new versioning scheme
- Updated to work with the upcoming 2021.1 release

### Fixed

- Kotlin Serialization dependency

## [1.8.0]

### Added

- Basic autocomplete and custom icon for dw.json file
- Require function file references for goto declaration support
- Require function auto complete for cartridge modules

### Updated

- Now requires Java 11 and 2020.3 IDE versions and above

## [1.8.0-beta.4]

### Fixed

- window path issue with ~/ completion

## [1.8.0-beta.3]

## [1.8.0-beta.2]

### Added

- Scoped require ~/ completion to current file's cartridge path

## [1.8.0-beta.1]

### Added

- Initial scaffold created from [IntelliJ Platform Plugin Template](https://github.com/JetBrains/intellij-platform-plugin-template)
- dw.json icon and autocomplete
- require function file references for go to declaration support
- require function auto complete for cartridge modules

### Updated

- Require Java 11 and 2020.3 IDE versions and above

[Unreleased]: https://github.com/nek4life/sfcc-studio/compare/v2024.2.1...HEAD
[2024.2.1]: https://github.com/nek4life/sfcc-studio/compare/v2024.2.0...v2024.2.1
[2024.2.0]: https://github.com/nek4life/sfcc-studio/compare/v2024.1.0...v2024.2.0
[2024.1.0]: https://github.com/nek4life/sfcc-studio/compare/v2023.1.0...v2024.1.0
[2023.1.0]: https://github.com/nek4life/sfcc-studio/compare/v2022.1.0...v2023.1.0
[2022.1.0]: https://github.com/nek4life/sfcc-studio/compare/v2021.2.1-beta.1...v2022.1.0
[2021.2.1-beta.1]: https://github.com/nek4life/sfcc-studio/compare/v2021.2.1-beta.0...v2021.2.1-beta.1
[2021.2.1-beta.0]: https://github.com/nek4life/sfcc-studio/compare/v2021.2.0...v2021.2.1-beta.0
[2021.2.0]: https://github.com/nek4life/sfcc-studio/compare/v2020.1.0...v2021.2.0
[2020.1.0]: https://github.com/nek4life/sfcc-studio/compare/v1.8.0...v2020.1.0
[1.8.0]: https://github.com/nek4life/sfcc-studio/compare/v1.8.0-beta.4...v1.8.0
[1.8.0-beta.4]: https://github.com/nek4life/sfcc-studio/compare/v1.8.0-beta.3...v1.8.0-beta.4
[1.8.0-beta.3]: https://github.com/nek4life/sfcc-studio/compare/v1.8.0-beta.2...v1.8.0-beta.3
[1.8.0-beta.2]: https://github.com/nek4life/sfcc-studio/compare/v1.8.0-beta.1...v1.8.0-beta.2
[1.8.0-beta.1]: https://github.com/nek4life/sfcc-studio/commits/v1.8.0-beta.1

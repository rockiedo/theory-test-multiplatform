# Overview

This app helps anyone practice driving theory test exams. It is built with Compose Multiplatform and can run on both Android and iOS.

<img src="https://github.com/rockiedo/theory-test-multiplatform/assets/11256533/6c651be8-010c-449c-bc3b-3c1bb06750a9" width="260"/> <img src="https://github.com/rockiedo/theory-test-multiplatform/assets/11256533/8512201e-5191-410a-9208-a3b4b84e4684" width="260"/> <img src="https://github.com/rockiedo/theory-test-multiplatform/assets/11256533/8bae4935-b583-41c6-9b80-4fb400036346" width="260"/>

# Project setup

To build this project you need to have these dependencies ready:
- JDK 11
- Gradle 8.4
- CocoaPods

Besides, Android Studio and Intellij are the preferred IDEs. You also need to install several plugins:
- [Kotlin Multiplatform Mobile](https://plugins.jetbrains.com/plugin/14936-kotlin-multiplatform-mobile).
- [SQLDelight](https://plugins.jetbrains.com/plugin/8191-sqldelight). This project relys on SQLDelight for handling on-device database. The plugin helps with syntax highlighting and code generation.

# Directory hierarchy

Most of the code is kept under the **shared** folder with the following structure.

![folder-hierarchy](https://github.com/rockiedo/theory-test-multiplatform/assets/11256533/ef312084-54bb-42df-a129-001be7b0c9bf)

- **commonMain** is where we keep code that is independent of platforms. We also define `expect` interfaces here.
  - There's a **sqldelight** directory. It contains database tables and functions that will be generated to Kotlin code by the sqldelight gradle plugin.
- **androidMain** and **iosMain** are where we implement the `actual` interfaces defined by *commonMain* such as database drivers, views, etc.

# Project architecture

This project follows the `MVVM` architecture and is organized by feature.

![project-structure](https://github.com/rockiedo/theory-test-multiplatform/assets/11256533/a90558e4-e51d-493f-8d86-29e723e503bf)

# License

This project is open-source under the [MIT](https://github.com/rockiedo/theory-test-multiplatform/blob/main/LICENSE) license.

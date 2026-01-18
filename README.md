# 📸 Pics – A Camera App

**Pics** is a modern, high-performance Android camera application designed to demonstrate a clean implementation of the [CameraX](https://developer.android.com/training/camerax) library using [Jetpack Compose](https://developer.android.com/jetpack/compose). Unlike generic camera samples, **Pics** focuses on a unified user experience for both photography and videography, featuring real-time state management and a reactive UI.

---

## 🚀 Core Features

- **Dual-Mode Capture**: Seamlessly switch between high-resolution **Photo Capture** and **Video Recording** within a single interface.
- **Smart Video Controls**: Real-time **Pause, Resume, and Stop** functionality during video recording, ensuring precise control over your content.
- **Integrated Media Preview**: An in-app **Media Bottom Sheet** that allows users to instantly view their recently captured photos and videos without leaving the camera screen.
- **Lens Management**: Quick toggle between **Front and Back cameras** with automatic state preservation.
- **Intelligent Permissions**: A proactive permission handling system that guides users through granting Camera and Microphone access.
- **Lifecycle Efficiency**: Deep integration with Android Lifecycle components to ensure the camera hardware is only active when needed, optimizing battery life and system resources.

---

## 🏗️ Repository & Project Structure

The project follows a modular **MVVM (Model-View-ViewModel)** architecture, separating hardware interaction, business logic, and UI components.

### **Key Directories & Modules**

- **[camera/](file:///d:/contri/Pics---A-Camera-App-main/app/src/main/java/com/example/pics/camera/)**: The engine of the application.
    - [CameraActions.kt](file:///d:/contri/Pics---A-Camera-App-main/app/src/main/java/com/example/pics/camera/CameraActions.kt): Encapsulates all CameraX logic, including photo capture callbacks and video recording state transitions.
    - [CameraPreview.kt](file:///d:/contri/Pics---A-Camera-App-main/app/src/main/java/com/example/pics/camera/CameraPreview.kt): A Compose wrapper for the `PreviewView`, bridging the gap between traditional Android Views and Declarative UI.
- **[ui/](file:///d:/contri/Pics---A-Camera-App-main/app/src/main/java/com/example/pics/ui/)**: All visual components.
    - [CameraScreen.kt](file:///d:/contri/Pics---A-Camera-App-main/app/src/main/java/com/example/pics/ui/CameraScreen.kt): The primary layout that orchestrates the camera feed, capture buttons, and state indicators.
    - [PhotoBottomSheet.kt](file:///d:/contri/Pics---A-Camera-App-main/app/src/main/java/com/example/pics/ui/PhotoBottomSheet.kt): A sophisticated preview gallery implementation using Material 3.
- **[viewmodel/](file:///d:/contri/Pics---A-Camera-App-main/app/src/main/java/com/example/pics/viewmodel/)**:
    - [MainViewModel.kt](file:///d:/contri/Pics---A-Camera-App-main/app/src/main/java/com/example/pics/viewmodel/MainViewModel.kt): Manages the application's "Source of Truth," tracking recording status, captured media lists, and UI states via `StateFlow`.
- **[utils/](file:///d:/contri/Pics---A-Camera-App-main/app/src/main/java/com/example/pics/utils/)**:
    - [PermissionUtils.kt](file:///d:/contri/Pics---A-Camera-App-main/app/src/main/java/com/example/pics/utils/PermissionUtils.kt): Utility functions for modern Android permission workflows.

### **Important Dependencies**
- **CameraX (Core, Camera2, Lifecycle, Video, View)**: Chosen for its device compatibility and automatic lifecycle handling.
- **Jetpack Compose**: Used for building a reactive, high-performance UI.
- **Kotlin Coroutines & Flow**: Essential for handling asynchronous camera operations without blocking the main thread.
- **Material 3**: Provides the modern design language and pre-built UI components.

---

## 🛠️ Implementation & Architecture Details

### **How Camera Functionality is Implemented**
The application uses the `LifecycleCameraController` in [MainActivity.kt](file:///d:/contri/Pics---A-Camera-App-main/app/src/main/java/com/example/pics/MainActivity.kt) as a high-level API. This controller is bound to the `LocalLifecycleOwner`, meaning the camera automatically stops when the app is minimized and restarts when it returns to the foreground.

### **Unique Customizations**
- **Unified Controller**: Instead of manually managing `Preview`, `ImageCapture`, and `VideoCapture` use cases, we use the `LifecycleCameraController` to simplify state management.
- **In-Memory Gallery**: To provide an instant-preview experience, captured photos are stored as `Bitmap` objects in a `StateFlow` within the [MainViewModel.kt](file:///d:/contri/Pics---A-Camera-App-main/app/src/main/java/com/example/pics/viewmodel/MainViewModel.kt), allowing the UI to react immediately to new captures.
- **Emulator Stability**: In [CameraActions.kt](file:///d:/contri/Pics---A-Camera-App-main/app/src/main/java/com/example/pics/camera/CameraActions.kt), we have a specialized configuration for video recording that disables audio by default to prevent crashes on Android Emulators, which often have unstable audio drivers.

### **Architectural Decisions**
- **Stateless UI**: The [CameraScreen.kt](file:///d:/contri/Pics---A-Camera-App-main/app/src/main/java/com/example/pics/ui/CameraScreen.kt) is largely stateless, receiving its configuration and data from the [MainViewModel.kt](file:///d:/contri/Pics---A-Camera-App-main/app/src/main/java/com/example/pics/viewmodel/MainViewModel.kt). This makes the UI easier to test and modify.
- **Separation of Concerns**: UI components never interact with the CameraX API directly; they delegate all actions to [CameraActions.kt](file:///d:/contri/Pics---A-Camera-App-main/app/src/main/java/com/example/pics/camera/CameraActions.kt).

---

## 🚀 Building & Running

### **Environment Requirements**
- **Android Studio**: Ladybug (2024.2.1) or newer.
- **JDK**: Java 17.
- **Android Device**: Physical device recommended (API 24+).

### **Steps to Run**
1. **Clone the repository**:
   ```bash
   git clone https://github.com/Vaibhav-P1/Pics---A-Camera-App.git
   ```
2. **Open in Android Studio**: Select the root folder and wait for the Gradle sync to complete.
3. **Configure JDK**: Ensure your project is set to use **Java 17** in `File > Settings > Build, Execution, Deployment > Build Tools > Gradle`.
4. **Deploy**: Connect your Android device and click the **Run** icon.

---

## 🤝 Contributing & Extending

We welcome contributions to **Pics**! To get started:

1. **Check the Roadmap**: Look at the current [README.md](file:///d:/contri/Pics---A-Camera-App-main/README.md) roadmap or open issues for feature requests.
2. **Fork & Branch**: Create a feature branch for your changes.
3. **Code Style**: Follow the existing Kotlin style and ensure all new UI components use Jetpack Compose.
4. **Pull Request**: Submit a detailed PR describing your changes.

For more details, see our [CONTRIBUTING.md](file:///d:/contri/Pics---A-Camera-App-main/CONTRIBUTING.md).

### **Ideas for Extending**
- Implement **Persistent Storage** for media files (using MediaStore).
- Add **Flash and Zoom** controls to the UI.
- Integrate **CameraX Extensions** (Bokeh, Face Retouch).

---

## 📄 License
This project is licensed under the **MIT License**.

Happy coding! 🚀

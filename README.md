# FacePix - Face Detection and Tagging App

FacePix is an Android application that scans the user's photo gallery, detects faces in images using MediaPipe, and allows users to tag identified faces. The app demonstrates modern Android development practices, including modular architecture, MVVM pattern, and efficient image processing.

## Features

- 📱 Photo gallery scanning with runtime permissions
- 👤 Face detection using Google MediaPipe
- 🏷️ Face tagging functionality
- 📦 Offline support with local database storage
- 🎯 Real-time face detection visualization
- ⚡ Optimized performance for large photo galleries

## Architecture & Technical Stack

### Architecture
- **MVVM (Model-View-ViewModel)** pattern for clean separation of concerns
- **Clean Architecture** principles with modular design
- **Repository Pattern** for data management

### Modules
```
app/
├── core/
│   ├── data/         # Data repositories
│   ├── database/     # Room database implementation
│   ├── facedetector/ # MediaPipe face detection
│   ├── model/        # Domain models
│   └── ui/          # Common UI components
└── feature/
    ├── home/         # Main screen functionality
    └── detail/       # Image detail and tagging
```

### Tech Stack
- **Hilt** for dependency injection
- **MediaPipe** for face detection
- **Room** for local data persistence
- **Kotlin Coroutines & Flow** for asynchronous operations
- **Jetpack Compose** for modern UI implementation
- **CameraX** for camera integration
- **AndroidX** libraries

## Implementation Details

### Face Detection
- Utilizes MediaPipe Tasks library for accurate face detection
- Processing happens in background threads to maintain UI responsiveness
- Images are processed sequentially to manage memory efficiently

### Data Storage
- Room database for storing face tags and processed image information
- Efficient querying for quick access to tagged faces
- Type converters for complex data types

### Performance Optimization
- Background processing of images
- Efficient memory management for large photo galleries
- Sequential processing to prevent OOM errors
- Caching of processed results

## Setup Instructions

1. Clone the repository
```bash
git clone https://github.com/yourusername/facepix.git
```

2. Open the project in Android Studio

3. Build and run the application

## Required Permissions
- `READ_EXTERNAL_STORAGE` for accessing photo gallery

## Future Improvements
- Implement WorkManager for background processing
- Add facial recognition for automatic tagging suggestions
- Enhance UI/UX with additional filters and sorting options
- Add batch processing capabilities
- Implement cloud backup for tags

## License

```
Copyright 2024 Somasundaram M

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

## Credits
- [Google MediaPipe](https://developers.google.com/mediapipe)

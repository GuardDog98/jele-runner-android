# Jele Runner Android (Kotlin OpenGL ES prototype)

This repository contains a minimal native Android prototype written in Kotlin using OpenGL ES 2.0. It includes on-screen virtual buttons (Left / Right / Jump) and a simple 3D cube representing "Jele" that runs forward, can switch lanes and jump. This is an initial prototype to be opened in Android Studio.

Minimum Android: API 29 (Android 10)

Controls:
- Left button: move left lane
- Right button: move right lane
- Jump button: jump

Notes:
- This is a simple proof-of-concept renderer and input flow. It uses a basic shader pipeline to draw a colored cube and plane.
- Next steps: polish model/mesh, add animations, sound, UI, save system, and more advanced rendering.

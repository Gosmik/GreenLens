# Green Lens

<p align="center">
  <strong>An Android app that lets you scan product barcodes or search for products to view nutritional information and dietary labels (vegan/vegetarian).</strong>
</p>

<p align="center">
  <a href="https://www.android.com/"><img src="https://img.shields.io/badge/Platform-Android-3DDC84?style=flat&logo=android&logoColor=white" alt="Platform"></a>
  <a href="https://developer.android.com/about/versions/oreo/"><img src="https://img.shields.io/badge/Min%20SDK-26-blue?style=flat" alt="Min SDK"></a>
  <a href="https://kotlinlang.org/"><img src="https://img.shields.io/badge/Kotlin-2.2.21-7F52FF?style=flat&logo=kotlin&logoColor=white" alt="Kotlin"></a>
  <a href="https://developer.android.com/jetpack/compose"><img src="https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?style=flat&logo=jetpackcompose&logoColor=white" alt="Compose"></a>
  <a href="https://developer.android.com/studio/releases/platforms"><img src="https://img.shields.io/badge/Target%20SDK-36-blue?style=flat" alt="Target SDK"></a>
  <a href="https://developer.android.com/studio/releases/build-tools"><img src="https://img.shields.io/badge/Compile%20SDK-36-blue?style=flat" alt="Compile SDK"></a>
  <a href="LICENSE"><img src="https://img.shields.io/badge/License-GPLv3-blue?style=flat" alt="License"></a>
</p>

## 📱 Features

- **Barcode Scanner** – Scan product barcodes using your camera
- **Product Search** – Search for products by name
- **Nutritional Info** – View calories, protein, carbohydrates and fat per 100g
- **Dietary Labels** – See whether a product is vegan or vegetarian
- **Clipboard** – Copy barcodes with one tap

---

## 🛠️ Tech Stack

| Technology | Usage |
|---|---|
| Kotlin | Programming language |
| Jetpack Compose | UI |
| CameraX | Barcode scanning |
| ML Kit | Barcode detection |
| Ktor | HTTP client |
| Kotlinx Serialization | JSON parsing |
| MVVM | Architecture |
| OpenFoodFacts API | Product data |

---

## 🏗️ Architecture

The app follows the **MVVM (Model-View-ViewModel)** architecture pattern.

```
de.gosmik.greenlens/
├── data/
│   ├── barcode/
│   └── openfoodfacts/
│       ├── model/
│       ├── api/
│       └── repository/
└── ui/
    ├── screen/
    │   ├── main/
    │   ├── barcode/
    │   └── licenses/
    ├── components/
    └── theme/
```

---

## 🚀 Getting Started

### Prerequisites

- Android Studio Hedgehog or newer
- Android SDK 26+
- Kotlin 2.0+

### Installation

1. Clone the repository
```bash
git clone https://github.com/gomsik/greenlens.git
```

2. Open the project in Android Studio

3. Build and run on your device or emulator

---

## 📦 Dependencies

```kotlin
// CameraX
implementation("androidx.camera:camera-core:1.4.2")
implementation("androidx.camera:camera-camera2:1.4.2")
implementation("androidx.camera:camera-lifecycle:1.4.2")
implementation("androidx.camera:camera-view:1.4.2")
implementation("androidx.camera:camera-compose:1.0.0")

// ML Kit
implementation("com.google.mlkit:barcode-scanning:17.3.0")

// Ktor
implementation("io.ktor:ktor-client-android:2.3.7")
implementation("io.ktor:ktor-client-content-negotiation:2.3.7")
implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.7")
```

---

## 🌍 Data Source

Product data is provided by [OpenFoodFacts](https://world.openfoodfacts.org/) – a free, open and collaborative food products database.

---

## 📄 License

This project is licensed under the MIT License – see the [LICENSE](LICENSE) file for details.

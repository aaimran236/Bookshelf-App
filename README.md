Bookshelf App
[![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-0095D5?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%2520Compose-4285F4?style=for-the-badge&logo=jetpack-compose&logoColor=white)](https://developer.android.com/jetpack/compose)
==================================
This project is the result of completing the "Android Basics with Compose: Unit 5, Pathway 2 Project:Create a Bookshelf App" codelab from Google. The Bookshelf app demonstrates modern Android development practices, fetching data from a remote API and displaying it in a responsive, grid-based UI.

The app searches for books using the Google Books API and displays their cover thumbnails in a vertically scrolling grid. This project showcases key skills in networking, state management, dependency injection, and building dynamic user interfaces with Jetpack Compose.

## 📸 Screenshots

| Final Implementation |
| :---: |
 | <img width="300" height="852" src="https://github.com/user-attachments/assets/7e8a9f80-63ee-4112-9c88-e96af25f9c2c"/>  <img width="300" height="852" src="https://github.com/user-attachments/assets/da4c8d0f-fbc5-4b59-b8a5-ec386a83031b"/> 
 <img width="300" height="852" src="https://github.com/user-attachments/assets/4bdacdc9-1544-4460-a5e6-68b110a1427f"/> <img width="300" height="852" src="https://github.com/user-attachments/assets/056e24e5-b452-4334-ba08-e341daa46616"/> | 
## 🎥 Video Demo

A short video demonstrating the app's features, including the loading state and the final thumbnail grid.
https://github.com/user-attachments/assets/ee413ecf-3fca-4482-91ed-0dc118d5f666


## ✨ Features

- **Remote Data Fetching:** Communicates with the Google Books API using Retrofit.
- **Dynamic Image Grid:** Displays book thumbnails fetched from the network in a `LazyVerticalGrid`.
- **Asynchronous Image Loading:** Efficiently loads and caches images using the Coil library.
- **UI State Management:** Manages UI state (Loading, Success, Error) in a `ViewModel` using a `sealed interface`, ensuring a predictable and robust user experience.
- **Repository Pattern:** Abstracts the data source from the UI layer, following modern architectural best practices.
- **Dependency Injection:** Utilizes a dependency injection container to provide dependencies (like the repository) to different parts of the app.
- **Responsive UI:** The grid layout adapts to different screen sizes.
- **Material 3 Design:** Implements the latest Material Design components, including a custom-themed `TopAppBar` and `Card` views.

## 🛠️ Tech Stack & Concepts Learned

This project was an opportunity to learn and implement a variety of core Android concepts:

- **UI (Jetpack Compose):**
  - `Scaffold`, `TopAppBar`, `Card`
  - `LazyVerticalGrid` for building efficient, scrollable grids.
  - State management in Compose using `mutableStateOf`.
- **Architecture:**
  - **MVVM (Model-View-ViewModel):** Separating UI logic from business logic.
  - **Repository Pattern:** Creating a clean abstraction for the data layer.
  - **Dependency Injection:** Using a manual DI container (`AppContainer`) to manage dependencies and improve testability.
- **Networking:**
  - **Retrofit:** For making type-safe HTTP requests to the Google Books API.
  - **Gson:** For parsing JSON responses into Kotlin data classes.
  - Handling complex, multi-step network requests (fetching a list, then fetching details for each item).
- **Coroutines:**
  - Managing asynchronous operations and background tasks.
  - Using `viewModelScope` for lifecycle-aware coroutines.
- **Image Loading:**
  - **Coil:** For asynchronously loading and displaying images from URLs.
- **Testing:**
  - **JUnit:** For unit testing.
  - Wrote unit tests for the `ViewModel` and the data repository.
  - **Faking Dependencies:** Created fake data sources and repositories to test components in isolation without making real network calls.

## 🚀 How to Build and Run

1.  **Clone the repository:**
    

2.  **Open in Android Studio:**
    - Open Android Studio (Hedgehog or newer is recommended).
    - Select `File > Open` and navigate to the cloned repository folder.

3.  **Build the project:**
    - Let Android Studio sync the Gradle files.
    - Click the "Run" button (▶️) and select an emulator or a connected physical device.

The app requires an internet connection to fetch data from the Google Books API.


## 📝 Project Structure

The project follows a standard Android app structure, organized by feature and layer:

- **`data/`**: Contains the data models (`Book`, `BookResponse`), the repository (`BookThumbnailRepository`), and the networking setup (`BookApiService`, `AppContainer`).
- **`ui/screens/`**: Contains the `ViewModel` (`BookShelfViewModel`) and the Composable UI screens (`HomeScreen`).
- **`ui/theme/`**: Contains the app's theme, colors, and typography.
- **`src/test/`**: Contains unit tests for the ViewModel and repository, along with fake data sources.

## Reflections & Future Improvements

This project was a comprehensive exercise in connecting a Compose UI to a live network source. Key challenges included managing the two-step data fetch process and correctly implementing the repository pattern for clean architecture.

Potential future improvements could include:

- **Pagination:** Implement "infinite scrolling" to load more books as the user scrolls to the bottom of the list.
- **Detail Screen:** Allow users to tap on a book thumbnail to navigate to a new screen showing more details about the book.

## 🙏 Acknowledgements

This project was made possible thanks to the fantastic work of the open-source community. I would like to acknowledge the key libraries that were instrumental in its development:
- [Project:Create a Bookshelf App (Codelab)](https://developer.android.com/codelabs/basic-android-kotlin-compose-bookshelf?continue=https%3A%2F%2Fdeveloper.android.com%2Fcourses%2Fpathways%2Fandroid-basics-compose-unit-5-pathway-2%23codelab-https%3A%2F%2Fdeveloper.android.com%2Fcodelabs%2Fbasic-android-kotlin-compose-bookshelf)
- [Retrofit](https://square.github.io/retrofit/): A type-safe HTTP client for Android and Java, used for making network requests to the Google Books API.
- [Gson](https://github.com/google/gson): A powerful Java serialization/deserialization library to convert JSON responses into Kotlin data objects.
- [Coil](https://coil-kt.github.io/coil/): An image loading library for Android backed by Kotlin Coroutines, used to efficiently load and display book thumbnails.
- [Jetpack Compose](https://developer.android.com/jetpack/compose): Google's modern toolkit for building native Android UI.
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html): For managing asynchronous operations gracefully.


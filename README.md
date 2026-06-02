Bookshelf App
[![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-0095D5?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%2520Compose-4285F4?style=for-the-badge&logo=jetpack-compose&logoColor=white)](https://developer.android.com/jetpack/compose)
==================================
This project is a fully expanded implementation based on the "Create a Bookshelf App" codelab from Google’s *Android Basics with Compose* pathway. It is designed around modern Android development standards, showcasing modular design, network integration, paginated lists, declarative navigation, and unidirectional data flow.

The application allows users to search for books dynamically using the Google Books API, displaying results in an infinite-scrolling paginated grid. Tapping on any item navigates to a details screen that displays more complete information about the selected book.

## 📸 Screenshots

| Search & Pagination | Book Details |
| :---: | :---: |
| <img width="300" src="https://github.com/user-attachments/assets/f45a003a-7fd4-41b5-a658-acc8825a3056"/>&nbsp;&nbsp;&nbsp;&nbsp;<img width="300" src="https://github.com/user-attachments/assets/b18569d8-64f6-4a58-9325-6e92765e1dcb"/> | <img width="300" src="https://github.com/user-attachments/assets/9d81120c-b6c4-4d24-84d5-8a866257ce97"/>&nbsp;&nbsp;&nbsp;&nbsp; <img width="300" src="https://github.com/user-attachments/assets/c9b8ad0a-0e9c-4da5-a232-e2733295c462"/>

## 🎥 Video Demo

A short video demonstrating the app's loading states, pagination scroll transitions, search execution, and detailed book previews.
<video src="https://github.com/user-attachments/assets/9749bfcd-fb6c-4b61-a938-dcb8f3891906" controls loop width="100%">
</video>

## ✨ Features

- **Dynamic Query Search:** Real-time query validation and input processing. The UI updates instantly when a new search is successfully submitted.
- **Deduplicated Paginated Loading (Paging 3):** Incorporates the Jetpack Paging library to handle chunked API data transfers safely. It limits initial load bounds and dynamically filters out duplicate API-returned items using a memory-efficient set evaluation.
- **Type-Safe Multi-Screen Navigation:** Utilizes Jetpack Compose Navigation (NavHost) to manage deep-level navigation, cleanly passing arguments between screens.
- **Network Resiliency & Data Filtering:** Cleans incomplete network responses prior to presentation using safe mapping extensions (toBookGridItemOrNull), bypassing structural network anomalies without halting the user experience.
- **Dependency Injection (Service Locator):** Uses an implementation of a manual dependency container (AppContainer) initialized at the Application level, facilitating lifecycle-appropriate instances.
- **Asynchronous Image Loading:** Efficiently loads and caches images using the Coil library.
- **State Preservation:** Keeps loaded pages in memory over device configuration changes (e.g., orientation changes) using cachedIn(viewModelScope).


## 🛠️ Tech Stack & Concepts Learned

This project implements architectural concepts built for stability, testability, and clarity:

### 🏛️ Architecture & State Management
- **MVVM Pattern:** Separates UI rendering logic from data sourcing.
- **Unidirectional Data Flow (UDF):** Actions flow up (via lambda expressions) and State flows down (via `StateFlow` and read-only Compose States) [1.1.1].
- **Repository Pattern:** Abstracts raw API endpoints into cleanly scoped operations (`BookRepository`).
- **State Machines:** Details screens represent async outcomes using a `sealed interface` (`BookDetailsUiState`), rendering structured Loading, Error, or Success widgets depending on the state.

### 🔌 Network & Core Libraries
- **Retrofit & Gson:** Manages raw HTTP interactions with the Google Books API.
- **OkHttp Interceptors:** Injects API keys across outbound connections without polluting specific api-service interface definitions.
- **Coroutines & Flow:** Handles async database, local state transitions, and background execution blocks cleanly.
- **Jetpack Paging 3:** Coordinates list growth as the user scrolls, minimizing redundant network traffic .

## 🔬 Core Code Implementation Details

The implementation highlights critical architectural decisions designed for stability:

### 1. Robust Pagination & Deduplication
The Google Books API does not guarantee unique entry payloads across sliding start-indexes. Our custom `PagingSource` addresses this constraint by maintaining a local record of unique primary keys to prevent UI conflicts:

```kotlin
private val returnedIds = mutableSetOf<String>()

override suspend fun load(params: LoadParams<Int>): LoadResult<Int, BookGridItem> {
    val startIndex = params.key ?: STARTING_INDEX
    return try {
        val response = bookApiService.searchBooks(
            searchQuery = query,
            startIndex = startIndex,
            maxResults = PAGE_SIZE,
        )

        val books = response.items
            .orEmpty()
            .mapNotNull { it.toBookGridItemOrNull() }
            .filter { book -> returnedIds.add(book.id) } // Drops already-loaded entries

        LoadResult.Page(
            data = books,
            prevKey = if (startIndex == STARTING_INDEX) null else startIndex - PAGE_SIZE,
            nextKey = if (books.isEmpty()) null else startIndex + books.size,
        )
    } catch (e: Exception) {
        LoadResult.Error(e)
    }
}
```

### 2. Live Flow Switching & Lifecycle Caching
To maintain a high performance search, the query emission triggers a transformation via `flatMapLatest`. This cancels the network activity of the stale query when the user triggers a new search . We secure cache boundaries on rotation by applying `.cachedIn(viewModelScope)`:

```kotlin
@OptIn(ExperimentalCoroutinesApi::class)
val bookPagingFlow: Flow<PagingData<BookGridItem>> = _activeQuery
    .flatMapLatest { query -> bookRepository.getBooksPagingFlow(query) }
    .cachedIn(viewModelScope)
```

## 🚀 How to Build and Run

1. **Clone the repository**
   ```bash
   git clone https://github.com/aaimran236/Bookshelf-App.git
   cd Bookshelf-App
   ```

2. **Add Your API Key**
   To access the Google Books API securely, append your API key inside your project's local properties or configuration setup, aligning with your build setup:
   ```properties
   BOOKS_API_KEY="your_api_key_here"
   ```

3. **Open in Android Studio:**
   - Open Android Studio (Hedgehog or newer recommended).
   - Select `File > Open` and select the cloned root directory.

4. **Sync and Run:**
   - Allow Gradle to sync the core project configurations.
   - Run the app (▶️) on an emulator or an active physical testing device. An active internet connection is required to fetch real-time book contents.


## 📝 Project Structure

The standard directory structure of the application is organized as follows:

```text
app/
└── src/
    └── main/
        └── java/
            └── com/
                └── example/
                    └── bookshelfapp/
                        ├── BookSelfApplication.kt
                        ├── AppViewModelProvider.kt
                        │
                        ├── data/
                        │   ├── AppContainer.kt
                        │   ├── BookApiService.kt
                        │   ├── BookPagingSource.kt
                        │   └── BookRepository.kt
                        │
                        └── ui/
                            ├── navigation/
                            │   └── BookShelfNavHost.kt
                            │
                            ├── screens/
                            │   ├── BookDetailsScreen.kt
                            │   ├── BookDetailsViewModel.kt
                            │   ├── HomeScreen.kt
                            │   └── BookShelfViewModel.kt
                            │
                            └── theme/
                                ├── Color.kt
                                ├── Theme.kt
                                └── Type.kt
```

## 🛠️ Future Roadmap

Planned enhancements to expand on the current implementation include:
- **Offline Caching (Room):** Cache paginated items to a local SQLite database to provide a seamless offline-first experience.
- **Local Favorites:** Support persistent bookmarking of book entries directly in local storage.
- **Expanded Detail Previews:** Render richer metadata content (e.g., sample reading paths, publisher notes, and category classification tags) on the details view.

## 🙏 Acknowledgements

This project was made possible thanks to the fantastic work of the open-source community. I would like to acknowledge the key libraries that were instrumental in its development:
- [Project:Create a Bookshelf App (Codelab)](https://developer.android.com/codelabs/basic-android-kotlin-compose-bookshelf?continue=https%3A%2F%2Fdeveloper.android.com%2Fcourses%2Fpathways%2Fandroid-basics-compose-unit-5-pathway-2%23codelab-https%3A%2F%2Fdeveloper.android.com%2Fcodelabs%2Fbasic-android-kotlin-compose-bookshelf)
- [Retrofit](https://square.github.io/retrofit/): A type-safe HTTP client for Android and Java, used for making network requests to the Google Books API.
- [Gson](https://github.com/google/gson): A powerful Java serialization/deserialization library to convert JSON responses into Kotlin data objects.
- [Coil](https://coil-kt.github.io/coil/): An image loading library for Android backed by Kotlin Coroutines, used to efficiently load and display book thumbnails.
- [Jetpack Compose](https://developer.android.com/jetpack/compose): Google's modern toolkit for building native Android UI.
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html): For managing asynchronous operations gracefully.


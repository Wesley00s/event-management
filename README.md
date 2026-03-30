# Event Management System

An Android application designed to manage complex event logistics, featuring guest tracking, budget control, and schedule management. Developed entirely in Kotlin with a focus on clean architecture and modern Android development practices.

## Features

* **Guest Management:** Track invitations, RSVPs, and manage attendee groups.
* **Budget Tracking:** Monitor overall event expenses, categorize spending, and calculate outstanding balances.
* **Schedule Logistics:** Manage event timelines and task checklists.
* **Secure Authentication:** User authentication and secure cloud data synchronization.

## Built With

This project leverages modern Android development tools and libraries to ensure a scalable and maintainable codebase.

**Core & Architecture**
* **Kotlin:** First-class programming language for Android development.
* **MVVM Architecture:** Separates the UI from the business logic, improving testability and maintenance.
* **Dagger Hilt:** Dependency injection framework to manage object lifecycles and dependencies efficiently.
* **Coroutines & Flow:** For handling asynchronous operations, background tasks, and reactive data streams.

**User Interface**
* **Jetpack Compose:** Modern, declarative UI toolkit for building native Android interfaces.

**Data & Backend**
* **Room Database:** SQLite object mapping library used for robust local data storage and offline caching.
* **Firebase Firestore:** Cloud-hosted NoSQL database for storing and synchronizing event data in real-time.
* **Firebase Authentication:** Secure backend service utilized to manage user sessions and access control.

## Project Structure

* `app/src/main/java/.../ui`: Contains all UI screens and Jetpack Compose components.
* `app/src/main/java/.../viewmodel`: Lifecycle-aware ViewModels managing UI state and user interactions.
* `app/src/main/java/.../data`: Repositories, local DAOs, and remote data source implementations.
* `app/src/main/java/.../domain`: Core business logic, models, and use cases.

## Getting Started

To build and run this project locally:

1. Clone the repository:
   ```bash
   git clone [https://github.com/Wesley00s/event-management.git](https://github.com/Wesley00s/event-management.git)
   ```

2. Open the project in Android Studio.

3. Configure Firebase credentials: 
   To enable remote features, create a project in the Firebase Console, download your own `google-services.json` file, and place it in the `app/` directory.

4. Build and deploy the project to an emulator or physical device.

## License

This project is licensed under the [MIT License](https://github.com/Wesley00s/event-management/blob/main/LICENSE).

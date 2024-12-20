
# Spaces by YG - Room Booking System

## Overview

**Spaces by YG** is an Android application that enables young creatives to book meeting spaces like the **Creative Room** or **Conference Room** for collaboration and idea-sharing. Users can fill out a booking form, which is then sent to an admin for confirmation or rejection.

## Features

- **Room Booking**: Select from available rooms and specify booking details.
- **Admin Confirmation**: Booking requests are sent to the admin for approval.
- **Email Notifications**: Receive updates on your booking status.
- **Admin Panel**: Administrators can manage bookings and room availability.

## Tech Stack

- **Language**: Kotlin
- **IDE**: Android Studio
- **Backend**: Firebase (Authentication, Firestore Database, Cloud Functions)
- **UI Components**: Material Design Components

## Installation

### Clone the Repository

1. **Clone the Repository**

   ```bash
   git clone https://github.com/engNesta/SpacesbyYG.git
   ```

### Open the Project

2. **Open the Project**

   - Launch **Android Studio**.
   - Click on **"Open an existing project"** and select the cloned repository folder.

### Sync with Gradle

3. **Sync with Gradle**

   - Android Studio will prompt you to sync the project with Gradle files.
   - Click **"Sync Now"** to download all the necessary dependencies.

### Set Up Firebase

4. **Set Up Firebase**

   - **Create a Firebase Project**:
      - Go to the [Firebase Console](https://console.firebase.google.com/) and create a new project.
   - **Add Android App to Firebase**:
      - Register your app with your app's package name (found in your `app/build.gradle` file).
      - Download the `google-services.json` file provided by Firebase.
      - Place the `google-services.json` file in the

app

directory of your project.
- **Enable Firebase Services**:
   - In the Firebase Console, enable **Authentication** and **Firestore Database**.
   - Set up authentication methods (e.g., Email/Password) in the **Authentication** section.
   - In **Firestore Database**, create the necessary collections and documents as per the app's requirements.

### Configure Firebase in the Project

5. **Configure Firebase in the Project**

   - Ensure the following lines are in your project's `build.gradle` files:

     **In the root `build.gradle` file:**

     ```gradle
     buildscript {
         dependencies {
             // Add this line
             classpath 'com.google.gms:google-services:4.3.13'
         }
     }
     ```

     **In the `app/build.gradle` file:**

     ```gradle
     // At the top of the file
     plugins {
         id 'com.android.application'
         id 'com.google.gms.google-services'
     }

     dependencies {
         // Firebase BOM (Bill of Materials)
         implementation platform('com.google.firebase:firebase-bom:31.2.3')

         // Firebase SDKs
         implementation 'com.google.firebase:firebase-auth-ktx'
         implementation 'com.google.firebase:firebase-firestore-ktx'

         // Material Design Components
         implementation 'com.google.android.material:material:1.9.0'

         // Other dependencies
         // ...
     }
     ```

### Install Required Dependencies

6. **Install Required Dependencies**

   - After updating the `build.gradle` files, click on **"Sync Now"** when prompted to download all dependencies.

### Build and Run the App

7. **Build and Run the App**

   - Click on **"Build"** > **"Make Project"** to build the app.
   - Connect an Android device or start an emulator.
   - Click on **"Run"** > **"Run 'app'"** to launch the application.

## Usage

### Users

1. **Run the App**

   Run the app on an Android emulator or a physical device.

2. **Navigate and Book**

   Navigate through the booking options to select either a **Creative Room** or **Conference Room**.

3. **Complete the Form**

   Complete the booking form and submit it to notify the admin.

4. **Wait for Confirmation**

   Wait for a confirmation email with your booking status.

### Admins

1. **Access the Admin Panel**

   Log in to the app using your admin credentials.

2. **View Booking Requests**

   Navigate to the **Admin Panel** to see all pending booking requests.

3. **Approve or Reject Requests**

   - To approve a request, tap **"Approve"** next to the booking.
   - To reject a request, tap **"Reject"** and optionally provide a reason.

4. **Update Booking Status**

   Users will be notified of the decision via email or in-app notification.

## License

This project is licensed under the MIT License. See the LICENSE file for details.
```
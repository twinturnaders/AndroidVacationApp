# Vacation Scheduler - Android Mobile Application

## Purpose
The Vacation Schedule is an Android mobile application developed for the WGU D308 performance assessment. It allows users to manage vacations and excursions with a user-friendly interface. The app enables adding, editing, deleting, and viewing vacations and excursions, setting alerts, and sharing vacation details.

---

## Features:

### Vacation Management:
- Add, edit, and delete vacations
- Vacation fields: Title, Hotel/Accommodation, Start Date, End Date, Phone Number
- Validation: Start date must precede end date, and be formatted correctly
- Ability to create an alert that triggers on Vacation Start date and Vacation End date
- Share vacation details via clipboard or messaging

### Excursion Management
- Add, edit, and delete excursions for each vacation
- Excursion fields: Title and Date
- Validation: Excursion date must be within vacation date range
- Ability to create an alert that triggers on the day of the excursion.
- Excursions are only available after the vacation is saved

###Alert System
- Uses Android AlarmManager to schedule toast-style alerts for:
    - Vacation Start Date
    - Vacation End Date
    - Excursion Date

###Sharing
- Vacation details can be shared via system share sheet (ie, messaging, clipboard)

### Search
- Users can search for an existing vacation by phone number
- Phone number is auto-filled when returning to the home screen from vacation activity or vacation detail activity

---

##Technologies Used
- Java -Android 8.0+
- Room Database- for persistent storage
- ViewModel + LiveData for lifecycle/data management
- AlarmManager for alert scheduling
- RecyclerView for dynamic list display
- ViewBinding for safe UI access

---

## Screens and Navigation
The app includes:
- Home screen (search and navigation)
- Vacation detail with excursion list (navigation to home screen & vacation edit)
- Vacation add/edit screen with excursion list (navigation to excursions, vacation detail, and home screen)
- Excursion detail/edit screen /add/delete (navigation to home screen and vacation edit)
- Sharing and alert features (available on all screens except the home screen)

A storyboard outlining the screen flow is included in `Storyboard - Vacation Scheduler.pdf`.

---

## APK Deployment
- Signed APK built targeting Android 8.0 (API 26) and higher
- Screenshots of APK generation included in submission

---

## Git Repository
https://gitlab.com/wgu-gitlab-environment/student-repos/bsonju1/d308-mobile-application-development-android
Includes commit history showing implementation progress across:
- Vacation features
- Excursion features
- Alert/validation logic
- UI styling and improvements

---

## How to Use the App
1. Launch the app
2. Add a new vacation (required before adding excursions)
3. Save the vacation to unlock excursion buttons
4. Add/edit/delete excursions for the vacation
5. Optionally set alerts and share vacation info
6. Use the home screen to return or search by phone number
7. The vacation details screen will launch after the previously created vacation is searched for, returned, and selected.
8. Optional edit vacation, return home, share vacation, or delete vacation.

---

##Contact & Submission Notes
This app was developed as a performance assessment project for WGU's D308 - Mobile Application Development course. All functionality has been tested in both emulator and physical device environments for compatibility with rubric requirements.

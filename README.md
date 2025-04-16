# PlacementHub

PlacementHub is a comprehensive Android application designed to streamline the placement process between educational institutions and students. It provides a platform for placement cell administrators to manage company drives and for students to apply for these opportunities.

## Features

### For Students
- 📱 User-friendly dashboard
- 📊 View placement drives (Upcoming, Ongoing, Past)
- 📝 Apply for eligible drives
- 🔔 Receive notifications about new drives
- 📈 Track application status
- 👤 Manage personal profile

### For Placement Cell
- 👥 Student management (add, edit, remove)
- 📥 Bulk import students via CSV
- 🏢 Company drive management
- 🎯 Student selection for drives
- 📢 Send notifications to students
- 📊 Track student applications

## Technical Details

### Architecture
- MVVM (Model-View-ViewModel) architecture
- Room Database for local storage
- LiveData for reactive UI updates
- WorkManager for background tasks
- Material Design components

### Tech Stack
- Kotlin/Java
- Android SDK
- Room Database
- WorkManager
- Material Design
- File Provider API

## Getting Started

### Prerequisites
- Android Studio (latest version)
- Android SDK (API level 24 or higher)
- Java Development Kit (JDK 11 or higher)

### Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/PlacementHub.git
   ```

2. Open the project in Android Studio

3. Build and run the app on an emulator or physical device

## Project Structure
```
app/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           └── placementhub/
│   │   │               ├── data/
│   │   │               │   ├── models/
│   │   │               │   ├── database/
│   │   │               │   └── repository/
│   │   │               ├── ui/
│   │   │               │   ├── auth/
│   │   │               │   ├── student/
│   │   │               │   └── placement/
│   │   │               └── utils/
│   │   └── res/
│   └── test/
```

## Database Schema

### Student
- sapId (Primary Key)
- fullName
- email
- password
- branch
- rollNumber
- cgpa
- gender
- backlogs
- batch

### Drive
- driveId (Primary Key)
- companyName
- role
- package
- location
- startDate
- deadline
- driveDate
- industryType
- workType
- jobType
- interviewMode
- eligibilityCriteria
- selectionProcess
- description

### StudentDrive
- id (Primary Key)
- studentId (Foreign Key)
- driveId (Foreign Key)
- status
- applicationDate

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- Material Design Components
- Android Architecture Components
- Room Database
- WorkManager

## Contact

Your Name - [@yourtwitter](https://twitter.com/yourtwitter)

Project Link: [https://github.com/yourusername/PlacementHub](https://github.com/yourusername/PlacementHub)

## Screenshots

[Add screenshots of your app here]

## Future Enhancements

- [ ] Cloud synchronization
- [ ] Real-time notifications
- [ ] Document upload support
- [ ] Analytics dashboard
- [ ] Multi-language support 
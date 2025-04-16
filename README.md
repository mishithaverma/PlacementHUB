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

Your Name - Mishitha

Project Link: (https://github.com/mishithaverma/PlacementHUB.git)

## Screenshots
![Screenshot 2025-04-16 212836](https://github.com/user-attachments/assets/9088c781-6264-4627-b508-836578c330cc)
![Screenshot 2025-04-16 213452](https://github.com/user-attachments/assets/602ad8fb-4211-42f9-9172-36a207bfbc3d)
![Screenshot 2025-04-16 213215](https://github.com/user-attachments/assets/9767a71a-b075-4ce9-aa67-aa068e9801ea)
![Screenshot 2025-04-16 213509](https://github.com/user-attachments/assets/cffc0414-f425-4a6f-bcf9-a30b248832a2)
![Screenshot 2025-04-16 215443](https://github.com/user-attachments/assets/2f2bb777-f9e6-47f1-8e2b-a8f8dc985776)
![Screenshot 2025-04-16 215457](https://github.com/user-attachments/assets/a2dd2f9c-5258-4902-a0ac-2090e3facb65)
![Screenshot 2025-04-16 215509](https://github.com/user-attachments/assets/c38c03e7-6498-4eb1-9ab0-7432e454c4d5)
![Screenshot 2025-04-16 215525](https://github.com/user-attachments/assets/c05091b8-e490-43d1-b4d7-80ca74017573)
![Screenshot 2025-04-16 215547](https://github.com/user-attachments/assets/2c51c453-d9d2-41fc-b4ed-e5254e96f25d)
![Screenshot 2025-04-16 213553](https://github.com/user-attachments/assets/64363bce-5bfc-4620-b3cb-30cd298c5456)
![Screenshot 2025-04-16 213812](https://github.com/user-attachments/assets/a7126106-3113-4f40-9f3b-37e186542344)
![Screenshot 2025-04-16 214203](https://github.com/user-attachments/assets/818310b9-e4f6-4165-b77b-387f62d5422b)
![Screenshot 2025-04-16 214115](https://github.com/user-attachments/assets/768fb868-0d96-4405-8229-e485dd4049bb)
![Screenshot 2025-04-16 214449](https://github.com/user-attachments/assets/14f95641-91b9-422c-afd1-7ef9119560a5)
![Screenshot 2025-04-16 214721](https://github.com/user-attachments/assets/d58fd8b7-e096-4a3c-94a3-6ac9640e56a4)
![Screenshot 2025-04-16 214928](https://github.com/user-attachments/assets/e39c27ba-fe04-4f85-bf13-9469d2b687c0)
![Screenshot 2025-04-16 214940](https://github.com/user-attachments/assets/a408a424-d65e-4a78-9e4f-c9f2847c5a23)
![Screenshot 2025-04-16 214954](https://github.com/user-attachments/assets/99fee316-fc84-4fc0-a57f-5370e942f607)


## Future Enhancements

- [ ] Cloud synchronization
- [ ] Real-time notifications
- [ ] Document upload support
- [ ] Analytics dashboard
- [ ] Multi-language support 

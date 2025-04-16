# Placement Management App - Technical Documentation

## 1. Project Overview

The Placement Management App is an Android application designed to streamline the placement process for a college. It serves three types of users: Students, Placement Members, and Admins (Placement Cell Admin).

## 2. Technical Stack

- **Platform**: Android
- **Language**: Java
- **Database**: SQLite (Local) / Firebase (Future Implementation)
- **Authentication**: Firebase Authentication (Future Implementation)
- **UI Framework**: Material Design Components
- **Architecture**: MVVM (Model-View-ViewModel)
- **Dependencies**:
  - AndroidX Core
  - Material Design
  - Room Database
  - ViewModel and LiveData
  - Navigation Component
  - Firebase (Future Implementation)

## 3. Project Structure

```
com.example.placementhub/
├── data/
│   ├── models/
│   │   ├── Student.java
│   │   ├── PlacementDrive.java
│   │   ├── Application.java
│   │   └── Notification.java
│   ├── database/
│   │   └── AppDatabase.java
│   └── repositories/
│       ├── StudentRepository.java
│       ├── DriveRepository.java
│       └── NotificationRepository.java
├── ui/
│   ├── auth/
│   │   ├── LoginActivity.java
│   │   └── RegisterActivity.java
│   ├── student/
│   │   ├── StudentDashboardActivity.java
│   │   ├── DriveListActivity.java
│   │   └── ProfileActivity.java
│   ├── placement/
│   │   ├── PlacementDashboardActivity.java
│   │   └── ApplicationManagementActivity.java
│   └── admin/
│       ├── AdminDashboardActivity.java
│       ├── DriveManagementActivity.java
│       └── AnalyticsActivity.java
├── viewmodels/
│   ├── AuthViewModel.java
│   ├── StudentViewModel.java
│   └── AdminViewModel.java
└── utils/
    ├── Constants.java
    └── Helpers.java
```

## 4. Database Schema

### Student Table
```sql
CREATE TABLE students (
    sap_id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    email TEXT UNIQUE NOT NULL,
    branch TEXT NOT NULL,
    cgpa REAL NOT NULL,
    password TEXT NOT NULL,
    is_placement_member BOOLEAN DEFAULT FALSE,
    access_code TEXT
);
```

### Placement Drive Table
```sql
CREATE TABLE placement_drives (
    drive_id TEXT PRIMARY KEY,
    company_name TEXT NOT NULL,
    job_role TEXT NOT NULL,
    description TEXT,
    eligibility_criteria TEXT,
    deadline DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Application Table
```sql
CREATE TABLE applications (
    application_id TEXT PRIMARY KEY,
    student_id TEXT,
    drive_id TEXT,
    status TEXT,
    applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(sap_id),
    FOREIGN KEY (drive_id) REFERENCES placement_drives(drive_id)
);
```

### Notification Table
```sql
CREATE TABLE notifications (
    notification_id TEXT PRIMARY KEY,
    title TEXT NOT NULL,
    message TEXT NOT NULL,
    type TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    target_audience TEXT
);
```

## 5. User Interface Components

### 5.1 Common Components
- Material Design AppBar
- Bottom Navigation
- RecyclerViews for lists
- Material Design Cards
- Custom Dialogs
- Progress Indicators

### 5.2 Screen Layouts

#### Student Screens
1. **Registration Screen**
   - Form with validation
   - Fields: SAP ID, Name, Email, Branch, CGPA, Password
   - Submit button
   - Link to login

2. **Login Screen**
   - Email/SAP ID field
   - Password field
   - Login button
   - Register link
   - Forgot password option

3. **Student Dashboard**
   - Upcoming drives section
   - Notifications section
   - Application status section
   - Calendar widget
   - Profile quick actions

4. **Drive Listing**
   - Filterable list of drives
   - Search functionality
   - Sort options
   - Drive cards with key information

#### Placement Member Screens
1. **Placement Dashboard**
   - Drive management section
   - Application review section
   - Student shortlisting interface
   - Notification management

#### Admin Screens
1. **Admin Dashboard**
   - Drive management
   - Student management
   - Placement member management
   - Analytics dashboard
   - Notification center

## 6. Business Logic

### 6.1 Authentication Flow
1. User selects role (Student/Admin)
2. For students:
   - New users register with required details
   - Existing users login with credentials
   - Placement members enter additional access code
3. For admins:
   - Login with predefined credentials
   - Session management

### 6.2 Placement Drive Management
1. Admin creates new drive
2. System notifies eligible students
3. Students can apply within deadline
4. Placement members can review applications
5. Admin can shortlist candidates

### 6.3 Notification System
1. System notifications for:
   - New drives
   - Application deadlines
   - Interview schedules
   - Status updates
2. Admin can send targeted notifications
3. Real-time updates using Firebase (future)

## 7. Security Considerations

1. Password hashing
2. Session management
3. Input validation
4. Access control
5. Data encryption
6. Secure API communication

## 8. Future Enhancements

1. Firebase Integration
   - Authentication
   - Real-time Database
   - Cloud Messaging
2. Analytics Dashboard
3. Chat System
4. Document Upload
5. Email Integration
6. Mobile Notifications

## 9. Testing Strategy

1. Unit Tests
   - Repository layer
   - ViewModel layer
   - Utility functions

2. Integration Tests
   - Database operations
   - Navigation flows
   - Authentication flows

3. UI Tests
   - Screen navigation
   - Form validation
   - User interactions

## 10. Deployment

1. Version Control
   - Git repository
   - Branch management
   - Code review process

2. Build Process
   - Gradle configuration
   - ProGuard rules
   - Release signing

3. Distribution
   - Internal testing
   - Beta testing
   - Production release

## 11. Maintenance

1. Regular Updates
   - Bug fixes
   - Feature additions
   - Performance optimization

2. Monitoring
   - Crash reporting
   - Usage analytics
   - Performance metrics

3. Documentation
   - Code documentation
   - API documentation
   - User guides 
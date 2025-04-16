package com.example.placementhub.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.placementhub.data.models.Student;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CsvHelper {
    private static final String TAG = "CsvHelper";
    
    // Required fields
    private static final String[] REQUIRED_FIELDS = {
            "sapId", "fullName", "email", "password", "branch", "rollNumber"
    };
    
    // All possible fields
    private static final String[] ALL_FIELDS = {
            "sapId", "fullName", "email", "password", "branch", 
            "rollNumber", "cgpa", "gender", "backlogs", "batch"
    };

    /**
     * Parse and validate CSV file from URI
     * @param context Application context
     * @param fileUri URI of the CSV file
     * @return ParseResult containing the parsed students or error message
     */
    public static ParseResult parseStudentCsv(Context context, Uri fileUri) {
        List<Student> students = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        
        if (fileUri == null) {
            errors.add("File URI is null");
            return new ParseResult(null, errors);
        }

        // Use a much more straightforward approach
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(fileUri);
            if (inputStream == null) {
                errors.add("Failed to open file stream");
                return new ParseResult(null, errors);
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String headerLine = reader.readLine();
            
            if (headerLine == null || headerLine.trim().isEmpty()) {
                errors.add("CSV file is empty or has no headers");
                reader.close();
                inputStream.close();
                return new ParseResult(null, errors);
            }
            
            // Simple CSV parsing (no fancy library)
            String[] headers = headerLine.split(",");
            Map<String, Integer> headerMap = new HashMap<>();
            
            // Basic header validation
            for (int i = 0; i < headers.length; i++) {
                headerMap.put(headers[i].trim().toLowerCase(), i);
            }
            
            // Check for required headers
            for (String required : REQUIRED_FIELDS) {
                if (!headerMap.containsKey(required.toLowerCase())) {
                    errors.add("Missing required header: " + required);
                }
            }
            
            if (!errors.isEmpty()) {
                reader.close();
                inputStream.close();
                return new ParseResult(null, errors);
            }
            
            String line;
            int lineNumber = 1; // Header was line 1
            
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                
                if (line.trim().isEmpty()) {
                    continue; // Skip empty lines
                }
                
                // Basic CSV parsing - split by comma
                // Note: This doesn't handle commas within quoted fields properly
                // For a production app, you'd want a proper CSV parser
                String[] values = line.split(",");
                
                if (values.length < headerMap.size()) {
                    errors.add("Line " + lineNumber + ": Not enough values");
                    continue;
                }
                
                try {
                    // Create a new student with default values
                    Student student = new Student();
                    
                    // Set simple string fields
                    for (String field : new String[]{"sapId", "fullName", "email", "password", "branch", "rollNumber", "gender", "batch"}) {
                        if (headerMap.containsKey(field.toLowerCase())) {
                            int index = headerMap.get(field.toLowerCase());
                            if (index < values.length) {
                                String value = values[index].trim();
                                
                                switch (field) {
                                    case "sapId":
                                        student.setSapId(value);
                                        break;
                                    case "fullName":
                                        student.setFullName(value);
                                        break;
                                    case "email":
                                        student.setEmail(value);
                                        break;
                                    case "password":
                                        student.setPassword(value);
                                        break;
                                    case "branch":
                                        student.setBranch(value);
                                        break;
                                    case "rollNumber":
                                        student.setRollNumber(value);
                                        break;
                                    case "gender":
                                        student.setGender(value);
                                        break;
                                    case "batch":
                                        student.setBatch(value);
                                        break;
                                }
                            }
                        }
                    }
                    
                    // Parse numeric fields carefully
                    if (headerMap.containsKey("cgpa")) {
                        try {
                            int index = headerMap.get("cgpa");
                            if (index < values.length && !values[index].trim().isEmpty()) {
                                student.setCgpa(Float.parseFloat(values[index].trim()));
                            } else {
                                student.setCgpa(0.0f); // Default
                            }
                        } catch (NumberFormatException e) {
                            student.setCgpa(0.0f); // Default on error
                            errors.add("Line " + lineNumber + ": Invalid CGPA format");
                        }
                    }
                    
                    if (headerMap.containsKey("backlogs")) {
                        try {
                            int index = headerMap.get("backlogs");
                            if (index < values.length && !values[index].trim().isEmpty()) {
                                student.setBacklogs(Integer.parseInt(values[index].trim()));
                            } else {
                                student.setBacklogs(0); // Default
                            }
                        } catch (NumberFormatException e) {
                            student.setBacklogs(0); // Default on error
                            errors.add("Line " + lineNumber + ": Invalid backlogs format");
                        }
                    }
                    
                    // Validate required fields 
                    boolean isValid = true;
                    if (student.getSapId() == null || student.getSapId().isEmpty()) {
                        errors.add("Line " + lineNumber + ": SAP ID is required");
                        isValid = false;
                    }
                    
                    if (student.getFullName() == null || student.getFullName().isEmpty()) {
                        errors.add("Line " + lineNumber + ": Full Name is required");
                        isValid = false;
                    }
                    
                    if (student.getEmail() == null || student.getEmail().isEmpty()) {
                        errors.add("Line " + lineNumber + ": Email is required");
                        isValid = false;
                    }
                    
                    if (student.getPassword() == null || student.getPassword().isEmpty()) {
                        errors.add("Line " + lineNumber + ": Password is required");
                        isValid = false;
                    }
                    
                    if (student.getBranch() == null || student.getBranch().isEmpty()) {
                        errors.add("Line " + lineNumber + ": Branch is required");
                        isValid = false;
                    }
                    
                    if (student.getRollNumber() == null || student.getRollNumber().isEmpty()) {
                        errors.add("Line " + lineNumber + ": Roll Number is required");
                        isValid = false;
                    }
                    
                    if (isValid) {
                        students.add(student);
                    }
                    
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing student at line " + lineNumber, e);
                    errors.add("Line " + lineNumber + ": " + e.getMessage());
                }
            }
            
            reader.close();
            inputStream.close();
            
            // If we have both students and errors, still return the valid students
            if (students.isEmpty() && !errors.isEmpty()) {
                return new ParseResult(null, errors);
            }
            
            return new ParseResult(students, errors.isEmpty() ? null : errors);
            
        } catch (IOException e) {
            Log.e(TAG, "Error reading CSV file", e);
            errors.add("Failed to read CSV file: " + e.getMessage());
            return new ParseResult(null, errors);
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error parsing CSV", e);
            errors.add("Unexpected error: " + e.getMessage());
            return new ParseResult(null, errors);
        }
    }

    /**
     * Class to hold the result of parsing a CSV file
     */
    public static class ParseResult {
        private final List<Student> students;
        private final List<String> errors;

        public ParseResult(List<Student> students, List<String> errors) {
            this.students = students;
            this.errors = errors;
        }

        public List<Student> getStudents() {
            return students;
        }

        public List<String> getErrors() {
            return errors;
        }

        public boolean hasErrors() {
            return errors != null && !errors.isEmpty();
        }

        public boolean hasStudents() {
            return students != null && !students.isEmpty();
        }
    }
} 
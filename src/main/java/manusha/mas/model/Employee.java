package manusha.mas.model;

import java.time.LocalDate;

public class Employee {
    private String fullName;
    private String epfNumber;
    private String nationalId;
    private String section;
    private String lineNumber;
    private String designation;
    private String address;
    private LocalDate dob;
    private String gender;
    private String maritalStatus;
    private String educationLevel;
    private String pastExperience;

    // Constructor
    public Employee(String fullName, String epfNumber, String nationalId, String section, String lineNumber,
                    String designation, String address, LocalDate dob, String gender, String maritalStatus,
                    String educationLevel, String pastExperience) {
        this.fullName = fullName;
        this.epfNumber = epfNumber;
        this.nationalId = nationalId;
        this.section = section;
        this.lineNumber = lineNumber;
        this.designation = designation;
        this.address = address;
        this.dob = dob;
        this.gender = gender;
        this.maritalStatus = maritalStatus;
        this.educationLevel = educationLevel;
        this.pastExperience = pastExperience;
    }

    // Getters and setters for each field
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEpfNumber() { return epfNumber; }
    public void setEpfNumber(String epfNumber) { this.epfNumber = epfNumber; }

    public String getNationalId() { return nationalId; }
    public void setNationalId(String nationalId) { this.nationalId = nationalId; }

    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }

    public String getLineNumber() { return lineNumber; }
    public void setLineNumber(String lineNumber) { this.lineNumber = lineNumber; }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getMaritalStatus() { return maritalStatus; }
    public void setMaritalStatus(String maritalStatus) { this.maritalStatus = maritalStatus; }

    public String getEducationLevel() { return educationLevel; }
    public void setEducationLevel(String educationLevel) { this.educationLevel = educationLevel; }

    public String getPastExperience() { return pastExperience; }
    public void setPastExperience(String pastExperience) { this.pastExperience = pastExperience; }
}

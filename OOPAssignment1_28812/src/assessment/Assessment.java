package com.auca.assessment;

import com.auca.enrollment.Enrollment;
import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;

import java.time.LocalDate;

public class Assessment implements Identifiable, Registrable {

    private final String assessmentId;
    private Enrollment enrollment;
    private String assessmentType;
    private double maximumScore;
    private double obtainedScore;
    private LocalDate assessmentDate;

    public Assessment(String assessmentId, Enrollment enrollment,
            String assessmentType, double maximumScore,
            double obtainedScore, LocalDate assessmentDate) {
        this.assessmentId = assessmentId;
        this.enrollment = enrollment;
        this.assessmentType = assessmentType;
        this.maximumScore = maximumScore;
        this.obtainedScore = obtainedScore;
        this.assessmentDate = assessmentDate;
    }

    @Override
    public String getId() {
        return assessmentId;
    }

    @Override
    public String getDisplayName() {
        return assessmentType;
    }

    @Override
    public boolean isActive() {
        return enrollment != null && enrollment.isActive();
    }

    public Enrollment getEnrollment() {
        return enrollment;
    }

    public void setEnrollment(Enrollment enrollment) {
        this.enrollment = enrollment;
    }

    public String getAssessmentType() {
        return assessmentType;
    }

    public void setAssessmentType(String assessmentType) {
        this.assessmentType = assessmentType;
    }

    public double getMaximumScore() {
        return maximumScore;
    }

    public void setMaximumScore(double maximumScore) {
        this.maximumScore = maximumScore;
    }

    public double getObtainedScore() {
        return obtainedScore;
    }

    public void setObtainedScore(double obtainedScore) {
        this.obtainedScore = obtainedScore;
    }

    public LocalDate getAssessmentDate() {
        return assessmentDate;
    }

    public void setAssessmentDate(LocalDate assessmentDate) {
        this.assessmentDate = assessmentDate;
    }

    @Override
    public void validate() throws ValidationException {
        if (assessmentId == null
                || assessmentId.trim().length() < MIN_ID_LENGTH
                || assessmentId.trim().length() > MAX_ID_LENGTH) {
            throw new ValidationException("Assessment ID must have 4 to 30 characters.");
        }

        if (enrollment == null || !enrollment.isActive()) {
            throw new ValidationException("Select an active enrollment.");
        }

        if (!"COURSEWORK".equals(assessmentType)
                && !"MIDTERM".equals(assessmentType)
                && !"FINAL EXAM".equals(assessmentType)) {
            throw new ValidationException("Choose COURSEWORK, MIDTERM, or FINAL EXAM.");
        }

        if (!Double.isFinite(maximumScore) || maximumScore <= 0) {
            throw new ValidationException("Maximum score must be a finite number greater than zero.");
        }

        if (!Double.isFinite(obtainedScore)
                || obtainedScore < 0 || obtainedScore > maximumScore) {
            throw new ValidationException("Obtained score must be between zero and the maximum score.");
        }

        if (assessmentDate == null) {
            throw new ValidationException("Assessment date is required.");
        }
    }

    @Override
    public void printDetails() {
        System.out.println(this);
    }

    @Override
    public String getRegistrationSummary() {
        return assessmentId + " - " + assessmentType
                + " - " + obtainedScore + "/" + maximumScore;
    }

    @Override
    public String toString() {
        String enrollmentLabel = enrollment == null
                ? "Unassigned"
                : enrollment.getRegistrationSummary();

        return "Assessment ID: " + assessmentId
                + "\nEnrollment: " + enrollmentLabel
                + "\nAssessment type: " + assessmentType
                + "\nMaximum score: " + maximumScore
                + "\nObtained score: " + obtainedScore
                + "\nAssessment date: " + assessmentDate;
    }
}
package manusha.mas.model;

import javafx.beans.property.*;
import java.time.LocalDate;

public class RequireDetails {

    private final StringProperty ieepf;
    private final StringProperty operation;
    private final StringProperty name;
    private final StringProperty section;
    private final ObjectProperty<LocalDate> reqDate;

    // Constructor
    public RequireDetails(String ieepf, String operation, String name, String section, LocalDate reqDate) {
        this.ieepf = new SimpleStringProperty(ieepf);
        this.operation = new SimpleStringProperty(operation);
        this.name = new SimpleStringProperty(name);
        this.section = new SimpleStringProperty(section);
        this.reqDate = new SimpleObjectProperty<>(reqDate);
    }

    // IEEPF
    public String getIeepf() {
        return ieepf.get();
    }

    public void setIeepf(String ieepf) {
        this.ieepf.set(ieepf);
    }

    public StringProperty ieepfProperty() {
        return ieepf;
    }

    // Operation
    public String getOperation() {
        return operation.get();
    }

    public void setOperation(String operation) {
        this.operation.set(operation);
    }

    public StringProperty operationProperty() {
        return operation;
    }

    // Name
    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public StringProperty nameProperty() {
        return name;
    }

    // Section
    public String getSection() {
        return section.get();
    }

    public void setSection(String section) {
        this.section.set(section);
    }

    public StringProperty sectionProperty() {
        return section;
    }

    // Request Date
    public LocalDate getReqDate() {
        return reqDate.get();
    }

    public void setReqDate(LocalDate reqDate) {
        this.reqDate.set(reqDate);
    }

    public ObjectProperty<LocalDate> reqDateProperty() {
        return reqDate;
    }

    // Override toString for debugging purposes
    @Override
    public String toString() {
        return "RequireDetails{" +
                "ieepf='" + getIeepf() + '\'' +
                ", operation='" + getOperation() + '\'' +
                ", name='" + getName() + '\'' +
                ", section='" + getSection() + '\'' +
                ", reqDate=" + getReqDate() +
                '}';
    }
}

package manusha.mas.model;

import javafx.beans.property.*;
import java.time.LocalDate;

public class RequestDetails {

    private final IntegerProperty id;
    private final StringProperty ieepf;
    private final StringProperty ieName;
    private final StringProperty ieShift;
    private final StringProperty ieSection;
    private final StringProperty moduleNo;
    private final StringProperty customer;
    private final StringProperty operation;
    private final ObjectProperty<LocalDate> reqDate;
    private final IntegerProperty count;

    public RequestDetails(int id, String ieepf, String ieName, String ieShift, String ieSection,
                          String moduleNo, String customer, String operation, LocalDate reqDate, int count) {
        this.id = new SimpleIntegerProperty(id);
        this.ieepf = new SimpleStringProperty(ieepf);
        this.ieName = new SimpleStringProperty(ieName);
        this.ieShift = new SimpleStringProperty(ieShift);
        this.ieSection = new SimpleStringProperty(ieSection);
        this.moduleNo = new SimpleStringProperty(moduleNo);
        this.customer = new SimpleStringProperty(customer);
        this.operation = new SimpleStringProperty(operation);
        this.reqDate = new SimpleObjectProperty<>(reqDate);
        this.count = new SimpleIntegerProperty(count);
    }

    // Getters and setters
    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public String getIeepf() {
        return ieepf.get();
    }

    public void setIeepf(String ieepf) {
        this.ieepf.set(ieepf);
    }

    public StringProperty ieepfProperty() {
        return ieepf;
    }

    public String getIeName() {
        return ieName.get();
    }

    public void setIeName(String ieName) {
        this.ieName.set(ieName);
    }

    public StringProperty ieNameProperty() {
        return ieName;
    }

    public String getIeShift() {
        return ieShift.get();
    }

    public void setIeShift(String ieShift) {
        this.ieShift.set(ieShift);
    }

    public StringProperty ieShiftProperty() {
        return ieShift;
    }

    public String getIeSection() {
        return ieSection.get();
    }

    public void setIeSection(String ieSection) {
        this.ieSection.set(ieSection);
    }

    public StringProperty ieSectionProperty() {
        return ieSection;
    }

    public String getModuleNo() {
        return moduleNo.get();
    }

    public void setModuleNo(String moduleNo) {
        this.moduleNo.set(moduleNo);
    }

    public StringProperty moduleNoProperty() {
        return moduleNo;
    }

    public String getCustomer() {
        return customer.get();
    }

    public void setCustomer(String customer) {
        this.customer.set(customer);
    }

    public StringProperty customerProperty() {
        return customer;
    }

    public String getOperation() {
        return operation.get();
    }

    public void setOperation(String operation) {
        this.operation.set(operation);
    }

    public StringProperty operationProperty() {
        return operation;
    }

    public LocalDate getReqDate() {
        return reqDate.get();
    }

    public void setReqDate(LocalDate reqDate) {
        this.reqDate.set(reqDate);
    }

    public ObjectProperty<LocalDate> reqDateProperty() {
        return reqDate;
    }

    public int getCount() {
        return count.get();
    }

    public void setCount(int count) {
        this.count.set(count);
    }

    public IntegerProperty countProperty() {
        return count;
    }
}

module com.tucn.pt_30424_pelle_andrei_assignment_2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;

    opens com.tucn to javafx.fxml;
    exports com.tucn;
    exports com.tucn.controller;
    opens com.tucn.controller to javafx.fxml;
    exports com.tucn.model;
    opens com.tucn.model to javafx.fxml;
    exports com.tucn.business_logic;
    opens com.tucn.business_logic to javafx.fxml;
}
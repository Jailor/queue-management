package com.tucn.controller;

import com.tucn.business_logic.SimulationManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class AppController {
    public static SimpleStringProperty property;
    @FXML
    private Button abortSimulationButton;
    @FXML
    private Button startSimulationButton;
    @FXML
    private TextArea textArea;
    @FXML
    private TextField simulationTime;
    @FXML
    private TextField numberOfClients;
    @FXML
    private TextField numberOfQueues;
    @FXML
    private TextField minArrivalTime;
    @FXML
    private TextField maxArrivalTime;
    @FXML
    private TextField minProcessingTime;
    @FXML
    private TextField maxProcessingTime;

    private Thread sim;
    private SimulationManager app;
    @FXML
    protected void onStartSimulation() {
        if(validateAllInput(true) != 0) return;
        startSimulationButton.setDisable(true);
        app = new SimulationManager(
                Integer.parseInt(simulationTime.getText()),
                Integer.parseInt(numberOfClients.getText()),
                Integer.parseInt(numberOfQueues.getText()),
                Integer.parseInt(minArrivalTime.getText()),
                Integer.parseInt(maxArrivalTime.getText()),
                Integer.parseInt(minProcessingTime.getText()),
                Integer.parseInt(maxProcessingTime.getText())
        );
        sim = new Thread(app);
        sim.start();
    }
    @FXML
    protected void onAbortSimulation()
    {
        if(sim.isAlive()) sim.interrupt();
        abortSimulationButton.setDisable(true);
    }
    @FXML
    protected void onResetSimulation() {
        if(sim.isAlive())
        {
            sim.stop();
            SimulationManager.stopQueues(app.getScheduler());
            property.setValue("Do not reset an already running simulation as this causes an unsafe exit!\n" +
                    "Use the abort simulation button first!");
        }
        else
        {
            property.setValue("");
        }
        abortSimulationButton.setDisable(false);
        startSimulationButton.setDisable(false);
    }

    public void initialize()
    {
        property = new SimpleStringProperty();
        textArea.textProperty().bind(property);
        //AUTO-SCROLL ON UPDATE
        property.addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                textArea.setScrollTop(Double.MAX_VALUE);
            }
        });
    }

    public int validateAllInput(boolean validate)
    {
        int retVal = validateInteger();
        if(retVal!=0)
        {
            if(validate) displayAlert(retVal,0);
            return retVal;
        }
        retVal = validateSign();
        if(retVal != 0)
        {
            if(validate) displayAlert(retVal, 1);
            return retVal;
        }
        retVal = validateOrder();
        if(retVal != 0)
        {
            if(validate) displayAlert(retVal, 2);
            return retVal;
        }
        return 0;
    }
    public int validateInteger()
    {
        if(!checkTextField(simulationTime)) return 1;
        if(!checkTextField(numberOfClients)) return 2;
        if(!checkTextField(numberOfQueues)) return 3;
        if(!checkTextField(minArrivalTime)) return 4;
        if(!checkTextField(maxArrivalTime)) return 5;
        if(!checkTextField(minProcessingTime)) return 6;
        if(!checkTextField(maxProcessingTime)) return 7;
        return 0;
    }

    public boolean checkTextField(TextField candidate)
    {
        try {
            Integer.parseInt(candidate.getText());
        }
        catch (Exception ex)
        {
            return false;
        }
        return true;
    }

    public void displayAlert(int errCode, int cause)
    {
        Alert alert =new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Bad input!");
        if(cause == 0)
        {
            switch (errCode) {
                case 1 -> alert.setContentText("Not a valid number on simulation time!");
                case 2 -> alert.setContentText("Not a valid number on number of clients!");
                case 3 -> alert.setContentText("Not a valid number on number of queues!");
                case 4 -> alert.setContentText("Not a valid number on min arrival time!");
                case 5 -> alert.setContentText("Not a valid number on max arrival time!");
                case 6 -> alert.setContentText("Not a valid number on min processing time!");
                case 7 -> alert.setContentText("Not a valid number on max processing time!");
                default -> alert.setContentText("Not a valid number!");
            }
        }
        else if(cause == 1)
        {
            switch (errCode) {
                case 1 -> alert.setContentText("Negative numbers not allowed on simulation time!");
                case 2 -> alert.setContentText("Negative numbers not allowed on number of clients!");
                case 3 -> alert.setContentText("Negative numbers not allowed on number of queues!");
                case 4 -> alert.setContentText("Negative numbers not allowed on min arrival time!");
                case 5 -> alert.setContentText("Negative numbers not allowed on max arrival time!");
                case 6 -> alert.setContentText("Negative numbers not allowed on min processing time!");
                case 7 -> alert.setContentText("Negative numbers not allowed on max processing time!");
                default -> alert.setContentText("Negative numbers not allowed!");
            }
        }
        else if(cause == 2)
        {
            switch (errCode) {
                case 1 -> alert.setContentText("Min arrival time cannot be larger than max arrival time!");
                case 2 -> alert.setContentText("Min processing time cannot be larger than max processing time!");
                case 3 -> alert.setContentText("Cannot have zero queues!");
                default -> alert.setContentText("Undefined error!");
            }
        }
        alert.show();
    }

    public int validateSign()
    {
        int[] arr = new int[8];
        arr[1] = Integer.parseInt(simulationTime.getText());
        arr[2] = Integer.parseInt(numberOfClients.getText());
        arr[3] = Integer.parseInt(numberOfQueues.getText());
        arr[4] = Integer.parseInt(minArrivalTime.getText());
        arr[5] = Integer.parseInt(maxArrivalTime.getText());
        arr[6] = Integer.parseInt(minProcessingTime.getText());
        arr[7] = Integer.parseInt(maxProcessingTime.getText());
        for (int i=1; i<=7; i++)
        {
            if(arr[i] < 0) return i;
        }
        return 0;
    }

    public int validateOrder()
    {
        int[] arr = new int[8];
        arr[1] = Integer.parseInt(simulationTime.getText());
        arr[2] = Integer.parseInt(numberOfClients.getText());
        arr[3] = Integer.parseInt(numberOfQueues.getText());
        arr[4] = Integer.parseInt(minArrivalTime.getText());
        arr[5] = Integer.parseInt(maxArrivalTime.getText());
        arr[6] = Integer.parseInt(minProcessingTime.getText());
        arr[7] = Integer.parseInt(maxProcessingTime.getText());
        if(arr[4] > arr[5])
        {
            return 1;
        }
        if(arr[6] > arr[7])
        {
            return 2;
        }
        if(arr[3] == 0) return 3;
        return 0;
    }
}
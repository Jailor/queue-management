package com.tucn.business_logic;

import com.tucn.controller.AppController;
import com.tucn.model.Client;
import com.tucn.model.Server;
import javafx.application.Platform;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class SimulationManager implements Runnable{
    public static final int GLOBAL_TIME = 500;
    private final Scheduler scheduler;
    private final int timeLimit;

    private final int minServiceTime;
    private final int maxServiceTime;


    private final int minArrivalTime;
    private final int maxArrivalTime;

    private final int clientCount;
    private final List<Client> generatedClients;

    private double averageServiceTime;
    private int peakHour = -1;
    private double peakHourValue = -1;

    private boolean toEnd = false;

    private StringBuilder output;

    public SimulationManager(int timeLimit,int clientCount,
                             int serverCount,
                              int minArrivalTime,
                            int maxArrivalTime,
                             int minServiceTime,
                             int maxServiceTime)
    {
        this.timeLimit = timeLimit;
        this.clientCount = clientCount;
        this.minArrivalTime = minArrivalTime;
        this.maxArrivalTime = maxArrivalTime;
        this.minServiceTime = minServiceTime;
        this.maxServiceTime = maxServiceTime;

        generatedClients = new Vector<>();
        generateNRandomTasks();
        scheduler = new Scheduler(serverCount);
        output = new StringBuilder();
    }

    private void generateNRandomTasks()
    {
        averageServiceTime = 0;
        for(int i=1; i<=clientCount; i++)
        {
            Random rand = new Random();
            Client t = new Client(i,rand.nextInt(minArrivalTime,maxArrivalTime + 1),
                    rand.nextInt(minServiceTime, maxServiceTime + 1));
            generatedClients.add(t);
            averageServiceTime += t.getServiceTime();
        }
        averageServiceTime = averageServiceTime/clientCount;
        Collections.sort(generatedClients);
    }

    @Override
    public void run() {
        int currentTime = 0;
        while (currentTime <= timeLimit)
        {
            List<Client> toRemove = new ArrayList<>();
            for(Client t: generatedClients)
            {
                if(t.getArrivalTime() == currentTime)
                {
                    toRemove.add(t);
                    scheduler.dispatchTask(t);
                }
            }
            generatedClients.removeAll(toRemove);

            appendClientSituation(currentTime);
            currentTime++;

            appendQueueSituation(currentTime);
            printToGui();

            //SLEEP
             try {
                    Thread.sleep(GLOBAL_TIME);
                } catch (InterruptedException e) {
                   output.append("Simulation manager was interrupted and exiting!\n");
                   printToGui();
                   break;
                }
            //CHECK IF END OF SIMULATION
             if(toEnd)
             {
                output.append("Simulation is over! Exiting early...\n");
                printToGui();
                break;
             }
             else if(generatedClients.size() == 0 )
             {
                int remainingTasks = 0;
                for(Server server: scheduler.getServers())
                {
                    if(server.getTasks().size() != 0)
                    {
                        remainingTasks += server.getTasks().size();
                    }
                }
                if(remainingTasks ==0)
                {
                    toEnd = true;
                }
             }
        }
        appendStatistics(currentTime);
        printToGui();
        stopQueues(scheduler);
        printToFile();
    }

    private void printToGui()
    {
        Platform.runLater(() -> AppController.property.setValue(output.toString()));
    }
    private void printToFile()
    {
        try {
            FileWriter myWriter = new FileWriter("log.txt");
            myWriter.write(output.toString());
            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
    public void appendClientSituation(int currentTime)
    {
        output.append( "At time " + currentTime +" :\n");
        if(generatedClients.size()!=0)
        {
            output.append("Current tasks to dispatch:\n");
            for(Client t: generatedClients)
            {
                output.append(t.toString());
                output.append("  ");
            }
            output.append("\n");
        }
        else
        {
            output.append("There are no more tasks to dispatch!\n");
        }
    }

    public void appendQueueSituation(int currentTime)
    {
        output.append("Queue situation:\n");
        double aux_sum = 0;
        for(int i=1; i<= scheduler.getServers().size();i++)
        {
            Server candidate = null;
            for (Server server: scheduler.getServers())
            {
                if(server.getId() == i)
                {
                    candidate = server;
                    break;
                }
            }
            if(candidate!= null)
            {
                output.append(candidate.toString() + "\n");
                aux_sum += candidate.getWaitingPeriod().get();
            }
        }
        aux_sum = aux_sum/scheduler.getServers().size();
        if(aux_sum > peakHourValue)
        {
            peakHourValue = aux_sum;
            peakHour = currentTime - 1;
        }
        output.append("\n");
    }

    public void appendStatistics(int currentTime)
    {
        //AVERAGE WAITING TIME CALCULATION
        double averageWaitingTimeAll = 0;
        for(int i=1; i<= scheduler.getServers().size();i++)
        {
            Server candidate = null;
            for (Server server: scheduler.getServers())
            {
                if(server.getId() == i)
                {
                    candidate = server;
                    break;
                }
            }
            if(candidate!= null)
            {
                averageWaitingTimeAll += candidate.getTotalWaitingTime()/(currentTime);
                output.append("Queue "+ candidate.getId() + " has an average waiting time of " +
                        String.format("%.3f",
                                candidate.getTotalWaitingTime()/(currentTime))+"\n");
            }
        }
        output.append("\n");
        averageWaitingTimeAll =averageWaitingTimeAll/ scheduler.getServers().size();
        output.append("Average waiting time for the queues in total is "
                + String.format("%.3f",averageWaitingTimeAll)+"\n");
        output.append("\n");
        //AVERAGE SERVICE TIME
        output.append("Average service time for a task is: " +
                String.format("%.3f",averageServiceTime)+"\n");
        //PEAK HOUR DISPLAY
        output.append("Peak hour for the simulation is time " + peakHour +
                " with an average waiting time of " + String.format("%.3f",peakHourValue)
                +" between all the queues\n");
    }
    public static void stopQueues(Scheduler scheduler)
    {
        for(Server s: scheduler.getServers())
        {
            s.stopQueue();
        }
    }
    //GETTERS AND SETTERS

    public Scheduler getScheduler() {
        return scheduler;
    }

}

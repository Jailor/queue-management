package com.tucn.model;

import com.tucn.business_logic.SimulationManager;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Server implements Runnable,Comparable<Server>{
    private boolean running = true;
    private final int id;
    private final BlockingQueue<Client> clients;
    private final AtomicInteger waitingPeriod;
    private double totalWaitingTime;

    public Server(int id)
    {
        this.id =id;
        clients = new LinkedBlockingQueue<>();
        waitingPeriod = new AtomicInteger(0);
        totalWaitingTime = 0;
    }

    public void  addTask(Client client)
    {
        synchronized (waitingPeriod)
        {
            clients.add(client);
            waitingPeriod.getAndAdd(client.getServiceTime());
        }
    }
    @Override
    public void run()
    {
        while (running)
        {
            if(!clients.isEmpty())
            {
                try {
                    Client t  = clients.peek();
                    while (t.getServiceTime() > 0) {
                        Thread.sleep(SimulationManager.GLOBAL_TIME);
                        synchronized (waitingPeriod)
                        {
                            totalWaitingTime += waitingPeriod.get();
                            t.setServiceTime(t.getServiceTime()-1);
                            waitingPeriod.getAndAdd(-1);
                        }
                    }
                    clients.take();
                } catch (InterruptedException exception) {
                    break;
                }
            }
        }
    }

    @Override
    public int compareTo(Server o) {
        if(this.waitingPeriod.get() < o.waitingPeriod.get()) return -1;
        if(this.waitingPeriod.get() > o.waitingPeriod.get()) return 1;
        return 0;
    }

    @Override
    public String toString() {
        if(clients.size()!= 0)
        {
            StringBuilder s = new StringBuilder();
            s.append("Queue "+id+" {");
            for(Client t: clients)
            {
                if(t.getServiceTime() !=0) s.append(t.toString());
            }
            s.append( ", waitingPeriod=" + waitingPeriod +
                    '}');
            return s.toString();
        }
        return "Queue "+ id + " is closed!";

    }
    public void stopQueue()
    {
        running=false;
    }
    //setters and getters

    public BlockingQueue<Client> getTasks() {
        return clients;
    }

    public AtomicInteger getWaitingPeriod() {
        return waitingPeriod;
    }

    public int getId() {
        return id;
    }

    public double getTotalWaitingTime() {
        return totalWaitingTime;
    }
}

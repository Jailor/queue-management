package com.tucn.model;

public class Client implements Comparable<Client>{
    private final int id;
    private final int arrivalTime;
    private int serviceTime;

    public Client(int id, int arrivalTime, int serviceTime) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.serviceTime = serviceTime;
    }

    @Override
    public int compareTo(Client o) {
        if(this.arrivalTime < o.arrivalTime) return -1;
        if(this.arrivalTime > o.arrivalTime) return 1;
        return 0;
    }

    @Override
    public String toString() {
        return "(" + id +","+ arrivalTime +","+ serviceTime +
                ')';
    }
    //Getters and setters

    public int getArrivalTime() {
        return arrivalTime;
    }


    public int getServiceTime() {
        return serviceTime;
    }

    public void setServiceTime(int serviceTime) {
        this.serviceTime = serviceTime;
    }
}

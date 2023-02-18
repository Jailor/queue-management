package com.tucn.business_logic;

import com.tucn.model.Client;
import com.tucn.model.Server;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

public class Scheduler {
    private List<Server> servers;
    private int maxNoServers;

    public Scheduler(int maxNoServers) {
        this.maxNoServers = maxNoServers;
        servers = new Vector<>();
        for(int i=1; i<=maxNoServers; i++)
        {
            Server s = new Server(i);
            servers.add(s);
            Thread t = new Thread(s);
            t.start();
        }
    }

    public void  dispatchTask(Client t)
    {
        Collections.sort(servers);
        Server min = servers.get(0);
        min.addTask(t);
    }
    //GETTERS AND SETTERS
    public List<Server> getServers() {
        return servers;
    }

}

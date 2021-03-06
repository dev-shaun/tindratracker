package org.vaadin.tindra.tcpserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vaadin.tindra.backend.AppService;
import org.vaadin.tindra.backend.UpdateRepository;
import org.vaadin.tindra.domain.Update;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by se on 19/06/14.
 */
@Service
public class Server {

    @Autowired
    UpdateRepository repo;
    
    @Autowired
    AppService appService;

    private final int portNumber;
    private boolean running = false;

    public Server() {
        this(50123);
    }

    public Server(int portNumber) {
        this.portNumber = portNumber;

    }

    public boolean isRunning() {
        return running;
    }

    @PostConstruct
    public void start() {

        running = true;
        
        List<Update> invalidNewerThanNow = repo.findByTimestampGreaterThanOrderByTimestampDesc( new Date());
        repo.deleteInBatch(invalidNewerThanNow);

            new Thread() {

                @Override
                public void run() {
                    ServerSocket serverSocket;
                    try {
                        serverSocket = new ServerSocket(portNumber);
                        while (isRunning()) {
                            new ServerThread(Server.this, serverSocket.accept()).start();
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(Server.class.getName()).
                                log(Level.SEVERE, null, ex);
                    }
                }
            }.start();
    }

    @PreDestroy
    public void stop() {
        this.running = false;
    }

    static public void main(String[] args) {
        new Server().start();

    }

    void persist(Update update) {
        Update saved = repo.save(update);
        appService.setLastUpdate(saved);
    }

}

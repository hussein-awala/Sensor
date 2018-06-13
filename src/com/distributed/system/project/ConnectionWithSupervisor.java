package com.distributed.system.project;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

/*
 * @author:Hussein Awala
 * this thread is responsible to establish the connection with the supervisor
 * then wait the connection with the data server to tell the supervisor that the sensor is ready
 */
public class ConnectionWithSupervisor extends Thread{
    protected static final String hostSupervisor="SUPERVISOR";
    protected static final int portSupervisor=1111;
    protected static final int timeBetweenEverySendOfDescriptor=5;
    protected BasicSensor sensor;
    protected Socket socket;
    protected ObjectOutputStream objectOutputStream;

    public ConnectionWithSupervisor(BasicSensor sensor) {
        this.sensor=sensor;
    }

    private final void registerAndStartWork() throws IOException, InterruptedException {
        //establish the connection
        establishConnection();
        //send the reference of sensor to the supervisor
        sendReferenceToSupervisor();
        //wait for establish the connection with the data server
        waitTheReadyCommand();
        //send the descriptors
        sendDescriptorEveryTau();
    }

    //this method is responsible for open the connection with the supervisor
    public void establishConnection() throws IOException {
        socket=new Socket(hostSupervisor,portSupervisor);
    }

    //this method send the reference of the sensor to the supervisor
    public void sendReferenceToSupervisor() throws IOException {
        objectOutputStream=new ObjectOutputStream(socket.getOutputStream());
        objectOutputStream.writeObject(sensor);
    }

    //this method is to enforce the thread to sleep while the sensor is not ready
    public synchronized void waitTheReadyCommand() throws InterruptedException {
        this.wait();
    }

    //send ready to Supervisor and notify the thread to continue to next step
    public synchronized void changeStateToReady() throws IOException {
        objectOutputStream.writeBoolean(true);
        this.notify();
    }

    //this method is to send the descriptor while the sensor is alive
    public void sendDescriptorEveryTau() throws InterruptedException, IOException {
        while (true){
            sleep(timeBetweenEverySendOfDescriptor*1000);
            objectOutputStream.writeObject(sensor.getDescriptor());
        }
    }
}

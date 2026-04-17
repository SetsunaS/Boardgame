package setsuna.boardgame.utils.network;

import setsuna.boardgame.utils.Constants;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class NetworkManager{
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private volatile boolean isRunning=false;
    private BlockingQueue<String> messageQueue=new LinkedBlockingQueue<>();

    public void connectToServer() throws IOException{
        isRunning=true;
        this.socket=new Socket(Constants.SERVER_HOST, Constants.SERVER_PORT);
        this.in=new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out=new PrintWriter(socket.getOutputStream(), true);

        listeningMessageFromServer();
    }

    private void listeningMessageFromServer(){
        new Thread(() -> {
            try{
                String message;
                while(isRunning && (message=in.readLine())!=null){
                    messageQueue.put(message);
                }
            }
            catch(IOException e){
                if(isRunning){
                    e.printStackTrace();
                    System.out.println("Error while receiving message.");
                }
            }
            catch(InterruptedException e){
                System.out.println("Error while putting received message in the queue.");
            }
        }).start();
    }

    public void sendMessageToServer(String message){
        System.out.println("sent message to server : "+message);
        if(out!=null) out.println(message);
    }

    public String receiveMessageFromServer() throws InterruptedException{
        return messageQueue.take();
    }

    public boolean getIsRunning(){
        return isRunning;
    }

    public void closeConnection(){
        try{
            isRunning=false;
            if(out!=null) out.close();
            if(in!=null) in.close();
            if(socket!=null) socket.close();
        }
        catch(IOException e){
            e.printStackTrace();
            System.out.println("Error while closing connection.");
        }
    }
}
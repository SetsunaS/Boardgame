package setsuna.boardgame.utils.network;

import setsuna.boardgame.utils.Constants;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class NetworkManager{
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public void connectToServer() throws IOException{
        this.socket=new Socket(Constants.SERVER_HOST, Constants.SERVER_PORT);
        this.in=new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out=new PrintWriter(socket.getOutputStream(), true);
    }

    public void sendMessageToServer(String message){
        if(out!=null) out.println(message);
    }

    public String receiveMessageFromServer() throws IOException{
        if(in!=null){
            String res=in.readLine();
            System.out.println(res);
            return res;
        }
        return null;
    }

    public void closeConnection(){
        try{
            if(socket!=null) socket.close();
            if(in!=null) in.close();
            if(out!=null) out.close();
        }
        catch(IOException e){
            e.printStackTrace();
            System.out.println("Error while closing connection.");
        }
    }
}
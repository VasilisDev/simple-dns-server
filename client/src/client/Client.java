/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

/**
 *
 * @author vasilis
 */
import java.net.*;
import java.io.*;
@SuppressWarnings("ALL")
public class Client {
    public static void main(String args[]) throws Exception {
        byte[] rbuf = new byte[1024], sbuf = new byte[1024];
        BufferedReader fromUser =
                new BufferedReader(new InputStreamReader(System.in));
        DatagramSocket socket = new DatagramSocket();
        System.out.print("Give the host:");
        String host = fromUser.readLine();
        InetAddress addr = InetAddress.getByName(String.valueOf(host));
        while(true) {
            //get an integer from user
            System.out.print("Enter a domain name: ");
            String data = fromUser.readLine();
            sbuf = data.getBytes();
            DatagramPacket spkt = new DatagramPacket(sbuf, sbuf.length,
                    addr, 5000);
            //send it to server
            socket.send(spkt);
            System.out.println("Sent to server: " + data);
            if(data.equals("-1")) break;
            DatagramPacket rpkt = new DatagramPacket(rbuf, rbuf.length);
            //retrieve result
            socket.receive(rpkt);
            data = new String(rpkt.getData(), 0, rpkt.getLength());
            System.out.println("Received from server: " + data);
        }
        //close the socket
        socket.close();
    }
}
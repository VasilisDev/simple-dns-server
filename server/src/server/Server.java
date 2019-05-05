/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

/**
 *
 * @author vasilis
 */

import java.net.*;
import java.io.*;
import java.util.*;


public class Server {
    public static void main(String args[]) throws Exception {

        //create a server socket at port 5000
        DatagramSocket socket = new DatagramSocket(5000);
        System.out.println("Server ready");
        

        while (true) {

            byte[] rbuf = new byte[1024];
            DatagramPacket rpkt = new DatagramPacket(rbuf, rbuf.length);
            //receive a packet from client
            socket.receive(rpkt);
            //hand over this packet to Handler
            new Handler(rpkt, socket);
        }
    }
}
class Handler implements Runnable {

    DatagramSocket socket;
    DatagramPacket pkt;
    Handler(DatagramPacket pkt, DatagramSocket socket) {
        this.pkt = pkt;
        this.socket = socket;
        new Thread(this).start();
        System.out.println("A thread created");
    }
    public void run() {
        Hashtable<String, String> table = new Hashtable<>();
        String[] parser;
        String fileName =  "resources/host.txt";
        byte[] sbuf = new byte[1024];
        //extract data and client information from this packet
        String data = new String(pkt.getData(), 0, pkt.getLength());
        InetAddress addr = pkt.getAddress();
        int port = pkt.getPort();
        String domain = String.valueOf(data);
        System.out.println("Received: " + domain + " from " + addr + ":" + port);
        BufferedWriter wr = null;
        BufferedReader br=null;

        try {
            String line;
            br = new BufferedReader(new FileReader(fileName));
            wr = new BufferedWriter(new FileWriter(fileName,true));
            while ((line = br.readLine()) != null) {
                parser = line.split(" ");
                table.put(parser[0], parser[1]);
            }
            System.out.println(table);
            if (table.containsKey(domain)) {
                sbuf = String.valueOf(table.get(domain)).getBytes();
            } else {
                     try {
                    InetAddress domainToIp = InetAddress.getByName(String.valueOf(domain));
                    parser = domainToIp.toString().split("/");
                    table.put(parser[0], parser[1]);
                    StringBuilder sb=new StringBuilder(parser[0]+" "+parser[1]);
                    wr.write(String.valueOf(sb));
                    wr.write("\n");
                    sbuf = String.valueOf(parser[1]).getBytes();
                }
                catch (UnknownHostException e){
                    sbuf = String.valueOf("Not found").getBytes();
                }
            }
            DatagramPacket spkt = new DatagramPacket(sbuf, sbuf.length,
                    addr, port);
            //send result to the client
            socket.send(spkt);
            //  System.out.println("Sent: " + domainToIp);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br!=null)
                br.close();
                if(wr!=null)
                    wr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

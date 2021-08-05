package com.cm.cloud.study.contants;


import com.cm.cloud.study.thread.DefaultThreadPool;
import com.cm.cloud.study.thread.ThreadPool;
import lombok.SneakyThrows;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class StudyThreadPoolHttp {

    static ThreadPool<StudyThreadPoolHttpHandler> pool = new DefaultThreadPool<>();

    static ServerSocket serverSocket;

    static int port = 8081;

    public static void setPort(int port){
        if (port > 0){
            StudyThreadPoolHttp.port = port;
        }
    }

    public static void main(String[] args) throws Exception {
        start();
    }

    public static void start() throws Exception {
        serverSocket = new ServerSocket(port);
        Socket socket = null;
        try {
            System.out.println("监听");
            synchronized (serverSocket) {
                while ((socket = serverSocket.accept()) != null) {
                    System.out.println("请求");
                    pool.execute(new StudyThreadPoolHttpHandler(socket));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            serverSocket.close();
        }
    }
     static class StudyThreadPoolHttpHandler implements Runnable{
         private Socket socket;
         public StudyThreadPoolHttpHandler(Socket socket){
             this.socket = socket;
         }
         @SneakyThrows
         @Override
         public void run() {
             PrintWriter out = null;
             OutputStream outputStream = socket.getOutputStream();
             OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
             BufferedWriter bufferedWriter =new BufferedWriter(outputStreamWriter);
             try {
                 String hah = "Hello World";
                 byte[] chars = hah.getBytes();
                 out = new PrintWriter(socket.getOutputStream());
                 out.println("HTTP/1.1 200 OK");
                 out.println("Server:Molly");
//                 out.println("Content-Type: application/json;charset=UTF-8");
                 out.println("Content-Type: text/html");
                 out.println("");
//                 bufferedWriter.write("HTTP/1.1 404\n");
                 //bufferedWriter.write("Content-Type:text/html;charset:utf-8\n");
                 bufferedWriter.write("\n");
                 bufferedWriter.write("<html>" +
                         "<head><title>hello</title></head>"+
                         "<body>Hello World</body></html>");
//                 socket.getOutputStream().write(chars,0,chars.length);
                 out.flush();
                 bufferedWriter.flush();
                 outputStreamWriter.flush();
                 outputStream.flush();
             } catch (IOException e) {
                 e.printStackTrace();
             }finally {
                 try {
                     out.close();
                     close(outputStream,outputStreamWriter,bufferedWriter,socket);
                 } catch (Exception e) {
                     e.printStackTrace();
                 }
             }
         }
     }

     private static void close(Closeable... closeables){
        if (closeables != null){
            for (Closeable closeable : closeables){
                try{
                    closeable.close();
                }catch (Exception e){

                }
            }
        }
     }
}

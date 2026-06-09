package fylo.service;

import fylo.utils.UploadUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class FileSharer {

    private final HashMap<Integer, String> availableFiles;

    public FileSharer() {
        this.availableFiles = new HashMap<>();
    }

    public int offerFile(String filePath){
        int port;
        while(true){
            port = UploadUtils.generateCode();
            if(!availableFiles.containsKey(port)){
                availableFiles.put(port, filePath);
                return port;
            }
        }
    }

    public void startFileServer(int port) throws IOException {
        String filePath = availableFiles.get(port);

        if(filePath == null){
            System.out.println("No file found for the given port: " + port);
            return;
        }

        try(ServerSocket serverSocket = new ServerSocket(port)){
            System.out.println("Serving file " + new File(filePath).getName() + " on port " + port);
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected: " + clientSocket.getInetAddress());
            new Thread(new FileTransferHandler(clientSocket, filePath)).start();
        }
    }

    private static class FileTransferHandler implements Runnable {

        private final Socket clientSocket;
        private final String filePath;

        public FileTransferHandler(Socket clientSocket, String filePath) {
            this.clientSocket = clientSocket;
            this.filePath = filePath;
        }

        @Override
        public void run() {
            try(FileInputStream fileInputStream = new FileInputStream(filePath)){
                OutputStream outStream = clientSocket.getOutputStream();
                String fileName = new File(filePath).getName();
                String header = "FileName: "+fileName+"\n";
                outStream.write(header.getBytes());

                byte[] buffer = new byte[4096];
                int bytesRead;
                while((bytesRead = fileInputStream.read(buffer)) != -1){
                    outStream.write(buffer, 0, bytesRead);
                }
                System.out.println("File transfer completed for " + fileName + " to " + clientSocket.getInetAddress());
            }catch(IOException e) {
                System.err.println("Error during file transfer: " + e.getMessage());
            }finally{
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    System.err.println("Error closing client socket: " + e.getMessage());
                }
            }
        }
    }
}

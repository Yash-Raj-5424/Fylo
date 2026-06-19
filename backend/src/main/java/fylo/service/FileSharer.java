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

    private final HashMap<String, FileEntry> availableFiles;

    public FileSharer() {
        this.availableFiles = new HashMap<>();
    }

    public String offerFile(String filePath){
        String code;
        int port;
        while(true){
            code = UploadUtils.generateToken();
            if(!availableFiles.containsKey(code)){
                port = UploadUtils.generateCode();
                availableFiles.put(code, new FileEntry(filePath, port));
                return code;
            }
        }
    }

    public void startFileServer(String code) throws IOException {
        FileEntry entry = availableFiles.get(code);

        if(entry == null){
            System.out.println("No file found for code: " + code);
            return;
        }

        try(ServerSocket serverSocket = new ServerSocket(entry.port)){
            System.out.println("Serving file " + new File(entry.filePath).getName() + " on port " + entry.port);
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected: " + clientSocket.getInetAddress());
            new Thread(new FileTransferHandler(clientSocket, entry.filePath)).start();
        }
    }

    public FileEntry getFileEntry(String code) {
        return availableFiles.get(code);
    }

    public static class FileEntry {
        public final String filePath;
        public final int port;

        public FileEntry(String filePath, int port) {
            this.filePath = filePath;
            this.port = port;
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

package fylo.controller.handler;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.Socket;

public class DownloadHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Headers headers = exchange.getResponseHeaders();
        headers.add("Access-Control-Allow-Origin", "*");

        if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            String response = "Method Not Allowed";
            exchange.sendResponseHeaders(405, response.getBytes().length);

            try (OutputStream outputStream = exchange.getResponseBody()) {
                outputStream.write(response.getBytes());
            }
            return;
        }

        String path = exchange.getRequestURI().getPath();
        String portStr = path.substring(path.lastIndexOf('/' + 1));

        try {

            int port = Integer.parseInt(portStr);

            try (Socket socket = new Socket("localhost", port)) {
                InputStream socketInput = socket.getInputStream();
                File tempFile = File.createTempFile("download_", ".tmp");
                String filename = "downloaded_file";

                try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;

                    ByteArrayOutputStream headerBaos = new ByteArrayOutputStream();
                    int b;

                    while ((b = socketInput.read()) != -1) {
                        if (b == '\n') break;
                        headerBaos.write(b);
                    }

                    String header = headerBaos.toString().trim();
                    if (header.startsWith("Filename: ")) {
                        filename = header.substring("Filename: ".length());
                    }

                    while ((bytesRead = socketInput.read(buffer)) != -1) {
                        fos.write(buffer, 0, bytesRead);
                    }
                }

                headers.add("Content-Disposition", "attachment; filename=\"" + filename + "\"");
                headers.add("Content-Type", "application/octet-stream");

                exchange.sendResponseHeaders(200, tempFile.length());

                try (OutputStream outStream = exchange.getResponseBody();
                     FileInputStream fis = new FileInputStream(tempFile)) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;

                    while ((bytesRead = fis.read(buffer)) != -1) {
                        outStream.write(buffer, 0, bytesRead);
                    }
                }

                tempFile.delete();

            } catch (Exception e) {
                System.err.println("Error while downloading the file from peer: " + e.getMessage());
                String res = "Error downloading file: " + e.getMessage();
                headers.add("Content-Type", "text/plain");
                exchange.sendResponseHeaders(500, res.getBytes().length);

                try (OutputStream outStream = exchange.getResponseBody()) {
                    outStream.write(res.getBytes());
                }
            }
        } catch (NumberFormatException e) {
            String response = "Bad Request: Invalid Port Number";
            exchange.sendResponseHeaders(400, response.getBytes().length);

            try (OutputStream outStream = exchange.getResponseBody()) {
                outStream.write(response.getBytes());
            }
        }
    }
}

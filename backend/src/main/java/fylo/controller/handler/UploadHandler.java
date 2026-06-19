package fylo.controller.handler;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import fylo.parser.MultipartParser;
import fylo.parser.ParseResult;
import fylo.service.FileSharer;

import java.io.*;
import java.util.UUID;

public class UploadHandler implements HttpHandler {

    private final FileSharer fileSharer;
    private final String uploadDir;

    public UploadHandler(FileSharer fileSharer, String uploadDir) {
        this.fileSharer = fileSharer;
        this.uploadDir = uploadDir;
    }


    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Headers headers = exchange.getResponseHeaders();
        headers.add("Access-Control-Allow-Origin", "*");

        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            String response = "Method Not Allowed";
            exchange.sendResponseHeaders(405, response.getBytes().length);

            try (OutputStream outStream = exchange.getResponseBody()) {
                outStream.write(response.getBytes());
            }
            return;
        }

        Headers requestHeaders = exchange.getRequestHeaders();
        String contentType = requestHeaders.getFirst("Content-Type");

        if (contentType == null || !contentType.startsWith("multipart/form-data")) {
            String response = "Bad Request: Content-Type must be multipart/form-data";
            exchange.sendResponseHeaders(400, response.getBytes().length);

            try (OutputStream outStream = exchange.getResponseBody()) {
                outStream.write(response.getBytes());
            }
            return;
        }

        try {
            String boundary = contentType.substring(contentType.indexOf("boundary=") + 9);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buf = new byte[4096];
            int n;
            InputStream is = exchange.getRequestBody();
            while ((n = is.read(buf)) != -1) {
                baos.write(buf, 0, n);
            }
            byte[] requestData = baos.toByteArray();

            MultipartParser parser = new MultipartParser(requestData, boundary);
            ParseResult result = parser.parse();

            if (result == null) {
                String response = "Bad Request: Could not parse file content";
                exchange.sendResponseHeaders(400, response.getBytes().length);

                try (OutputStream outStream = exchange.getResponseBody()) {
                    outStream.write(response.getBytes());
                }
                return;
            }

            String fileName = result.filename;
            if (fileName == null || fileName.trim().isEmpty()) {
                fileName = "unnamed_file";
            }
            String uniqueFileName = UUID.randomUUID() + "_" + new File(fileName).getName();
            String filePath = uploadDir + File.separator + uniqueFileName;

            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                fos.write(result.fileContent);
            }
            String code = fileSharer.offerFile(filePath);

            new Thread(() -> {
                try {
                    fileSharer.startFileServer(code);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).start();

            String jsonResponse = "{\"code\": \"" + code + "\"}";
            headers.add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, jsonResponse.getBytes().length);

            try (OutputStream outStream = exchange.getResponseBody()) {
                outStream.write(jsonResponse.getBytes());
            }

        } catch (Exception e) {
            System.err.println("Error processing the File Upload: " + e.getMessage());
            String response = "Server error: " + e.getMessage();
            exchange.sendResponseHeaders(500, response.getBytes().length);

            try (OutputStream outputStream = exchange.getResponseBody()) {
                outputStream.write(response.getBytes());
            }
        }
    }
}

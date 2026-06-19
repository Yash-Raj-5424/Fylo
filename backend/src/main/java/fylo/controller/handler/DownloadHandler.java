package fylo.controller.handler;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import fylo.service.FileSharer;

import java.io.*;
import java.net.Socket;

public class DownloadHandler implements HttpHandler {

    private final FileSharer fileSharer;

    public DownloadHandler(FileSharer fileSharer) {
        this.fileSharer = fileSharer;
    }

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
        boolean isView = path.startsWith("/view/");
        String code = path.substring(path.lastIndexOf('/') + 1);

        if (code.isEmpty()) {
            String response = "Bad Request: Missing code";
            exchange.sendResponseHeaders(400, response.getBytes().length);

            try (OutputStream outputStream = exchange.getResponseBody()) {
                outputStream.write(response.getBytes());
            }
            return;
        }

        FileSharer.FileEntry entry = fileSharer.getFileEntry(code);

        if (entry == null) {
            String response = "Unauthorized: Invalid or expired code";
            exchange.sendResponseHeaders(401, response.getBytes().length);

            try (OutputStream outputStream = exchange.getResponseBody()) {
                outputStream.write(response.getBytes());
            }
            return;
        }

        try (Socket socket = new Socket("localhost", entry.port)) {
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
                if (header.startsWith("FileName: ")) {
                    filename = header.substring("FileName: ".length());
                }

                while ((bytesRead = socketInput.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
            }

            if (isView) {
                headers.add("Content-Disposition", "inline; filename=\"" + filename + "\"");
                headers.add("Content-Type", getContentType(filename));
            } else {
                headers.add("Content-Disposition", "attachment; filename=\"" + filename + "\"");
                headers.add("Content-Type", "application/octet-stream");
            }

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
    }

    private String getContentType(String filename) {
        int dot = filename.lastIndexOf('.');
        if (dot < 0) return "application/octet-stream";
        String ext = filename.substring(dot + 1).toLowerCase();

        switch (ext) {
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "webp":
                return "image/webp";
            case "svg":
                return "image/svg+xml";
            case "bmp":
                return "image/bmp";
            case "ico":
                return "image/x-icon";
            case "pdf":
                return "application/pdf";
            case "mp4":
                return "video/mp4";
            case "webm":
                return "video/webm";
            case "avi":
                return "video/x-msvideo";
            case "mov":
                return "video/quicktime";
            case "mp3":
                return "audio/mpeg";
            case "wav":
                return "audio/wav";
            case "flac":
                return "audio/flac";
            case "m4a":
                return "audio/mp4";
            case "txt":
            case "csv":
            case "json":
            case "xml":
            case "html":
            case "htm":
            case "css":
            case "js":
            case "md":
            case "yaml":
            case "yml":
            case "toml":
            case "ini":
            case "cfg":
            case "log":
            case "java":
            case "py":
            case "ts":
            case "tsx":
            case "jsx":
            case "rb":
            case "go":
            case "rs":
            case "sh":
            case "bat":
                return "text/plain; charset=utf-8";
            default:
                return "application/octet-stream";
        }
    }
}

package fylo.controller;

import com.sun.net.httpserver.HttpServer;
import fylo.controller.handler.CORSHandler;
import fylo.controller.handler.DownloadHandler;
import fylo.controller.handler.UploadHandler;
import fylo.service.FileSharer;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileController {

    private final FileSharer fileSharer;
    private final HttpServer server;
    private final String uploadDir;
    private final ExecutorService executorService;

    public FileController(int port) throws IOException {
        this.fileSharer = new FileSharer();
        this.server = HttpServer.create(new InetSocketAddress(port), 0);
        System.out.println("HttpServer created successfully");
        String envDir = System.getenv("FYLO_UPLOAD_DIR");
        if (envDir != null && !envDir.isBlank()) {
            this.uploadDir = envDir;
        } else {
            this.uploadDir = new File(System.getProperty("java.io.tmpdir"), "fylo_uploads").getAbsolutePath();
        }
        System.out.println("Upload directory: " + uploadDir);
        this.executorService = Executors.newFixedThreadPool(10);

        File uploadDirectory = new File(uploadDir);
        if (!uploadDirectory.exists() && !uploadDirectory.mkdirs()) {
            throw new IOException("Failed to create upload directory: " + uploadDir);
        }

        server.createContext("/upload", new UploadHandler(fileSharer, uploadDir));
        server.createContext("/download", new DownloadHandler(fileSharer));
        server.createContext("/view", new DownloadHandler(fileSharer));
        server.createContext("/", new CORSHandler());

    }

    public void start() {
        server.start();
        System.out.println("API server started on port " + server.getAddress().getPort());
    }

    public void stop() {
        server.stop(0);
        executorService.shutdown();
        System.out.println("API server stopped.");
    }


}

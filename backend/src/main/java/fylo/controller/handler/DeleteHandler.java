package fylo.controller.handler;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import fylo.service.FileSharer;

import java.io.*;

public class DeleteHandler implements HttpHandler {

    private final FileSharer fileSharer;

    public DeleteHandler(FileSharer fileSharer) {
        this.fileSharer = fileSharer;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Headers headers = exchange.getResponseHeaders();
        headers.add("Access-Control-Allow-Origin", "*");

        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            headers.add("Access-Control-Allow-Methods", "DELETE, OPTIONS");
            headers.add("Access-Control-Allow-Headers", "Content-Type");
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        if (!exchange.getRequestMethod().equalsIgnoreCase("DELETE")) {
            String response = "Method Not Allowed";
            exchange.sendResponseHeaders(405, response.getBytes().length);
            try (OutputStream out = exchange.getResponseBody()) {
                out.write(response.getBytes());
            }
            return;
        }

        String path = exchange.getRequestURI().getPath();
        String code = path.substring(path.lastIndexOf('/') + 1);

        if (code.isEmpty()) {
            String response = "Bad Request: Missing code";
            exchange.sendResponseHeaders(400, response.getBytes().length);
            try (OutputStream out = exchange.getResponseBody()) {
                out.write(response.getBytes());
            }
            return;
        }

        boolean removed = fileSharer.removeFile(code);

        if (removed) {
            String response = "{\"status\":\"deleted\"}";
            headers.add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.getBytes().length);
            try (OutputStream out = exchange.getResponseBody()) {
                out.write(response.getBytes());
            }
        } else {
            String response = "Not Found: Invalid code";
            exchange.sendResponseHeaders(404, response.getBytes().length);
            try (OutputStream out = exchange.getResponseBody()) {
                out.write(response.getBytes());
            }
        }
    }
}

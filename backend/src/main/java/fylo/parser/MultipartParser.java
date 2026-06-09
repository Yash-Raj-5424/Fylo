package fylo.parser;

public class MultipartParser {

    private final byte[] data;
    private final String boundary;

    public MultipartParser(byte[] data, String boundary) {
        this.data = data;
        this.boundary = boundary;
    }

    public ParseResult parse() {

        try {
            String stringData = new String(data);
            String filenameMarker = "filename=\"";
            int filenameStart = stringData.indexOf(filenameMarker);

            if (filenameStart == -1) return null;

            filenameStart += filenameMarker.length();
            int filenameEnd = stringData.indexOf("\"", filenameStart);
            String filename = stringData.substring(filenameStart, filenameEnd);

            String contentTypeMarker = "Content-Type: ";
            int contentTypeStart = stringData.indexOf(contentTypeMarker, filenameEnd);

            String contentType = "application/octet-stream";
            if (contentTypeStart != -1) {
                contentTypeStart += contentTypeMarker.length();
                int contentTypeEnd = stringData.indexOf("\r\n", contentTypeStart);
                contentType = stringData.substring(contentTypeStart, contentTypeEnd);
            }

            String headerEndMarker = "\r\n\r\n";
            int headerEnd = stringData.indexOf(headerEndMarker);

            if (headerEnd == -1) return null;
            int contentStart = headerEnd + headerEndMarker.length();

            byte[] boundaryBytes = ("\r\n--" + boundary + "--").getBytes();
            int contentEnd = indexOf(data, boundaryBytes, contentStart);

            if (contentEnd == -1) {
                boundaryBytes = ("\r\n--" + boundary).getBytes();
                contentEnd = indexOf(data, boundaryBytes, contentStart);
            }

            if (contentEnd == -1 || contentEnd <= contentStart) return null;

            byte[] fileContent = new byte[contentEnd - contentStart];
            System.arraycopy(data, contentStart, fileContent, 0, fileContent.length);

            return new ParseResult(filename, fileContent, contentType);
        } catch (Exception e) {
            System.out.println("Error parsing multipart data: " + e.getMessage());
            return null;
        }
    }

    private int indexOf(byte[] data, byte[] target, int start) {
        outer:
        for (int i = start; i <= data.length - target.length; i++) {
            for (int j = 0; j < target.length; j++) {
                if (data[i + j] != target[j]) continue outer;
            }
            return i;
        }
        return -1;
    }

}

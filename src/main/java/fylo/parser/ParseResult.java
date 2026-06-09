package fylo.parser;

public class ParseResult {

    public final String filename;
    public final byte[] fileContent;
    public final String contentType;

    public ParseResult(String fileName, byte[] fileContent, String contentType) {
        this.filename = fileName;
        this.fileContent = fileContent;
        this.contentType = contentType;
    }
}

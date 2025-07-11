package src.main.java.httpeek;

public class Utils {
    static byte[] toBytes(String str) {
        return str.getBytes();
    }

    static String getFileExtension(String fileName) {
        int idx = fileName.lastIndexOf(".");

        if (idx != -1) {
            return fileName.substring(idx + 1, fileName.length());
        }

        return "";
    }

    static String getMimeType(String extension) {
        String mime = switch (extension) {
            case "html" -> "text/html";
            case "css" -> "text/css";
            case "js" -> "text/javascript";
            case "md" -> "text/markdown";
            case "json" -> "application/json";
            case "jpeg", "jpg" -> "image/jpeg";
            case "png" -> "image/png";
            default -> "text/plain";
        };

        return mime;
    }
}

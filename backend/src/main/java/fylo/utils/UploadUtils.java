package fylo.utils;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class UploadUtils {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private static final Set<String> BLOCKED_EXTENSIONS = new HashSet<>(Arrays.asList(
        "exe", "bat", "bin", "cmd", "com", "cpl", "gadget", "inf1", "ins", "inx", "isu",
        "jar", "jse", "js", "jsp", "class", "lnk", "msc", "msi", "msp", "mst",
        "paf", "pif", "ps1", "ps1xml", "ps2", "ps2xml", "psc1", "psc2", "reg", "scr",
        "sct", "sh", "shb", "shs", "u3p", "vb", "vbe", "vbs", "vsmacros", "vsw",
        "ws", "wsf", "wsh", "dll", "sys", "drv", "ocx", "ax", "cpl", "appref-ms"
    ));

    static {
        String envBlocked = System.getenv("FYLO_BLOCKED_EXTENSIONS");
        if (envBlocked != null && !envBlocked.isBlank()) {
            String[] custom = envBlocked.split(",");
            BLOCKED_EXTENSIONS.clear();
            for (String ext : custom) {
                BLOCKED_EXTENSIONS.add(ext.trim().toLowerCase());
            }
        }
    }

    public static int generateCode(){
        int DYNAMIC_STARTING_PORT = 49152;
        int DYNAMIC_ENDING_PORT = 65535;
        return (int) (Math.random() * (DYNAMIC_ENDING_PORT - DYNAMIC_STARTING_PORT+1))
                + DYNAMIC_STARTING_PORT;
    }

    public static String generateToken() {
        StringBuilder sb = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            sb.append(ALPHANUMERIC.charAt(RANDOM.nextInt(ALPHANUMERIC.length())));
        }
        return sb.toString();
    }

    public static boolean isAllowedFile(String filename) {
        if (filename == null || filename.isEmpty()) return true;
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex == -1 || dotIndex == filename.length() - 1) return true;
        String ext = filename.substring(dotIndex + 1).toLowerCase();
        return !BLOCKED_EXTENSIONS.contains(ext);
    }
}

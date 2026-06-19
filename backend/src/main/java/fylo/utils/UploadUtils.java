package fylo.utils;

public class UploadUtils {

    public static int generateCode(){
        int DYNAMIC_STARTING_PORT = 49152;
        int DYNAMIC_ENDING_PORT = 65535;
        return (int) (Math.random() * (DYNAMIC_ENDING_PORT - DYNAMIC_STARTING_PORT+1))
                + DYNAMIC_STARTING_PORT;
    }
}

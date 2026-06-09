package fylo;


import fylo.controller.FileController;

public class App {
    public static void main(String[] args) {

        try{
            FileController fileController = new FileController(8080);
            fileController.start();

            System.out.println("Server started on port 8080. Press Ctrl+C to stop");
            System.out.println("UI is available at http://localhost:3000");

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Shutting down the server...");
                fileController.stop();
            }));




        }catch (Exception e){
            System.err.println("Failed to start the server: " + e.getMessage());
            e.printStackTrace();
        }

    }
}

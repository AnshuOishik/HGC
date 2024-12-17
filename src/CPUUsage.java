package hgc;
import java.io.BufferedReader;
import java.io.InputStreamReader;

class CPUUsage {

    // Method to get the CPU usage for the compression process
    public static String compCpuUsage(String command) {
    	String cpuUsage = "Failed to retrieve CPU usage"; // Default message if retrieval fails
        try {
            // Step 1: Find the PID of the compression process 
            String processName = "java -Xms12g hgc.HGC "+command;  // Example process name
            String[] cmd = { "/bin/sh", "-c", "pgrep -f \"" + processName + "\"" };
            Process process = Runtime.getRuntime().exec(cmd);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            
            String pid = reader.readLine();
            if (pid == null) {
                System.out.println("Process not found");
                return cpuUsage;  // Return the failure message if the process is not found
            }
            
            // Step 2: Find the CPU usage of the process using the PID
            String[] cpuCmd = { "/bin/sh", "-c", "ps -p " + pid + " -o %cpu" };
            Process cpuProcess = Runtime.getRuntime().exec(cpuCmd);
            BufferedReader cpuReader = new BufferedReader(new InputStreamReader(cpuProcess.getInputStream()));
            
            // Skip the header line
            cpuReader.readLine();
            
            // Read the CPU usage
            cpuUsage = cpuReader.readLine();
            if (cpuUsage == null) {
                System.out.println("Failed to retrieve CPU usage");
                return "Failed to retrieve CPU usage";  // Return failure message
            }

            // Print the CPU usage as originally intended
            System.out.println("CPU usage of the process: " + cpuUsage + "%");

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();  // Return error message if something goes wrong
        }
        return cpuUsage.trim();  // Return the CPU usage as a string
    }

    // Method to get the CPU usage for the decompression process
    public static String decomCpuUsage(String command) {
        String cpuUsage = "Failed to retrieve CPU usage"; // Default message if retrieval fails
        try {
            // Step 1: Find the PID of the decompression process
            String processName = "java -Xms4g hgc.HGC "+command;  // Example decompression process name
            String[] cmd = { "/bin/sh", "-c", "pgrep -f \"" + processName + "\"" };
            Process process = Runtime.getRuntime().exec(cmd);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            
            String pid = reader.readLine();
            if (pid == null) {
                System.out.println("Process not found");
                return cpuUsage;  // Return failure message if the process is not found
            }

            // Step 2: Find the CPU usage of the process using the PID
            String[] cpuCmd = { "/bin/sh", "-c", "ps -p " + pid + " -o %cpu" };
            Process cpuProcess = Runtime.getRuntime().exec(cpuCmd);
            BufferedReader cpuReader = new BufferedReader(new InputStreamReader(cpuProcess.getInputStream()));
            
            // Skip the header line
            cpuReader.readLine();
            
            // Read the CPU usage
            cpuUsage = cpuReader.readLine();
            if (cpuUsage == null) {
                System.out.println("Failed to retrieve CPU usage");
                return "Failed to retrieve CPU usage";  // Return failure message
            }

            // Print the CPU usage as originally intended
            System.out.println("CPU usage of the process: " + cpuUsage + "%");

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();  // Return error message if something goes wrong
        }
        return cpuUsage.trim();  // Return the CPU usage as a string
    }

    public static void main(String[] args) {
        // Test the methods
        //String cpuUsage = compCpuUsage(command);
        //System.out.println("Compression CPU Usage (returned): " + cpuUsage + "%");

        //String decomCpuUsage = decomCpuUsage(command);
        //System.out.println("Decompression CPU Usage (returned): " + decomCpuUsage + "%");
    }
}






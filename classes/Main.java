import java.io.IOException;
import java.util.List;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.tudresden.sumo.cmd.Trafficlight;
import it.polito.appeal.traci.SumoTraciConnection;

public class Main {

    public static void main(String[] args) {
        int simulationProject = 1;

         if ("--simulation-project".equals(args[1])) {
            simulationProject = Integer.parseInt(args[1]);
      
        }
        
        String sumoConfigFilePath;
        switch (simulationProject) {
            case 1:
            // insert FULL Path ie: /Users/username/desktop/.../jade/sumo/osm.sumocfg
                sumoConfigFilePath = "/Users/alexarmero/Desktop/DISSERTATION/A-Multi-Agent-Simulation-/jade/sumo/1V/osm.sumocfg";
                break;
            case 2:
                sumoConfigFilePath ="/Users/alexarmero/Desktop/DISSERTATION/A-Multi-Agent-Simulation-/jade/sumo/2V/osm.sumocfg";
                break;
            case 3:
                sumoConfigFilePath = "/Users/alexarmero/Desktop/DISSERTATION/A-Multi-Agent-Simulation-/jade/sumo/3V/osm.sumocfg";
                break;
            default:
                System.err.println("Invalid command line argument. Please provide 1, 2, or 3.");
                return;
        }

     try {
            ProcessBuilder pb = new ProcessBuilder("sumo-gui", "-c", sumoConfigFilePath);
            Process process = pb.start();
            System.out.println("SUMO started.");
        } catch (IOException e) {
            System.err.println("Error starting SUMO: " + e.getMessage());
            return;
        }

        
        System.out.println("Starting JADE...");
        Runtime rt = Runtime.instance();
        Profile profile = new ProfileImpl();
        profile.setParameter(Profile.MAIN_HOST, "localhost");
        profile.setParameter(Profile.MAIN_PORT, "1099");
    
        
        AgentContainer container = rt.createMainContainer(profile);
        System.out.println("Main container created.");

        try {
            // Create SumoTraciConnection
            SumoTraciConnection conn = new SumoTraciConnection(8773);
            conn.addOption("collision.action", "remove");
            conn.addOption("waiting-time-memory", "300");
            conn.addOption("time-to-teleport", "999999");
            conn.addOption("max-depart-delay", "1000");
            conn.addOption("log", "false");
            conn.runServer();
            conn.do_timestep();
            
            List<String> trafficLightIDs = (List<String>) conn.do_job_get(Trafficlight.getIDList());
            long trafficLightInterval = 100;
            Object[] agentArgs = new Object[]{conn};
            Object[] trafficLightAgentArgs = new Object[]{conn,trafficLightIDs,trafficLightInterval};
            System.out.println("1) Connecting to SUMO at IP: localhost and port: 8773");


            // Create agents
            AgentController trafficLightAgent = container.createNewAgent("TrafficLightAgent", "agents.TrafficLightAgent",trafficLightAgentArgs);
            AgentController vehicleAgent = container.createNewAgent("VehicleAgent", "agents.VehicleAgent", agentArgs);
            AgentController cyclistAgent = container.createNewAgent("CyclistAgent", "agents.CyclistAgent", agentArgs);
            // Create AgentCounterAgent
            // AgentController agentCounterAgent = container.createNewAgent("AgentCounterAgent", "agents.AgentCounterAgent",agentArgs);
            // agentCounterAgent.start();

            // Create ExecutorService
            ExecutorService executor = Executors.newFixedThreadPool(3);


           // Start agents in separate threads
            executor.submit(() -> {
                try {
                    vehicleAgent.start();
                } catch (StaleProxyException e) {
                    e.printStackTrace();
                }
            });

            executor.submit(() -> {
                try {
                    cyclistAgent.start();
                } catch (StaleProxyException e) {
                    e.printStackTrace();
                }
            });


              executor.submit(() -> {
                try {
                    trafficLightAgent.start();
                } catch (StaleProxyException e) {
                    e.printStackTrace();
                }
            });

            System.out.println("connected!");

            // Implement a loop that runs the simulation for 500 ticks
            // int simulationTicks = 500;
            // for (int i = 0; i < simulationTicks; i++) {
            //     conn.do_timestep();
            // }

            // Shutdown ExecutorService
            executor.shutdown();

            // Close the connection to the SUMO simulation
            // conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
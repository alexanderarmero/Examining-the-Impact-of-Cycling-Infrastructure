package agents;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import it.polito.appeal.traci.SumoTraciConnection;
import de.tudresden.sumo.cmd.Trafficlight;
import de.tudresden.sumo.cmd.Lane;
import java.io.IOException;
import java.util.List;

public class TrafficLightAgent extends Agent {
    
    private SumoTraciConnection conn;
    private List<String> trafficLightIDs;
    private long interval;

    
    protected void setup() {
        System.out.println("TrafficLightAgent setup started.");
     
    try {
        Object[] args = getArguments();
        trafficLightIDs = (List<String>) args[1];
        interval = (long) args[2];
        conn = (SumoTraciConnection) args[0];
   
        System.out.println("Traffic light IDs: " + trafficLightIDs);
        addBehaviour(new TrafficLightManagementBehaviour(this, interval));

    } catch (Exception e) {
        System.err.println("Error initializing connection to SUMOLIGHT: " + e.getMessage());
        doDelete();
        return;
    }

        System.out.println("TrafficLightAgent setup completed.");
    }

    private class TrafficLightManagementBehaviour extends TickerBehaviour {
        public TrafficLightManagementBehaviour(Agent agent, long interval) {
            super(agent, interval);
        }

        @Override
        protected void onTick() {
           try {
            conn.do_timestep(); // advance simulation tick
            manageTrafficLights();
        } catch (Exception e) {
            e.printStackTrace();
        }
        }
    }
private void manageTrafficLights() {
    try {
        for (String trafficLightID : trafficLightIDs) {
            int currentPhase = (int) conn.do_job_get(Trafficlight.getPhase(trafficLightID));
            int optimalPhase = determineOptimalPhase(trafficLightID);

            System.out.println("Traffic light ID: " + trafficLightID);
            System.out.println("Current phase: " + currentPhase);
            System.out.println("Optimal phase: " + optimalPhase);
            if (currentPhase != optimalPhase) {
                conn.do_job_set(Trafficlight.setPhase(trafficLightID, optimalPhase));
                System.out.println("Updated traffic light phase to optimal phase.");
            } else {
                System.out.println("Traffic light phase is already optimal.");
            }
        }
    } catch ( IllegalStateException e) {
        e.printStackTrace();
    } catch (Exception e) {
        e.printStackTrace();
    }
}
private int determineOptimalPhase(String trafficLightID) {
    int maxQueueIndex = -1;
    int maxQueueLength = -1;

    try {
        System.out.println("Determining Optimal Phase");

        String state = (String) conn.do_job_get(Trafficlight.getRedYellowGreenState(trafficLightID));
        int numPhases = state.length();

        for (int i = 0; i < numPhases; i++) {
             List<String> lanes = (List<String>) conn.do_job_get(Trafficlight.getControlledLanes(trafficLightID));
            int totalQueueLength = 0;

            for (String lane : lanes) {
                totalQueueLength += (int) conn.do_job_get(Lane.getLastStepVehicleNumber(lane));
            }

            System.out.println("Traffic light ID: " + trafficLightID);
            System.out.println("Total queue length for phase " + i + ": " + totalQueueLength);

            if (totalQueueLength > maxQueueLength) {
                maxQueueIndex = i;
                maxQueueLength = totalQueueLength;
                System.out.println("New maximum queue length found: " + maxQueueLength);
            }
        }
    } catch (IOException | IllegalStateException e) {
        e.printStackTrace();
    } catch (Exception e) {
        e.printStackTrace();
    }

    System.out.println("Optimal phase determined: " + maxQueueIndex);
    return maxQueueIndex;
}




}

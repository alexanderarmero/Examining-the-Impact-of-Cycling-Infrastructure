package behaviour;
import it.polito.appeal.traci.SumoTraciConnection;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import java.util.HashSet;
import java.util.Set;
import java.io.IOException;
import java.util.Random;

import agents.*;
import de.tudresden.sumo.objects.SumoStringList;
import de.tudresden.sumo.cmd.Vehicle;
import de.tudresden.sumo.util.SumoCommand;

public class RelaxedRuleBreakingAgentBehaviour extends TickerBehaviour implements CommunicationModels {

    private String agentType;
    private double silent;
    private SumoTraciConnection conn;
    private Random random;
     // Add a new HashSet to store processed vehicle IDs
    private Set<String> processedVehicles;


    double speed;
    double acceleration;
    double gapAcceptance;
    double laneChanging;

    // Constructor
    public RelaxedRuleBreakingAgentBehaviour(SumoTraciConnection conn, String agentType, double silent, Agent agent, long interval) {
        super(agent, interval);
        this.conn = conn;
        this.agentType = agentType;
        this.silent = silent;
        this.random = new Random();
        this.processedVehicles = new HashSet<>(); // Initialize the HashSet
    }

    // Override the action() method and implement behavior-specific logic
    @Override
    public void onTick() {

        try {
            // Get the list of vehicle IDs currently in the simulation
            SumoCommand getIdList = Vehicle.getIDList();
            Object result = this.conn.do_job_get(getIdList);

            SumoStringList vehicleIds = (SumoStringList) result;

          // Iterate through the list of vehicle IDs
        for (String vehicleId : vehicleIds) {

             if (processedVehicles.contains(vehicleId)) {
                // Vehicle has already been processed, skip to the next vehicle
                continue;
            }

            try {
         
            // Add the vehicle ID to the processed vehicles set
            processedVehicles.add(vehicleId);
            // Get the vehicle type ID
            SumoCommand getTypeId = Vehicle.getTypeID(vehicleId);
            String vType = (String)this.conn.do_job_get(getTypeId);

            if (this.silent <= 0.5) {
                // Agent is not silent, perform communication-related actions
                System.out.println("Agent is communicative");
                sendMessage(vehicleId, agentType);
             }else{ 
               System.out.println("Agent is silent");
            }


                switch (agentType) {
                    case "vehicle":
                        if (vType.equals("veh__passenger")) {
                               conn.do_job_set(Vehicle.setParameter(vehicleId, "lcCooperative", "0.2"));
                               conn.do_job_set(Vehicle.setParameter(vehicleId, "lcSpeedGain", "1.2"));
                               conn.do_job_set(Vehicle.setParameter(vehicleId, "lcKeepRight", "0.1"));
                            System.out.println("Connected to car: " + vehicleId);

                            // Define values for RelaxedRuleBreaking cars
                            speed = 13.9; // meters per second (50 km/h)
                            acceleration = 1.5; // meters per second squared
                            gapAcceptance = 2.5; // meters
                            laneChanging = 0.5; // some value representing lane changing behavior

                           conn.do_job_set(Vehicle.setParameter(vehicleId, "speedFactor", Double.toString(speed)));
                            conn.do_job_set(Vehicle.setParameter(vehicleId, "maxSpeed", Double.toString(speed)));
                            conn.do_job_set(Vehicle.setParameter(vehicleId, "minGap", Double.toString(gapAcceptance)));
                            conn.do_job_set(Vehicle.setParameter(vehicleId, "maxAccel", Double.toString(acceleration)));


                        } else if (vType.equals("veh__motorcycle")) {
                            System.out.println("Connected to motorcycle: " + vehicleId);
                               conn.do_job_set(Vehicle.setParameter(vehicleId, "lcCooperative", "0.2"));
                               conn.do_job_set(Vehicle.setParameter(vehicleId, "lcSpeedGain", "1.2"));
                               conn.do_job_set(Vehicle.setParameter(vehicleId, "lcKeepRight", "0.1"));

                            // Define values for RelaxedRuleBreaking motorcycles
                            speed = 13.9; // meters per second (50 km/h)
                            acceleration = 1.5; // meters per second squared
                            gapAcceptance = 2.0; // meters
                            laneChanging = 0.7; // some value representing lane changing behavior

                            conn.do_job_set(Vehicle.setParameter(vehicleId, "speedFactor", Double.toString(speed)));
                            conn.do_job_set(Vehicle.setParameter(vehicleId, "maxSpeed", Double.toString(speed)));
                            conn.do_job_set(Vehicle.setParameter(vehicleId, "minGap", Double.toString(gapAcceptance)));
                            conn.do_job_set(Vehicle.setParameter(vehicleId, "maxAccel", Double.toString(acceleration)));

                        } else if (vType.equals("pt_bus")) {
                        System.out.println("Connected to bus: " + vehicleId);
                         conn.do_job_set(Vehicle.setParameter(vehicleId,  "lcCooperative", "0.3"));
                         conn.do_job_set(Vehicle.setParameter(vehicleId,  "lcSpeedGain", "1.1"));
                         conn.do_job_set(Vehicle.setParameter(vehicleId,  "lcKeepRight", "0.1"));

                        // Define values for RelaxedRuleBreaking buses
                        speed = 11.1; // meters per second (40 km/h)
                        acceleration = 1.5; // meters per second squared
                        gapAcceptance = 2.5; // meters
                        laneChanging = 0.5; // some value representing lane changing behavior

                            conn.do_job_set(Vehicle.setParameter(vehicleId, "speedFactor", Double.toString(speed)));
                            conn.do_job_set(Vehicle.setParameter(vehicleId, "maxSpeed", Double.toString(speed)));
                            conn.do_job_set(Vehicle.setParameter(vehicleId, "minGap", Double.toString(gapAcceptance)));
                            conn.do_job_set(Vehicle.setParameter(vehicleId, "maxAccel", Double.toString(acceleration)));

                    }
                    break;
                case "cyclist":
                    if (vType.equals("cyc__bicycle")) {
                        System.out.println("Connected to cyclist: " + vehicleId);
                           conn.do_job_set(Vehicle.setParameter(vehicleId, "lcCooperative", "0.2"));
                           conn.do_job_set(Vehicle.setParameter(vehicleId, "lcSpeedGain", "1.1"));
                           conn.do_job_set(Vehicle.setParameter(vehicleId, "lcKeepRight", "0.1"));

                        // Define values for RelaxedRuleBreaking cyclists
                        speed = 4.4; // meters per second (16 km/h)
                        acceleration = 1.0; // meters per second squared
                        gapAcceptance = 1.5; // meters
                        laneChanging = 0.5; // some value representing lane changing behavior
                        
                        conn.do_job_set(Vehicle.setParameter(vehicleId, "speedFactor", Double.toString(speed)));
                        conn.do_job_set(Vehicle.setParameter(vehicleId, "maxSpeed", Double.toString(speed)));
                        conn.do_job_set(Vehicle.setParameter(vehicleId, "minGap", Double.toString(gapAcceptance)));
                        conn.do_job_set(Vehicle.setParameter(vehicleId, "maxAccel", Double.toString(acceleration)));

                    }
                   break;
            
            }
            //  Remove vehicles that have left the simulation from the processedVehicles set
              processedVehicles.retainAll(vehicleIds);

              } catch (IOException | IllegalStateException e) {
                e.printStackTrace();
                continue;
               } catch (Exception e) {
            // Handle the exception caused by a vehicle that no longer exists in the simulation
            System.out.println("Vehicle " + vehicleId + " not found in the simulation");
            processedVehicles.remove(vehicleId); // Remove the vehicle ID from the processedVehicles set
            continue;
}
            // Add the vehicle ID to the processed vehicles set
            processedVehicles.add(vehicleId);
        
        }
    } catch (IOException | IllegalStateException e) {
        e.printStackTrace();
    } catch (Exception e) {
        e.printStackTrace();
    }
}

private void sendMessage(String vehicleId, String agentType) {    
    // Simulate the probability of sending a message
    double messageProbability = random.nextDouble();

    //Implement get Neighbours


    if (messageProbability < 0.5) {
        // 50% chance to send a message
        System.out.println("[" + agentType + " " + vehicleId + "] Sending random message to nearby agents");
        // Implement random message sending logic here
    }
}

// @Override
// public boolean done() {
//     return false; // Agent never completes behavior
// }
}
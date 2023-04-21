package agents;
import behaviour.InHurryRuleBreakingAgentBehaviour;
import behaviour.InHurryRuleFollowingAgentBehaviour;
import behaviour.RelaxedRuleBreakingAgentBehaviour;
import behaviour.RelaxedRuleFollowingAgentBehaviour;
import it.polito.appeal.traci.SumoTraciConnection;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;

public class CyclistAgent extends Agent implements CommunicationModels {

    private SumoTraciConnection conn;
    private double inHurry;
    private double silent;
    private double ruleBreaking;

    protected void setup() {
        System.out.println("Inside CyclingAgent setup method...");

        inHurry = generateRandomValue();
        silent = generateRandomValue();
        ruleBreaking = generateRandomValue();

        try {
              Object[] args = getArguments();
              conn = (SumoTraciConnection) args[0];
            } catch (Exception e) {
            System.err.println("Error initializing connection to SUMO: " + e.getMessage());
            doDelete();
            return;
        }
        
        // Add behavior for agent
        Behaviour vehicleBehaviour;
        long interval = 100; // 1000 is the interval in milliseconds
        if (inHurry > 0.5) {
            if (ruleBreaking > 0.5) {
                vehicleBehaviour = new InHurryRuleBreakingAgentBehaviour(conn, "cyclist", silent, this, interval);
            } else {
                vehicleBehaviour = new InHurryRuleFollowingAgentBehaviour(conn, "cyclist", silent, this, interval);
            }
        } else {
            if (ruleBreaking > 0.5) {
                vehicleBehaviour = new RelaxedRuleBreakingAgentBehaviour(conn, "cyclist", silent, this, interval);
            } else {
                vehicleBehaviour = new RelaxedRuleFollowingAgentBehaviour(conn, "cyclist", silent, this, interval);
            }
        }
        addBehaviour(vehicleBehaviour);
    }

    protected void takeDown() {
        if (conn != null) {
            try {
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }    
}
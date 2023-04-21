package agents;
import java.util.Random;

public interface CommunicationModels {
    double inHurry = 0;
    double ruleBreaking = 0;
    double silent = 0;

    default double generateRandomValue() {
        Random random = new Random();
        return random.nextDouble();
    }
}

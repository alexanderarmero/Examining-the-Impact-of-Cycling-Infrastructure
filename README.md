# Examining the Impact of Cycling Infrastructure

This repository contains the dissertation project for King's College London computer science graduate. The project is a SUMO and JADE multi-agent system simulation examining the impact of different cycling infrastructure densities in Piccadilly Circus. The project consists of three different simulations, each with varying cycling infrastructure densities.

The goal of the project is to determine the impact of these densities on various traffic performance markers, including travel time, delay, average speed, flow, occupancy, queue length, and emissions.

The JADE section defines behaviors and assigns them to different SUMO agents in the simulations. These behaviors are categorized into:

- In a Hurry
- Relaxed
- Rule Following
- Rule Breaking
- Silent
- Communicative

These behaviors impact the agents' average speed, max speed, acceleration, min_gap, and lane-changing probability and frequency.

The project concludes its results using Python scripts that generate comparative visualizations.

## Installation Guide

This installation guide is meant for MacOS users, links are provided to documentation to help Linux or windows users.

Please follow these steps to install and run the project:

1. Download and install SUMO:

   - [Download SUMO](https://sumo.dlr.de/docs/Downloads.php)

2. Set `$SUMO_HOME` environment variable and other relevant variables:

   - Make sure you install SUMO correctly, following the docs:

     To finalize your SUMO setup, please make sure to set the SUMO_HOME environment variable and have it point to the directory of your SUMO installation. Depending on your shell, you may set this variable either in .bashrc or .zshrc. To set this variable in .bashrc you can use the following commands.

     `touch ~/.bashrc; open ~/.bashrc`

     Just insert the following new line at the end of the file:

     `export SUMO_HOME=/your/path/to/sumo`

     where /your/path/to/sumo is the path stated in the caveats section of the brew install sumo command. Restart the Terminal (or run source ~/.bashrc) and test the newly added variable:

     `echo $SUMO_HOME`

     After the installation you need to log out/in in order to let X11 start automatically, when calling a gui-based application like sumo-gui. (Alternatively, you may start X11 manually by pressing cmd-space and entering XQuartz).

   - Refer the installation instructions [here](https://sumo.dlr.de/docs/Installing/index.html).

3. Clone the repository:
   `git clone https://github.com/alexanderarmero/Examining-the-Impact-of-Cycling-Infrastructure.git`

4. Unzip the `lib` folder and make sure the `.jar` files are referenced in the project.

5. To reference the `.jar` files in VSCode:

- For VSCode, follow the instructions [here](https://code.visualstudio.com/docs/java/java-project).

6.  To compile the SUMO simulations without JADE interactions comment the remote port setting in the `osm.sumocfg`:

`<!-- <remote-port value="8773"/> -->`

Then run the following command from each of the simulation directories:

`sumo-gui -c osm.sumocfg`

The `-gui` flag is optional.

7. To compile the JADE classes and run JADE to execute the SUMO simulation, execute the following from the root directory:
   `javac -cp lib/TraaS.jar:lib/jade.jar classes/agents/CommunicationModels.java classes/agents/TrafficLightAgent.java classes/agents/VehicleAgent.java classes/agents/CyclistAgent.java classes/behaviour/InHurryRuleBreakingAgentBehaviour.java classes/behaviour/InHurryRuleFollowingAgentBehaviour.java classes/behaviour/RelaxedRuleBreakingAgentBehaviour.java classes/behaviour/RelaxedRuleFollowingAgentBehaviour.java classes/Main.java && java -cp lib/TraaS.jar:lib/jade.jar:lib/log4j-api-2.20.0.jar:lib/log4j-core-2.20.0.jar:./classes/agents:./classes/behaviours:./classes Main --simulation-project "<simulation_version>"`

Replace `<simulation_version>` with `1`, `2`, or `3` to specify the simulation version.

8. Add the following function to your `bash_profile` on macOS to simplify the above command:

`function compile_sumo() {`
`if [ "$#" -ne 1 ]; then`
`echo "Please provide a command line argument (1, 2, or 3) to select the SUMO project."`
`return`
`fi `
` javac -cp lib/TraaS.jar:lib/jade.jar classes/agents/CommunicationModels.java classes/agents/TrafficLightAgent.java classes/agents/VehicleAgent.java classes/agents/CyclistAgent.java classes/behaviour/``InHurryRuleBreakingAgentBehaviour.java classes/behaviour/InHurryRuleFollowingAgentBehaviour.java classes/behaviour/RelaxedRuleBreakingAgentBehaviour.java classes/behaviour/RelaxedRuleFollowingAgentBehaviour.java ``classes/Main.java && java -cp lib/TraaS.jar:lib/jade.jar:lib/log4j-api-2.20.0.jar:lib/log4j-core-2.20.0.jar:./classes/agents:./classes/behaviours:./classes Main --simulation-project $1 `

For Windows and Linux users, you can create a similar script or function for your specific shell. Adjust the syntax accordingly.

9. To compile and run the simulations with JADE, run the following commands:

`compile_sumo 1`
`compile_sumo 2`
`compile_sumo 3`

10. To execute the Python scripts, run the following command:

`python3 simulation_scripts.py <simulation_version>`

Replace `<simulation_version>` with `1`, `2`, or `3`.

11. To generate visualizations of results and output them to a CSV file, run the following command:

`python3 metric_graphs.py`

Note: You must run all three simulations before executing this command.

12. Make sure to adjust any command to match your directory structure if modified.

## Contributing

Please read the [CONTRIBUTING.md](CONTRIBUTING.md) file for details on how to contribute to this project.

## License

This project is licensed under the MIT License. See the [LICENSE.md](LICENSE.md) file for details.

## Acknowledgements

- King's College London
- SUMO
- JADE

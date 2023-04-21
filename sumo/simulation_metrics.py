import sys
import csv
import xml.etree.ElementTree as ET

if len(sys.argv) != 2 or sys.argv[1] not in ("1", "2", "3"):
    print("Usage: python script.py [1|2|3]")
    sys.exit(1)

target_folder = sys.argv[1] + "V"

emissions_file = f"./{target_folder}/emission_output.xml"
edgedata_file = f"./{target_folder}/edge_data.xml"
lanedata_file = f"./{target_folder}/lane_data.xml"
tripinfo_file = f"./{target_folder}/tripinfo.xml"
stopinfo_file = f"./{target_folder}/stopinfos.xml"

# Read all XML files
stopinfo_tree = ET.parse(stopinfo_file)
emissions_tree = ET.parse(emissions_file)
edgedata_tree = ET.parse(edgedata_file)
lanedata_tree = ET.parse(lanedata_file)
tripinfo_tree = ET.parse(tripinfo_file)

# Initialize metrics
total_travel_time = 0
total_delay = 0
total_bus_travel_time = 0
total_speed = 0
vehicle_count = 0
total_flow = 0
total_occupancy = 0
total_queue_length = 0
total_emissions = {
    "CO_abs": 0,
    "CO2_abs": 0,
    "HC_abs": 0,
    "PMx_abs": 0,
    "NOx_abs": 0,
    "fuel_abs": 0,
    "electricity_abs": 0,
}


# Initialize metrics
total_bicycles = 0
total_motorcycles = 0
total_cars = 0
total_cycle_time = 0
simulation_duration = 0

# Aggregate vehicle count from tripinfo
for tripinfo in tripinfo_tree.findall("tripinfo"):
    vehicle_type = tripinfo.get("vType")
    if vehicle_type == "cyc__bicycle":
        total_bicycles += 1
        total_cycle_time += float(tripinfo.get("duration"))
    elif vehicle_type == "veh__motorcycle":
        total_motorcycles += 1
    elif vehicle_type == "veh__passenger":
        total_cars += 1
    
# Calculate average cycle time
average_cycle_time = total_cycle_time / (total_bicycles)


# Calculate simulation duration from edgedata
simulation_duration = float(edgedata_tree.find("interval").get("end"))

# Print aggregated metrics
print("Total bicycles:", total_bicycles)
print("Total motorcycles:", total_motorcycles)
print("Total cars:", total_cars)
print("Average Cycle Time: {:.2f} s".format(average_cycle_time))
print("Simulation duration: {:.2f} s".format(simulation_duration))


# Aggregate bus travel times
for stopinfo in stopinfo_tree.findall("stopinfo"):
    total_bus_travel_time += float(stopinfo.get("ended")) - float(stopinfo.get("started"))

# Aggregate metrics from tripinfo
for tripinfo in tripinfo_tree.findall("tripinfo"):
    total_travel_time += float(tripinfo.get("duration"))
    total_delay += float(tripinfo.get("timeLoss"))
    vehicle_count += 1

# Aggregate metrics from edgedata
intervalEdge = edgedata_tree.find("interval")
for edge in intervalEdge.findall("edge"):
    total_speed += float(edge.get("speed"))
    total_flow += float(edge.get("departed")) + float(edge.get("arrived"))
    total_queue_length += float(edge.get("waitingTime"))

# Aggregate metrics from lanedata
intervalLane = lanedata_tree.find("interval")
for edge in intervalLane.findall("edge"):
    for lane in edge.findall("lane"):
        total_occupancy += float(lane.get("occupancy"))

# Aggregate metrics from emissions
for timestep in emissions_tree.findall("timestep"):
    for vehicle in timestep.findall("vehicle"):
        total_emissions["CO_abs"] += float(vehicle.get("CO"))
        total_emissions["CO2_abs"] += float(vehicle.get("CO2"))
        total_emissions["HC_abs"] += float(vehicle.get("HC"))
        total_emissions["PMx_abs"] += float(vehicle.get("PMx"))
        total_emissions["NOx_abs"] += float(vehicle.get("NOx"))
        total_emissions["fuel_abs"] += float(vehicle.get("fuel"))
        total_emissions["electricity_abs"] += float(vehicle.get("electricity"))

# Compute average metrics
average_travel_time = total_travel_time / vehicle_count
average_delay = total_delay / vehicle_count
average_speed = total_speed / vehicle_count
average_flow = total_flow / vehicle_count
average_occupancy = total_occupancy / vehicle_count
average_queue_length = total_queue_length / vehicle_count
average_bus_travel_time = total_bus_travel_time / vehicle_count


# Print aggregated metrics in rounded format
print("Average Bus Travel Time: {:.2f} s".format(average_bus_travel_time))
print("Average Travel Time: {:.2f} s".format(average_travel_time))
print("Average Delay: {:.2f} s".format(average_delay))
print("Average Speed: {:.2f} km/h".format(average_speed * 3.6))  # Convert from m/s to km/h
print("Average Flow: {:.2f} vehicles".format(average_flow))
print("Average Occupancy: {:.2f} %".format(average_occupancy))
print("Average Queue Length: {:.2f} vehicles".format(average_queue_length))

# Print total emissions in a rounded format with units
print("Total Emissions:")
emission_units = {
    "CO_abs": "g",
    "CO2_abs": "g",
    "HC_abs": "g",
    "PMx_abs": "g",
    "NOx_abs": "g",
    "fuel_abs": "ml",
    "electricity_abs": "Wh",
}
for emission_type, value in total_emissions.items():
    print("  {}: {:.2f} {}".format(emission_type, value,emission_units[emission_type]))



# Save the metrics in a CSV file
metrics = [
    ("Total bicycles", total_bicycles),
    ("Total motorcycles", total_motorcycles),
    ("Total cars", total_cars),
    ("Average Cycle Time", average_cycle_time),
    ("Simulation duration", simulation_duration),
    ("Average Bus Travel Time", average_bus_travel_time),
    ("Average Travel Time", average_travel_time),
    ("Average Delay", average_delay),
    ("Average Speed", average_speed * 3.6),  # Convert from m/s to km/h
    ("Average Flow", average_flow),
    ("Average Occupancy", average_occupancy),
    ("Average Queue Length", average_queue_length),
] + [(emission_type, value) for emission_type, value in total_emissions.items()]

output_csv = f"metrics{sys.argv[1]}.csv"

with open(output_csv, "w", newline="") as csvfile:
    csv_writer = csv.writer(csvfile)
    csv_writer.writerow(["Metric", "Value", "Unit"])
    for metric, value in metrics:
        unit = emission_units.get(metric, "")
        csv_writer.writerow([metric, value, unit])
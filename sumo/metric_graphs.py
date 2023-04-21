import csv
import pandas as pd
import plotly.graph_objects as go
import plotly.subplots as sp

def read_metrics_from_csv(file_path):
    metrics = {}
    with open(file_path, "r", newline="") as csvfile:
        csv_reader = csv.reader(csvfile)
        next(csv_reader)  # Skip header
        for row in csv_reader:
            metrics[row[0]] = float(row[1])
    return metrics

def combine_metrics_to_csv(metrics1, metrics2, metrics3, output_file):
    combined_metrics = [metrics1, metrics2, metrics3]
    header = ["Field"] + [f"Simulation {i+1}" for i in range(len(combined_metrics))]
    
    with open(output_file, "w", newline="") as csvfile:
        csv_writer = csv.writer(csvfile)
        csv_writer.writerow(header)
        
        for field in metrics1.keys():
            row = [field] + [metrics[field] for metrics in combined_metrics]
            csv_writer.writerow(row)

def plot_table_from_csv(file_path):
    df = pd.read_csv(file_path)
    df.iloc[:, 1:] = df.iloc[:, 1:].round(2)
    fig = go.Figure(data=[go.Table(header=dict(values=df.columns),
                                   cells=dict(values=[df.Field, df['Simulation 1'], df['Simulation 2'], df['Simulation 3']]))])
    fig.show()

def plot_difference_table(file_path):
    df = pd.read_csv(file_path)
    df['Difference'] = df['Simulation 3'] - df['Simulation 1']
    df['Difference'] = df['Difference'].round(2)
    df['Color'] = ['red' if x >= 0 else 'green' for x in df['Difference']]
    
    # Exclude total agent count fields
    df = df[~df['Field'].str.contains('total agent count', case=False)]

    fig = go.Figure(data=[go.Table(header=dict(values=['Field', 'Difference']),
                                   cells=dict(values=[df.Field, df['Difference']], font_color=['black', df['Color']])),
                          ])
    fig.show()

def plot_metrics(metrics1, metrics2, metrics3, title, metrics_to_plot):
    # Create the subplot structure
    fig = sp.make_subplots(rows=len(metrics_to_plot), cols=1, shared_xaxes=True, vertical_spacing=0.05, subplot_titles=metrics_to_plot)

    # Add bars to the subplots
    for idx, metric_name in enumerate(metrics_to_plot, start=1):
        fig.add_trace(go.Bar(name="Simulation 1", x=["Simulation 1"], y=[metrics1[metric_name]], showlegend=False), row=idx, col=1)
        fig.add_trace(go.Bar(name="Simulation 2", x=["Simulation 2"], y=[metrics2[metric_name]], showlegend=False), row=idx, col=1)
        fig.add_trace(go.Bar(name="Simulation 3", x=["Simulation 3"], y=[metrics3[metric_name]], showlegend=False), row=idx, col=1)

    fig.update_yaxes(autorange=True, type="linear")  # Update the y-axis scale
    fig.update_layout(height=700 * len(metrics_to_plot), title_text=title)
    fig.show()

file1 = "metrics1.csv"
file2 = "metrics2.csv"
file3 = "metrics3.csv"

metrics1 = read_metrics_from_csv(file1)
metrics2 = read_metrics_from_csv(file2)
metrics3 = read_metrics_from_csv(file3)

# Define the list of metrics to plot
metrics_to_plot = [
    "Average Travel Time",
    "Average Delay",
    "Average Speed",
    "Average Flow",
    "Average Occupancy",
    "Average Queue Length",
    "CO_abs",
    "CO2_abs",
    "HC_abs",
    "PMx_abs",
    "NOx_abs",
    "fuel_abs",
    "electricity_abs",
]

output_file = "combined_metrics.csv"
combine_metrics_to_csv(metrics1, metrics2, metrics3, output_file)

plot_table_from_csv(output_file)
plot_difference_table(output_file)
plot_metrics(metrics1, metrics2, metrics3, "Metrics Comparison", metrics_to_plot)

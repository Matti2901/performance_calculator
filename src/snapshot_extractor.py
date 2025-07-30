import os
import re
import subprocess
import config

# === CONFIGURATION ===

# Only "compress" project for testing
#PROJECTS = ["compress"]
NON_MODULAR_PROJECTS = ["compress"]
VERSIONS = config.VERSIONS
JPROFILER_EXPORTER = config.JPROFILER_EXPORTER

# === Full version: uncomment to activate all projects ===
#PROJECTS = ["jackrabbit", "openmrs", "active", "compress", "roller"]
PROJECTS = config.PROJECTS
# NON_MODULAR_PROJECTS = ["compress"]
VERSIONS = ["init", "noDependency", "noModular"]

PROJECT_MODULES = config.MODULES_PROJECTS

SNAPSHOT_PREFIX = "snapshotAnalysis"
SNAPSHOT_SUFFIX = ".jps"

METRIC_EXPORT_TYPES = {
    "TelemetryCPU": "CPU_Load_telemetry",
    "TelemetryHeap": "Memory_telemetry",
    "RecordedObjects": "Recorded_Objects",
}

PROJECTS_PATH = os.path.join("..", "projects")
OUTPUT_DIR_BASE = os.path.join("..", "performance_data")

# === FUNCTIONS ===

def capitalize_version(version):
    """Capitalize the first letter of the version name."""
    return version[0].upper() + version[1:]

def extract_run_number(file_name):
    """Extract the run number from the snapshot filename."""
    match = re.search(r"snapshotAnalysis(\d+)", file_name)
    return match.group(1) if match else None

def export_metric(snapshot_path, export_type, metric, version, run_number, project, module=None):
    """
    Export a specific metric from a JProfiler snapshot to CSV.
    Creates the output directory if it does not exist.
    Skips export if the CSV file already exists.
    """
    version_cap = capitalize_version(version)
    file_name = f"{version_cap}{metric}{run_number}"

    # ⚠️ Special behavior for Roller: no module included in the output path
    if project == "roller":
        output_dir = os.path.join(OUTPUT_DIR_BASE, project)
    else:
        output_dir = os.path.join(OUTPUT_DIR_BASE, project)
        if module:
            output_dir = os.path.join(output_dir, module)

    os.makedirs(output_dir, exist_ok=True)

    output_csv = os.path.join(output_dir, f"{file_name}.csv")

    if os.path.exists(output_csv):
        print(f"⏭️  File already exists, skipping export: {output_csv}")
        return

    cmd = [
        JPROFILER_EXPORTER,
        snapshot_path,
        export_type,
        output_csv
    ]

    result = subprocess.run(cmd, capture_output=True, text=True)
    if result.returncode != 0:
        print(f"❌ Export error [{export_type}] for {snapshot_path}:\n{result.stderr}")
        return

    print(f"✅ CSV saved: {output_csv}")


def process_snapshot(directory_path, version, project, module=None):
    """
    Process all snapshot files in the given directory.
    For each valid snapshot, export all the configured metrics.
    """
    if not os.path.exists(directory_path):
        print(f"🚫 Directory does not exist: {directory_path}")
        return

    files = [f for f in os.listdir(directory_path)
             if f.startswith(SNAPSHOT_PREFIX) and f.endswith(SNAPSHOT_SUFFIX)]
    
    if not files:
        print(f"⚠️ No snapshots found in: {directory_path}")
        return

    for file in files:
        run_number = extract_run_number(file)
        if run_number is None:
            print(f"⚠️ Ignored file without run number: {file}")
            continue

        snapshot_path = os.path.join(directory_path, file)
        for export_type, metric in METRIC_EXPORT_TYPES.items():
            export_metric(snapshot_path, export_type, metric, version, run_number, project, module)

# === MAIN ===

if __name__ == "__main__":
    for project in PROJECTS:
        for version in VERSIONS:
            if project in NON_MODULAR_PROJECTS:
                # For non-modular projects, process the version folder directly
                version_path = os.path.join(PROJECTS_PATH, project, version)
                process_snapshot(version_path, version, project)
            else:
                # For modular projects, process each module separately
                modules = PROJECT_MODULES.get(project, [])
                for module in modules:
                    module_path = os.path.join(PROJECTS_PATH, project, version, module)
                    process_snapshot(module_path, version, project, module)

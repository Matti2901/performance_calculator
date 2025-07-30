# Performance Calculator

Performance Calculator is a Python and PowerShell-based tool designed to **analyze, compare, and visualize performance metrics** across multiple runs of Java-based projects. It automates the execution of tests, extraction of profiling data from JProfiler snapshots, and report generation in Excel format with clear formatting, deltas, and improvement/regression summaries.

## Features
✔ Executes all steps automatically (tests, snapshot extraction, report generation)  
✔ Supports **modular and non-modular Java projects**  
✔ Generates **Excel reports** with:
- CPU, Memory, and Object metrics
- Color-coded deltas and percentage differences
- Summaries for improvements and regressions  
✔ Integrates **JProfiler** for detailed profiling  
✔ Optional **Telegram notifications** for execution status  

## Installation
Clone the repository and install dependencies:
```bash
git clone https://github.com/Matti2901/performance_calculator.git
cd performance_calculator
pip install -r requirements.txt
```

## Usage
To execute the full workflow (tests, snapshot extraction, and report generation):
```powershell
.\scripts\runall.ps1
```
This script:
- Runs Maven tests for all configured projects and versions
- Dynamically switches Java versions
- Collects **JProfiler snapshots**
- Generates **Performance.xlsx** with calculated deltas and summaries

You can also run specific phases:
- **Extract snapshots only**:
```powershell
python SnapshotExtractor.py
```
- **Generate reports from existing CSV data**:
```bash
python performanceInitial.py
```

## Telegram Notifications
The tool supports **Telegram notifications**. To enable:
1. Create a Telegram bot and obtain the **bot token**
2. Start a chat with the bot and get the **chat ID**
3. Add the token and chat ID in:
   - `SendMessage.py`
   - PowerShell scripts (set `$notify = $true`)

## Output
- **Performance.xlsx**: Complete report across all runs
- **Summary Sheets**:
  - **Improvement** → metrics improved vs Init
  - **Regression** → metrics worsened vs Init
  -  **FinalAverage** → FinalData
- Additional Excel files for modular projects


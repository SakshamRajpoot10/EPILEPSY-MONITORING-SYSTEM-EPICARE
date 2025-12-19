import os
import re

# Define the pattern for migration files you want to delete (e.g., 0001_initial.py, 0002_initial.py, ...)
migration_pattern = re.compile(r'^(000[1-9]|00[1-9][0-9]|0[1-9][0-9][0-9])_.*\.py$')

# Get the base directory of your Django project
BASE_DIR = os.path.dirname(os.path.abspath(__file__))

# List of apps to exclude from deletion, if any
exclude_apps = []

# Iterate through each app in the project
for app in os.listdir(BASE_DIR):
    app_dir = os.path.join(BASE_DIR, app)
    
    # Skip non-directory files or excluded apps
    if not os.path.isdir(app_dir) or app in exclude_apps:
        continue
    
    # Check if 'migrations' directory exists within the app directory
    migration_dir = os.path.join(app_dir, 'migrations')
    if not os.path.exists(migration_dir):
        continue
    
    # Iterate through migration files and delete matching ones
    for migration_file in os.listdir(migration_dir):
        if migration_pattern.match(migration_file):
            file_path = os.path.join(migration_dir, migration_file)
            print(f"Deleting file: {file_path}")
            os.remove(file_path)

print("Migration files deleted successfully.")

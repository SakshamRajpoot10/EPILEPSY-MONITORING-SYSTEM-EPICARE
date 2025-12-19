
import os

def delete_pyc_files(directory, exclude_dirs=None):
    if exclude_dirs is None:
        exclude_dirs = []

    for root, dirs, files in os.walk(directory):
        # Skip directories that are in the exclude list
        dirs[:] = [d for d in dirs if d not in exclude_dirs]
        
        for file in files:
            if file.endswith('.pyc'):
                file_path = os.path.join(root, file)
                try:
                    os.remove(file_path)
                    print(f"Deleted {file_path}")
                except Exception as e:
                    print(f"Failed to delete {file_path}: {e}")

if __name__ == "__main__":
    project_dir = os.path.dirname(os.path.abspath(__file__))
    exclude_dirs = ['env']  # Add any other directories you want to exclude

    delete_pyc_files(project_dir, exclude_dirs)


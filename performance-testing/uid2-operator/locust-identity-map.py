import json
import time
import sys
from locust import User, task, between, events, TaskSet
from locust.runners import MasterRunner
from uid2_client import IdentityMapClient, IdentityMapInput

def _usage():
    print('Usage: venv/bin/python -m locust -f performance-testing/uid2-operator/locust-identity-map.py <base_url> <client_key> <client_secret>'
          , file=sys.stderr)
    sys.exit(1)


if len(sys.argv) <= 4:
    _usage()

base_url = sys.argv[1]
client_key = sys.argv[2]
client_secret = sys.argv[3]
email_count = 5000

class IdentityMapTasks(TaskSet):
    @task
    def identity_map_large_batch_task(self):
        try:
            response = self.user.identityMapClient.generate_identity_map(
                IdentityMapInput.from_emails([f"test{i}@example.com" for i in range(email_count)])
            )
            if response.is_success():
                self.user.environment.runner.stats.log_request("IdentityMap", "generate_identity_map", response.elapsed_time * 1000, len(response.response)) # Log successful request
            else:
                self.user.environment.runner.stats.log_error("IdentityMap", "generate_identity_map", f"Failed with status: {response.status}")
        except Exception as e:
            self.user.environment.runner.stats.log_error("IdentityMap", "generate_identity_map", f"Exception: {e}")
            print(f"Error in identity_map_large_batch_task: {e}")

class IdentityMapUser(User):
    wait_time = between(1, 2)

    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.identityMapClient = IdentityMapClient(
            base_url, 
            client_key,
            client_secret)
        self.tasks = [IdentityMapTasks] # Assign TaskSet

# Handle summary data
def on_locust_stop(environment, **kwargs):
    if isinstance(environment.runner, MasterRunner):
        summary = {
            "start_time": environment.runner.start_time,
            "end_time": time.time(),
            "duration": time.time() - environment.runner.start_time,
            "stats": environment.runner.stats.serialize_stats(),
        }
        with open("locust_summary.json", "w") as f:
            json.dump(summary, f, indent=4)
        print("Locust summary saved to locust_summary.json")

events.quitting.add_listener(on_locust_stop)
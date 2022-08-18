#!/usr/bin/env python
from pythonosc import udp_client
from pythonosc import osc_message_builder
import sys


client = udp_client.SimpleUDPClient('127.0.0.1', 5757)
# First argument in synth package name (subdirectory name).
# Second argument is the synth name
# Third argument is the volume.
# Fourth argument is the Amplitude control bus scale.
client.send_message("/chorus", [sys.argv[1], sys.argv[2], float(sys.argv[3]), float(sys.argv[4])])

#!/usr/bin/env python
from pythonosc import udp_client
from pythonosc import osc_message_builder
import sys


client = udp_client.SimpleUDPClient('127.0.0.1', 5757)
# First argument in synth package name (subdirectory name).
# Second argument is the synth number
# Third argument is the output audio channel.
# Fourth argument is the volume.
# Fifth argument is the Amplitude control bus scale.
client.send_message("/synth", [sys.argv[1], int(sys.argv[2]), int(sys.argv[3]), float(sys.argv[4]), float(sys.argv[5])])

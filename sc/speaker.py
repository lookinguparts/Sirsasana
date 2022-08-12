#!/usr/bin/env python
from pythonosc import udp_client
from pythonosc import osc_message_builder
import sys


client = udp_client.SimpleUDPClient('127.0.0.1', int(sys.argv[1]))
client.send_message("/speaker", [int(sys.argv[2]), float(sys.argv[3])])







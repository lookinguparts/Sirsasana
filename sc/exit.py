#!/usr/bin/env python
from pythonosc import udp_client
from pythonosc import osc_message_builder
import sys


client = udp_client.SimpleUDPClient('127.0.0.1', 5757)
client.send_message("/exit", [])


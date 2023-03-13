from flask import Flask
app = Flask(__name__)

import lavis_service.service
import lavis_service.caption
import lavis_service.tag
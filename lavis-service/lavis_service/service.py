from lavis_service import app
from .caption import caption_image
from .tag import tags_from_caption

from flask import jsonify, request

@app.route("/caption", methods=["POST"])
def caption():
    file = request.files['image']
    print(f'received file with length {file.content_length}')

    return jsonify(caption=caption_image(file))

@app.route("/tags", methods=["POST"])
def tag():
    file = request.files['image']
    print(f'received file with length {file.content_length}')
    caption = caption_image(file)

    return jsonify(tags=tags_from_caption(caption))

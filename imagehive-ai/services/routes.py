from services import app
from .lavis import caption_image, caption_images
from .nltk import tags_from_caption

from flask import jsonify, request

# LAVIS IMAGE CAPTIONING

@app.route("/caption", methods=["POST"])
def caption():
    file = request.files['image']
    print(f'received file with length {file.content_length}')

    return jsonify(caption=caption_image(file))

@app.route("/caption-multiple", methods=["POST"])
def caption_multiple():
    files = request.files.getlist("images")

    return jsonify(caption_images(files))

# NLTK GENERATING TAGS FROM CAPTION

@app.route("/tags", methods=["POST"])
def tags():
    caption = request.form['caption']
    print(f'received file with length {file.content_length}')

    return jsonify(tags=tags_from_caption(caption))
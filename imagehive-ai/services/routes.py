from services import app
from .lavis import caption_image, caption_images
from .nltk import tags_from_caption

from flask import jsonify, request


# LAVIS IMAGE CAPTIONING
@app.route("/caption", methods=["POST"])
def caption():
    single_file = request.files.get("image")
    file_list = request.files.getlist("images")
    if single_file:
        print("received single file to caption")
        return jsonify(caption=caption_image(single_file))
    elif len(file_list) > 0:
        return jsonify(caption=caption_images(file_list))
    else:
        return jsonify(error="no files sent"), 400


# NLTK GENERATING TAGS FROM CAPTION
@app.route("/tags", methods=["POST"])
def tags():
    caption = request.form["caption"]
    return jsonify(tags=tags_from_caption(caption))

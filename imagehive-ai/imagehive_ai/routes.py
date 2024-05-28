from imagehive_ai import app
from flask import jsonify, request
import ollama
from PIL import Image
import base64
import io
import time
import functools
from werkzeug.datastructures import FileStorage
from typing import Callable

_log = app.logger
MODEL = "llava-llama3"


def log_duration(func: Callable) -> Callable:
    @functools.wraps(func)
    def wrapper(*args, **kwargs):
        start_time = time.time()
        result = func(*args, **kwargs)
        duration = time.time() - start_time
        _log.info("'%s' took %ss", func.__name__, duration)
        return result

    return wrapper


def split_tags(string: str):
    return [tag.strip().lower() for tag in string.split(",")]


@log_duration
def scaled_down_base64_image(file: FileStorage):
    img = Image.open(file)
    img.thumbnail((1400, 1400))
    buffered = io.BytesIO()
    img.save(buffered, format="JPEG")
    return base64.b64encode(buffered.getvalue()).decode("utf-8")


@log_duration
def ask_image_question(file: FileStorage, question: str) -> str:
    _log.info("asking image question '%s'", question)
    image = scaled_down_base64_image(file)
    response = ollama.chat(
        model=MODEL,
        messages=[
            {
                "role": "user",
                "content": question,
                "images": [image],
            }
        ],
    )
    content = response["message"]["content"]
    _log.info("received response: '%s'", content)
    return content


@app.route("/generate/caption", methods=["POST"])
def caption():
    _log.info("received request to generate caption")
    file_list = request.files.getlist("images")
    if len(file_list) > 0:
        response = []
        for file in file_list:
            print("received single file to caption")
            caption = ask_image_question(
                file,
                "Generate a one-line caption for this image:",
            )
            caption = caption.replace('"', "").strip()

            response.append(caption)
        return jsonify(captions=response)
    else:
        return jsonify(error="no files sent"), 400


@app.route("/generate/tags", methods=["POST"])
def tags():
    _log.info("received request to generate tags")
    file_list = request.files.getlist("images")
    if len(file_list) > 0:
        response = []
        for file in file_list:
            response_text = ask_image_question(
                file,
                "Describe this image using up to ten tags, separated by commas:",
            )
            tags = split_tags(response_text)
            response.append(tags)
        return jsonify(tags=response)

    else:
        return jsonify(error="no files sent"), 400

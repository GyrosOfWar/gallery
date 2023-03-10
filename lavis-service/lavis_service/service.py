from typing import List
import torch

from lavis_service import app

from flask import jsonify, request
from lavis.models import load_model_and_preprocess
from PIL import Image

device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
caption_model, caption_vis_processors, _ = load_model_and_preprocess(name="blip_caption", model_type="base_coco", is_eval=True, device=device)
tag_model, tag_vis_processors, tag_txt_processors = load_model_and_preprocess(name="blip_vqa", model_type="vqav2", is_eval=True, device=device)

@app.route("/caption", methods=["POST"])
def caption_image():
    file = request.files['image']
    print(f'received file with length {file.content_length}')
    rgb_image = Image.open(file.stream).convert("RGB")

    preprocessed_image = caption_vis_processors["eval"](rgb_image).unsqueeze(0).to(device)
    generated_captions: List[str] = caption_model.generate({"image": preprocessed_image})
    print(f'generated captions {generated_captions}')
    caption = None
    if len(generated_captions) != 0:
        caption = generated_captions[0]

    return jsonify(caption=caption)

@app.route("/tags", methods=["POST"])
def tag_image():
    file = request.files['image']
    print(f'received file with length {file.content_length}')
    rgb_image = Image.open(file.stream).convert("RGB")

    question = "What are the defining features of this picture described in nouns?"
    image = tag_vis_processors["eval"](rgb_image).unsqueeze(0).to(device)
    question = tag_txt_processors["eval"](question)
    generated_tags: List[str] = tag_model.predict_answers(samples={"image": image, "text_input": question}, inference_method="generate")
    print(f'generated tags {generated_tags}')

    return jsonify(tags=generated_tags)

import torch

from lavis.models import load_model_and_preprocess
from PIL import Image
from typing import List

device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
caption_model, caption_vis_processors, _ = load_model_and_preprocess(name="blip_caption", model_type="base_coco", is_eval=True, device=device)

def caption_image(file):
    rgb_image = Image.open(file.stream).convert("RGB")
    preprocessed_image = caption_vis_processors["eval"](rgb_image).unsqueeze(0).to(device)
    generated_captions: List[str] = caption_model.generate({"image": preprocessed_image})
    print(f'generated captions {generated_captions}')
    caption = None
    if len(generated_captions) != 0:
        caption = generated_captions[0]
    
    return caption

def caption_images(files):
    names = []
    captions = []

    for image in files:
        names.append(image.filename)
        captions.append(caption_image(image))

    return list(zip(names,captions))
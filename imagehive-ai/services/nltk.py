import nltk
from nltk import word_tokenize, pos_tag

nltk.download('brown')
nltk.download('punkt')
nltk.download('averaged_perceptron_tagger')

def tags_from_caption(caption):
    tokens = word_tokenize(caption)
    parts_of_speech = nltk.pos_tag(tokens)
    nouns = list(filter(lambda x: x[1] == "NN" or x[1] == "NNS", parts_of_speech))
    tags = [i[0] for i in nouns]
    print(f'generated tags {tags}')

    return tags
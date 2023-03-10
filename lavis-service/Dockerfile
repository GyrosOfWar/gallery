FROM python:3.10-alpine

RUN apk update && \
    apk add build-essential python3-dev python3-setuptools make cmake && \
    apk add ffmpeg libavcodec-dev libavfilter-dev libavformat-dev libavutil-dev

COPY requirements.txt ./
RUN pip install -r ./requirements.txt

COPY . .
CMD [ "flask", "--app", "lavis_service", "run" ]
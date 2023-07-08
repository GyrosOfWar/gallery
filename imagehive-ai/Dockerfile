FROM python:3.10

WORKDIR /app

RUN apt-get update && \
    apt-get install -y build-essential python3-dev python3-setuptools make cmake \
    ffmpeg libavcodec-dev libavfilter-dev libavformat-dev libavutil-dev

COPY requirements.txt .
RUN pip install -r ./requirements.txt

COPY . .
EXPOSE 5000
CMD [ "flask", "--app", "services", "run" ]

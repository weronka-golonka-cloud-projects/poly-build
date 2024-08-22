FROM python:3.9-alpine3.17

RUN apk update \
    && apk add bash \
    && apk add curl

RUN pip install --upgrade pip

RUN adduser -D e2etest
USER e2etest
WORKDIR /home/app

COPY --chown=e2etest:e2etest requirements.txt .

RUN pip install --user -r requirements.txt

ENV AWS_ACCESS_KEY_ID="test"
ENV AWS_SECRET_ACCESS_KEY="test"
ENV AWS_DEFAULT_REGION="us-east-1"

COPY --chown=e2etest:e2etest src/ .

CMD ["python", "main.py"]
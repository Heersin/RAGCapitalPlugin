FROM python:3.11-slim

ENV PYTHONDONTWRITEBYTECODE=1 \
    PYTHONUNBUFFERED=1 \
    UV_LINK_MODE=copy \
    HOST=0.0.0.0 \
    PORT=8000

WORKDIR /app

RUN pip install --no-cache-dir uv

COPY pyproject.toml uv.lock README.md ./
RUN uv sync --frozen --no-dev

COPY src ./src
COPY docs ./docs
COPY eval ./eval
COPY sample ./sample
COPY scripts ./scripts
COPY docker ./docker

RUN mkdir -p runtime \
    && chmod +x docker/start.sh scripts/raglocal.py

EXPOSE 8000

CMD ["./docker/start.sh"]

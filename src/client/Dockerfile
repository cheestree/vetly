FROM node:20-bookworm-slim

WORKDIR /app

ENV EXPO_NO_TELEMETRY=1
ENV CI=1

COPY package*.json ./

RUN npm ci --omit=optional \
  && npm cache clean --force

COPY . .

EXPOSE 8081 19000 19001 19002

CMD ["npx", "expo", "start", "--host", "lan"]
FROM node:18-alpine
RUN npm install -g @expo/cli
WORKDIR /app

COPY package*.json ./
RUN npm install

COPY . .

EXPOSE 19000 19001 19006
CMD ["npx", "expo", "start", "--web"]
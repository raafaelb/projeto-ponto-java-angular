# ============================ #
#         DESENVOLVIMENTO      #
# ============================ #
FROM node:18-alpine AS dev
WORKDIR /app

# Instala o Angular CLI GLOBALMENTE
RUN npm install -g @angular/cli

# Copia package.json primeiro (para cache)
COPY package*.json ./

# Instala dependências
RUN npm install

EXPOSE 4200
CMD ["ng", "serve", "--host", "0.0.0.0", "--poll", "1000"]

# ============================ #
#         PRODUÇÃO             #
# ============================ #
FROM node:18-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

FROM nginx:alpine AS production
COPY --from=build /app/dist/frontend/browser /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf
EXPOSE 80
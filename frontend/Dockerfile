# ====== BUILD STAGE ======
FROM node:20-alpine AS builder

WORKDIR /app

COPY package*.json ./
RUN npm install

COPY . .

# Build Angular
RUN npm run build

# ====== RUNTIME STAGE ======
FROM nginx:alpine

# Copiar build a nginx
COPY --from=builder /app/dist /usr/share/nginx/html

# Copiar config opcional
# COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
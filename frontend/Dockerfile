# Stage 1
FROM node:18-alpine as builder
WORKDIR /app
COPY . .
RUN npm install
RUN npm run build

# Stage 2
FROM nginx:alpine
COPY --from=builder /app/dist/frontend /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

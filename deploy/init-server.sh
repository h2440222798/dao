#!/bin/bash
# ============================
# 道家五行 - 云服务器初始化脚本
# 首次部署时在云服务器上执行
# ============================
set -e

DEPLOY_DIR="/opt/daojiawuxing"
GITHUB_USER="h2440222798"  # 替换为你的 GitHub 用户名

echo "===== 1. 创建部署目录 ====="
mkdir -p $DEPLOY_DIR
cd $DEPLOY_DIR

echo "===== 2. 安装 Docker（如未安装）====="
if ! command -v docker &> /dev/null; then
    curl -fsSL https://get.docker.com | sh
    systemctl enable docker
    systemctl start docker
    echo "Docker 安装完成"
else
    echo "Docker 已安装: $(docker --version)"
fi

echo "===== 3. 创建 docker-compose.yml ====="
cat > docker-compose.yml << 'COMPOSE_EOF'
services:
  backend:
    image: ${BACKEND_IMAGE:-ghcr.io/h2440222798/daojiawuxing-backend}:${IMAGE_TAG:-latest}
    container_name: daojiawuxing-backend
    restart: unless-stopped
    environment:
      - SPRING_DATASOURCE_URL=jdbc:mysql://${DB_HOST:-172.19.0.1}:${DB_PORT:-3306}/${DB_NAME:-daojiawuxing}?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8&connectTimeout=5000&socketTimeout=60000
      - SPRING_DATASOURCE_USERNAME=${DB_USERNAME:-daojiawuxing}
      - SPRING_DATASOURCE_PASSWORD=${DB_PASSWORD}
      - DB_HOST=${DB_HOST:-172.19.0.1}
      - DB_PORT=${DB_PORT:-3306}
      - DB_NAME=${DB_NAME:-daojiawuxing}
      - DB_USERNAME=${DB_USERNAME:-daojiawuxing}
      - DB_PASSWORD=${DB_PASSWORD}
      - REDIS_HOST=${REDIS_HOST:-localhost}
      - REDIS_PORT=${REDIS_PORT:-6379}
      - REDIS_PASSWORD=${REDIS_PASSWORD:-}
      - REDIS_DATABASE=${REDIS_DATABASE:-1}
    deploy:
      resources:
        limits:
          cpus: "1.5"
          memory: 768M
        reservations:
          memory: 256M
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8101/api/doc.html"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 60s
    networks:
      - app-network

  frontend:
    image: ${FRONTEND_IMAGE:-ghcr.io/your-username/daojiawuxing-frontend}:${IMAGE_TAG:-latest}
    container_name: daojiawuxing-frontend
    restart: unless-stopped
    ports:
      - "${APP_PORT:-8080}:80"
    deploy:
      resources:
        limits:
          cpus: "0.5"
          memory: 128M
        reservations:
          memory: 32M
    depends_on:
      backend:
        condition: service_started
    networks:
      - app-network

networks:
  app-network:
    driver: bridge
    ipam:
      config:
        - subnet: 172.19.0.0/24
          gateway: 172.19.0.1
COMPOSE_EOF

echo "===== 4. 创建 .env 配置文件 ====="
if [ ! -f .env ]; then
    cat > .env << ENV_EOF
# 镜像配置（替换 your-username）
FRONTEND_IMAGE=ghcr.io/${GITHUB_USER}/daojiawuxing-frontend
BACKEND_IMAGE=ghcr.io/${GITHUB_USER}/daojiawuxing-backend
IMAGE_TAG=latest

# 应用端口
APP_PORT=8080

# 数据库配置
SPRING_DATASOURCE_URL=jdbc:mysql://172.19.0.1:3306/daojiawuxing?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8&connectTimeout=5000&socketTimeout=60000
SPRING_DATASOURCE_USERNAME=daojiawuxing
SPRING_DATASOURCE_PASSWORD=your_mysql_password
DB_HOST=172.19.0.1
DB_PORT=3306
DB_NAME=daojiawuxing
DB_USERNAME=daojiawuxing
DB_PASSWORD=your_mysql_password

# Redis（可选）
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=
REDIS_DATABASE=1
ENV_EOF
    echo ".env 文件已创建，请编辑填入实际数据库密码："
    echo "  vi $DEPLOY_DIR/.env"
else
    echo ".env 文件已存在，跳过"
fi

echo ""
echo "===== 初始化完成 ====="
echo "请执行以下步骤："
echo "1. 编辑 .env 文件填入实际配置: vi $DEPLOY_DIR/.env"
echo "2. 确保 MySQL 已创建 daojiawuxing 数据库"
echo "3. 在 GitHub Actions 中手动触发 Build and Deploy 工作流"

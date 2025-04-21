#!/bin/bash
set -e

# 删除现有构建器
docker buildx rm multiarch-builder || true

# 创建并使用新的 buildx 构建器
docker buildx create --name multiarch-builder --driver docker-container --use
docker buildx inspect multiarch-builder --bootstrap

# 构建并推送多平台镜像
docker buildx bake -f docker-bake.hcl --push

echo "多平台镜像构建完成并已推送至仓库" 
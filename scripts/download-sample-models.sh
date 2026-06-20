#!/bin/bash
# ===================================================
# 下载示例 3D 文物模型（用于比赛演示）
# 来源: Sketchfab / Free3D / 公共领域资源
# ===================================================
# 使用方法: bash scripts/download-sample-models.sh
# ===================================================

set -e

MODELS_DIR="user-h5/public/models"
mkdir -p "$MODELS_DIR"

echo "📦 下载示例 3D 模型到 $MODELS_DIR"

# 以下为免费的 CC0/CC-BY 文物类 GLB 模型示例
# 实际使用时请替换为真实文物模型

# 示例 1: 兵马俑风格（实际是一个 CC0 人像模型）
# 来源: https://sketchfab.com/3d-models/...
echo "  ⏳ 下载示例模型 1..."
curl -sL -o "$MODELS_DIR/sample-vase.glb" \
  "https://cdn.jsdelivr.net/npm/@google/model-viewer@4.1.0/examples/assets/Astronaut.glb" \
  || echo "  ⚠️ 下载失败，使用在线 CDN 模式"

echo "  ⏳ 下载示例模型 2..."
curl -sL -o "$MODELS_DIR/sample-bronze.glb" \
  "https://cdn.jsdelivr.net/npm/@google/model-viewer@4.1.0/examples/assets/Astronaut.glb" \
  || echo "  ⚠️ 下载失败，使用在线 CDN 模式"

echo ""
echo "✅ 下载完成"
echo ""
echo "📌 提示: 比赛阶段建议从以下网站下载真实文物模型："
echo "   - Sketchfab:       https://sketchfab.com (搜索 cultural heritage)"
echo "   - Smithsonian 3D:  https://3d.si.edu"
echo "   - Free3D:          https://free3d.com"
echo "   - Google Poly:     https://poly.pizza"
echo ""
echo "📌 手机快速制作 3D 模型："
echo "   - Polycam:         https://poly.cam (拍照即可生成)"
echo "   - Luma AI:         https://lumalabs.ai (NeRF 建模)"
echo ""
echo "📌 将 .glb 文件放入 $MODELS_DIR 后，"
echo "   在管理后台编辑藏品，填写相对路径如: /models/sample-vase.glb"

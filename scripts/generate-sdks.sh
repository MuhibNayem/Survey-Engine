#!/bin/bash

# Configuration
SPEC_PATH="src/main/resources/static/openapi.yaml"
OUTPUT_DIR="sdks"

# Helper to generate SDK
generate_sdk() {
  local language=$1
  local generator=$2
  local target_dir="$OUTPUT_DIR/$language"

  echo ">>> Generating $language SDK..."
  mkdir -p "$target_dir"
  
  # Using npx to ensure we have openapi-generator-cli
  npx @openapitools/openapi-generator-cli generate \
    -i "$SPEC_PATH" \
    -g "$generator" \
    -o "$target_dir" \
    --skip-validate-spec \
    --additional-properties=disallowAdditionalPropertiesIfNotPresent=false
}

# Generate SDKs
generate_sdk "java" "java"
generate_sdk "go" "go"
generate_sdk "python" "python"

# TS with package.json
echo ">>> Generating typescript SDK..."
mkdir -p "$OUTPUT_DIR/typescript"
npx @openapitools/openapi-generator-cli generate \
  -i "$SPEC_PATH" \
  -g "typescript-fetch" \
  -o "$OUTPUT_DIR/typescript" \
  --skip-validate-spec \
  --additional-properties=npmName=@survey-engine/sdk,npmVersion=1.1.0,supportsES6=true,snapshot=false

echo ">>> All SDKs generated successfully in $OUTPUT_DIR/"

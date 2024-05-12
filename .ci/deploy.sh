#!/usr/bin/env bash

# PS4="++++++++++ "
# set -o xtrace

IMAGE_TAG=$(echo ${BITBUCKET_BRANCH}-${BITBUCKET_COMMIT::7} | tr '/_' '-')

wget -qO /usr/local/bin/yq https://github.com/mikefarah/yq/releases/latest/download/yq_linux_amd64
chmod a+x /usr/local/bin/yq

git config --global user.email "pipelines@powtoon.com"
git config --global user.name "Arena BE Pipeline"


while [[ $# -gt 0 ]]; do
    case "$2" in
        develop)
            git clone --branch=master --depth 1 "https://x-token-auth:${REPO_ACCESS_TOKEN}@bitbucket.org/powtoon/gitops-dev.git" gitops/
            cd gitops/
            yq eval ".variables.arena_image_tag = \"${IMAGE_TAG}\"" -i ./dev/develop.yaml
            git commit -a -m "$IMAGE_TAG [skip ci]"
            git push
            ;;
        staging-ec)
            git clone --branch=master --depth 1 "https://x-token-auth:${REPO_ACCESS_TOKEN}@bitbucket.org/powtoon/gitops-ec-staging.git" gitops/
            cd gitops/
            yq eval ".variables.arena_image_tag = \"${IMAGE_TAG}\"" -i ./staging/staging-ec.yaml
            git commit -a -m "$IMAGE_TAG"
            git push
            ;;
        prod-ec)
            git clone --branch=master --depth 1 "https://x-token-auth:${REPO_ACCESS_TOKEN}@bitbucket.org/powtoon/gitops-ec-prod.git" gitops/
            cd gitops/
            yq eval ".variables.arena_image_tag = \"${IMAGE_TAG}\"" -i ./prod/prod-ec.yaml
            git commit -a -m "$IMAGE_TAG"
            git push
            ;;
    esac
    shift
done
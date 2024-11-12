#!/usr/bin/env bash

ARENA_BACKEND_VERSION=$(echo ${BITBUCKET_BRANCH}-${BITBUCKET_COMMIT::7} | tr '/_' '-')

# Parse arguments
while [[ "$#" -gt 0 ]]; do
    case $1 in
        copy_policies) ;;
        --src=*) SRC="${1#*=}" ;;
        --dest=*) DEST="${1#*=}" ;;
        *) echo "Unknown parameter passed: $1"; exit 1 ;;
    esac
    shift
done

if [[ -z "$SRC" || -z "$DEST" ]]; then
    echo "Error: Both --src and --dest arguments are required."
    exit 1
fi

# We don't want to copy policies on rollback, only on planned deployments.
rollback_checker () {
    cd gitops/
    if git log --author="Arena BE Pipeline" --pretty=format:%s | head -n 5 | tail -n 4 | grep -q "$ARENA_BACKEND_VERSION"; then
        echo "Running rollback to $ARENA_BACKEND_VERSION, skip copy policies"
        exit 0
    fi
}


rollback_checker

# Copying Permitio policies 
echo -e "\n########## Copying Permitio policies: $SRC -> $DEST\n"

sleep 60

apt-get update > /dev/null && apt-get install -y nodejs npm git > /dev/null
git clone https://x-token-auth:${RNP_REPO_ACCESS_TOKEN}@bitbucket.org/powtoon/rnp-utils.git
cd rnp-utils
npm ci

node hub/utils/copy-env.js "$SRC" "$DEST"

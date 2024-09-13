#!/bin/sh

set -e

make_migrations() {
    echo -e "\n########## Applying SQL migrations for the release:\n" 
    cat >> liquibase.properties << EOF
changeLogFile: db/changelog-root.xml    
url: jdbc:postgresql://$POSTGRES_HOST:5432/$POSTGRES_DB_NAME
username: $POSTGRES_USER
password: $POSTGRES_PASS
liquibaseSchemaName: arena_hub
EOF
    mvn liquibase:update -Dliquibase.verbose=true
}

copy_permitio_policies() {
    echo -e "\n########## Copying Permitio policies:\n"
    install_deps() {
        # Install dependencies, clone rnp-utils repo
        apk add --update nodejs npm git
        git clone https://x-token-auth:${RNP_REPO_ACCESS_TOKEN}@bitbucket.org/powtoon/rnp-utils.git
        cd rnp-utils
        npm ci
    }

    if [ "${K8S_NAMESPACE}" == "develop" ]; then
        echo -e "\n########## Copying Permitio policies from dev-wip to dev-stable:\n"
        install_deps
        node hub/utils/copy-env.js dev-wip dev-stable
    elif [ "${K8S_NAMESPACE}" == "staging-ec" ]; then
        echo -e "\n########## Copying Permitio policies from dev to staging:\n"
        install_deps
        node hub/utils/copy-env.js dev-stable staging
    elif [ "${K8S_NAMESPACE}" == "prod-ec" ]
    then
        echo -e "\n########## Copying Permitio policies from dev to production:\n"
        install_deps
        node hub/utils/copy-env.js staging production
    else
        echo "${K8S_NAMESPACE} is no in the list"
    fi
}

do_release() {
    echo -e "\n########## Doing entire release:\n"
    make_migrations
    copy_permitio_policies 
}

# Run certain functions on demand
if [[ $# -eq 0 ]] ; then
    echo "No arguments"
    exit 0
fi

while [[ $# -gt 0 ]]; do
    case "$1" in
        --do-release)
            do_release
            ;;
        --make_migrations)
            make_migrations
            ;;
        --copy-permitio-policies)
            copy_permitio_policies
            ;;
        *)
            # Handle any unrecognized flags
            echo "Unrecognized flag: $1"
            exit 1
            ;;
    esac
    shift
done
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

do_release() {
    echo -e "\n########## Doing entire release:\n"
    make_migrations 
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
        *)
            # Handle any unrecognized flags
            echo "Unrecognized flag: $1"
            exit 1
            ;;
    esac
    shift
done
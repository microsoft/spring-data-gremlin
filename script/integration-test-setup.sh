#!/bin/bash

endpoint_pr="$GREMLIN_ENDPOINT_PR"
username_pr="$GREMLIN_USERNAME_PR"
password_pr="$GREMLIN_PASSWORD_PR"

endpoint_push="$GREMLIN_ENDPOINT_PUSH"
username_push="$GREMLIN_USERNAME_PUSH"
password_push="$GREMLIN_PASSWORD_PUSH"

git_branch=$(git branch | grep "*" | cut -d ' ' -f2-)

if [[ "$git_branch" = *"FETCH_HEAD"* ]]; then
    endpoint=$endpoint_pr
    username=$username_pr
    password=$password_pr
else
    endpoint=$endpoint_push
    username=$username_push
    password=$password_push
fi

cat << EOF
gremlin.endpoint=$endpoint
gremlin.port=443
gremlin.username=$username
gremlin.password=$password

EOF


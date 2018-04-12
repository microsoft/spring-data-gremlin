#1/bin/bash

endpoint="$GREMLIN_ENDPOINT"
username="$GREMLIN_USERNAME"
password="$GREMLIN_PASSWORD"

cat << EOF
gremlin.endpoint=$endpoint
gremlin.port=443
gremlin.username=$username
gremlin.password=$password

EOF


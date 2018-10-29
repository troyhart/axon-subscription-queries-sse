#!/usr/bin/env bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

cd ${DIR}

./mvnw clean install
docker build -t sse-baskets-api:latest .

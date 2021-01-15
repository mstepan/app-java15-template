#!/usr/bin/env bash
docker-compose up --scale redis-sentinel=3 -d

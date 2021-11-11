#!/bin/bash
docker kill cart-service > /dev/null 2>&1
docker rm cart-service > /dev/null 2>&1
docker-compose up -d
echo "STG: $(curl -ks http://ggnanasekaran.com:8000)"
echo
echo "PRD1: $(curl -ks http://ggnanasekaran.com:9001)"
echo
echo "PRD2: $(curl -ks http://ggnanasekaran.com:9002)"
echo

export DOCKER_HOST=ssh://gnanam@ggnanasekaran.com
docker ps

group "default" {
  targets = ["app"]
}

target "app" {
  context = "."
  dockerfile = "Dockerfile"
  tags = ["registry.fjhdream.lol/drinkup/drinkup:latest"]
  platforms = ["linux/amd64", "linux/arm64"]
} 
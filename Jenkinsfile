node {
  stage 'Checkout'
  git url: 'https://github.com/mrudaysharma/SpringBootShoppingCart.git'

  stage 'build'
  docker.build('cart-service')

  stage 'deploy'
  sh './deploy.sh'
}
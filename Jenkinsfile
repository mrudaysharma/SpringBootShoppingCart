node {
  stage 'Checkout'
  git url: 'https://github.com/mrudaysharma/SpringBootShoppingCart.git'
  
  stage 'build'
  withCredentials([usernamePassword( credentialsId: 'docker-hub-credentials', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
  docker.withRegistry('https://registry.hub.docker.com','docker-hub-credentials')
  {
     docker.build('cart-service')
  }
  }
  stage 'deploy'
  sh './deploy.sh'
}

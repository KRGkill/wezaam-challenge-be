




#Running the application

### --Follow the following steps to run the app--

###### Compile the code and generate the jar:
  `mvn clean install`
____

###### Build the docker image:
`docker build -t wezaam/wezaam-challenge-be .`
____

###### Then run the docker container with:
`docker run -p 80:7070 wezaam/wezaam-challenge-be`
____

###### See the Api service:
http://localhost/swagger-ui.html
____

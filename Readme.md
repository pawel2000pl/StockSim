# StockSim
Created by Paweł Bielecki, 2023

## Configuration

Find a file *src/main/webapp/WEB-INF/resources.example.xml*.
Copy the file as *src/main/webapp/WEB-INF/resources.xml*.
Edit the file with database credentials.
The database could be initialized with commands in the file *init-db.sql*.
Change the password. It could be generated with the following command:
```
head -c 65536 /dev/random | sha1sum
```

## How to build
```
docker build -t stocksim .
```

## How to run
```
docker run -it -p 8080:8080 -p 8081:8081 stocksim
```

## Fast build and run
```
docker build -t stocksim . && docker run -it -p 8080:8080 -p 8081:8081 stocksim
```

## How to use

Open your browser on http://localhost:8080


# Build the game on folder ```build``` using Unity

# Build image 

```shell script
docker build . -t one-tap-soccer
```

```shell script
docker tag one-tap-soccer:latest viniciusfcf/one-tap-soccer:latest
```

```shell script
docker push viniciusfcf/one-tap-soccer:latest
```

or

```shell script
docker tag one-tap-soccer:latest quay.io/vflorent/one-tap-soccer:latest
```

```shell script
docker push quay.io/vflorent/one-tap-soccer:latest
```
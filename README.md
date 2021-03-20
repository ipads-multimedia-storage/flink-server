# flink server

This is the process part of the project, implemented by Flink

### Quick Start

This project is build by `gradle`.

to use `opencv` in this project,  you should move `libopencv_java4.dll` （find under directory `common/src/main/resources`）to `JAVA_HOME/bin` on your machine

- `JAVA_HOME` is where you put java jdk

<img src="https://gitee.com/fangnuowu/img/raw/master///20210320135948.png" alt="image-20210320135946989" style="zoom:67%;" />

#### To run the project with `IDEA`

- use `gradle build` to build 

  <img src="https://gitee.com/fangnuowu/img/raw/master///20210320123725.png" alt="image-20210320123724213" style="zoom: 67%;" />

- click `run` to start

  <img src="https://gitee.com/fangnuowu/img/raw/master///20210320123520.png" alt="image-20210320114404754" style="zoom:67%;" />

#### To run the project in`TERMINAL`

```
$ gradlew build
BUILD SUCCESSFUL in 2s
12 actionable tasks: 2 executed, 10 up-to-date

$ gradlew run
<=========----> 75% EXECUTING
```

### File Structure

The struct of this project is

```
├── common
  ├── src
	├── main
	  ├── java/flink
	  │ ├── config
	  │	  ├── CONFIG.java -- parameter used during process
	  │	├── operator -- apache flink operators
	  │	  ├── InformationToOutput.java -- trans detect info to message to client
      │   ├── TransferImage.java -- translate image to detect info
	  │	├── sink
	  │	  ├── BandwidthSerialize.java -- give bandwidth info to client
	  │	  ├── MessageSerialize.java -- give objects info to client
	  │	  ├── PravegaRouter.java -- 
	  │	  ├── PravegaSerialize.java -- 
   	  │	├── source
	  │	├── tracker // detect and tracking related
	  │	├── types // defined types
	  │	├── utils // some useless 
	  ├── resources
	  │ ├── config.properties -- global properties, e.g. ports
	  │ ├── log4j.properties -- log related properties, e.g. where to store log
	  ├── StreamingJob -- main function
	├── test
```

### CONFIG

- `config.properties`

  you properties you might want to change

  ```properties
  socket.sink.hostname = localhost # 192.168.137.174 (the ip of respberry )
  enablePravega = false # true to enable Pravega
  ```

- config/CONFIG.java

  - this defines the min and max object area, we can only detect object with size in this range

    ```java
    public static double MIN_BLOB_AREA = 5000;
    public static double MAX_BLOB_AREA = 100000;
    ```

  
  - this decide welter to show information to user
  
    ```java
    public static boolean _draw_image_flag = true;
    ```


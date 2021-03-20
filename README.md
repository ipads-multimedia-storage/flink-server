# flink server

This is the process part of the project, implemented by Flink

### Quick Start

This project is build by `gradle`.

To run the project with `IDEA`

- use `gradle build` to build 

  <img src="https://gitee.com/fangnuowu/img/raw/master///20210320123725.png" alt="image-20210320123724213" style="zoom: 67%;" />

- click `run` to start

  <img src="https://gitee.com/fangnuowu/img/raw/master///20210320123520.png" alt="image-20210320114404754" style="zoom:67%;" />

### Struct

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
	  │	  ├── PravegaRouter.java -- allocate stream router key for Pravega
	  │	  ├── PravegaSerialize.java -- serialize stream for Pravega
   	  │	├── source
	  │	  ├── BandwidthReduce.java -- for bandwidth detection
	  │	  ├── OpenCVSocketSource.java -- receive video stream from client
	  │	  ├── SourceData.java -- source data type TODO?: move it to "types" package
	  │	  ├── TimeAssigner.java -- assign timestamp to stream
	  │	├── tracker // detect and tracking related
	  │	├── types // defined types
	  │	├── utils // some useless 
	  ├── resources
	  │ ├── config.properties -- global properties, e.g. ports
	  │ ├── log4j.properties -- log related properties, e.g. where to store log
	  ├── StreamingJob -- main function
	├── test
```


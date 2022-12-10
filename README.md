# Wine_Quality_Prediction
Github Link : https://github.com/kinjalamin28/Wine_Quality_Prediction

Docker Hub Link : https://hub.docker.com/repository/docker/kinjalamin28/wine_quality

How to set up cluster and run application using Hadoop, spark and docker :
1.	Login to AWS Learner Lab. Start lab. Click on AWS. 
2.	On the AWS Management Console, search for EMR and create a cluster.
3.	Select configurations and click on create cluster.
4.	Change the inbound rules. 
5.	Upload/copy files to EMR using winSCP.
6.	Connect putty using following instruction.
    Start PuTTY.
    In the Category list, click Session.
    In the Host Name field, type hadoop@ec2-18-212-153-147.compute-1.amazonaws.com
    In the Category list, expand Connection > SSH, and then click Auth.
    For Private key file for authentication, click Browse and select the private key file (amin.ppk) used to launch the cluster.
    Click Open.
    Click Yes to dismiss the security alert.
7.	Copy all files to HDFS
[hadoop@ip-172-31-47-150 ~]$ hadoop fs -put * .
[hadoop@ip-172-31-47-150 ~]$ hadoop fs -put * .
put: `TrainingDataset.csv': File exists
put: `ValidationDataset.csv': File exists
put: `wine_quality_prediction.jar': File exists
[hadoop@ip-172-31-47-150 ~]$
8.	Submit the spark job to run in cluster mode using following command : 
    spark-submit –deploy-mode cluster –class com.cloudcomputing.winequalityprediction.PredictionModelTrainer wine_quality_prediction.jar hdfs://ip-172-31-47-   150.ec2.internal:8020/user/ adoop/TrainingDataset.csv hdfs://ip-172-31-47-150.ec2.internal:8020/user/ adoop/ValidationDataset.csv hdfs://ip-172-31-47-  150.ec2.internal:8020/user/ adoop/model/
9.	To run the prediction application test : 
spark-submit –class com.cloudcomputing.winequalityprediction.PredictionTesting wine_quality_prediction.jar hdfs://ip-172-31-47-150.ec2.internal:8020/user/ adoop/ValidationDataset.csv hdfs://ip-172-31-47-150.ec2.internal:8020/user/ adoop/model/

How to set up spark cluster using docker and run application:

1.	Clone the repository : 
git clone https://github.com/kinjalamin28/Wine_Quality_Prediction.git
2.	Run : cd Wine_Quality_Prediction
3.	Add the test file into the data directory : TestDataset.csv
4.	To create a spark cluster run : docker-compose up –d 
5.	To copy files to HDFS run:
docker cp data/model spark-master:/opt/workspace && docker cp data/TestDataset.csv  spark-master:/opt/workspace/TestDataset.csv to copy files to HDFS.
6.	To predict the test file and build model : 
docker run --rm -it --network wine-quality-prediction-master_default -v hadoop-distributed-file-system:/opt/workspace --name wine_quality_prediction_test --link spark-master:spark-master kinjalamin28/wine_quality:tag1


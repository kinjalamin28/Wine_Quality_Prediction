FROM winequality/spark-maven-template:3.1.2-hadoop3.2

MAINTAINER KINJAL AMIN

ENV SPARK_APPLICATION_MAIN_CLASS com.cloudcomputing.winequalityprediction.PredictionTesting

ENV SPARK_APPLICATION_JAR_NAME Wine-Quality-Prediction-1.0

ENV SPARK_APPLICATION_ARGS "file:///opt/workspace/TestDataset.csv file:///opt/workspace/model"

VOLUME /opt/workspace



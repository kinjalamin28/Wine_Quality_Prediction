package com.cloudcomputing.winequalityprediction;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class PredictionTesting {

    public static void main(String[] args) {

        Logger.getLogger("org").setLevel(Level.ERROR);

        if (args.length < 2) {
            System.err.println("Required args are missing");
            System.exit(1);
        }

        final String TEST_DATASET = args[0];
        final String SAVED_MODEL = args[1];

        SparkSession spark = new SparkSession.Builder()
                .appName("Wine Quality Prediction").getOrCreate();

        PredictionModelTrainer predictionModelTrainer = new PredictionModelTrainer();

        // Load model
        PipelineModel model = PipelineModel.load(SAVED_MODEL);

        // read and transform test data in vector format
        Dataset<Row> testDataSet = predictionModelTrainer.readAndTransformDataSet(spark, TEST_DATASET);

        // feed into the model
        Dataset<Row> predictedDataSet = model.transform(testDataSet);
        predictedDataSet.show();

        // evaluate performance using absolute mean error
        predictionModelTrainer.evaluatePredictionPerformance(predictedDataSet);
    }
}

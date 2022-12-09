package com.cloudcomputing.winequalityprediction;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.ml.evaluation.RegressionEvaluator;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.ml.regression.LinearRegression;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.io.IOException;
import java.util.HashMap;

public class PredictionModelTrainer {

    public static void main(String[] args) {

        Logger.getLogger("org").setLevel(Level.ERROR);

        if (args.length < 3) {
            System.err.println("Required args are missing");
            System.exit(1);
        }

        final String TRAINING_DATASET = args[0];
        final String VALIDATION_DATASET = args[1];
        final String MODEL_PATH = args[2];

        PredictionModelTrainer app = new PredictionModelTrainer();
        SparkSession spark = new SparkSession.Builder()
                .appName("Wine Quality Model").getOrCreate();

        Dataset<Row> finalDataSet = app.readAndTransformDataSet(spark, TRAINING_DATASET);

        //run dataset against linear regression - Build model
        LinearRegression linearRegression = new LinearRegression().setMaxIter(20)
                .setRegParam(0).setFeaturesCol("features").setLabelCol("quality");

        //Create pipeline and execute model creation
        Pipeline pipeline = new Pipeline().setStages(new PipelineStage[]{linearRegression});
        PipelineModel model = pipeline.fit(finalDataSet);

        // Load and transform validation data set
        Dataset<Row> validationDataSet = app.readAndTransformDataSet(spark, VALIDATION_DATASET);

        //Make prediction
        Dataset<Row> predictions = model.transform(validationDataSet);
        predictions.show();

        // Evaluate Model performance
        app.evaluatePredictionPerformance(predictions);

        //Save the developed model
        try{
            model.write().overwrite().save(MODEL_PATH);
        }catch (IOException e){
            System.out.println("Error : "+e.getMessage());
        }

    }

    public void evaluatePredictionPerformance(Dataset<Row> testData){
        RegressionEvaluator regressionEvaluator = new RegressionEvaluator()
                .setLabelCol("quality").setPredictionCol("prediction").setMetricName("mae");
        double absoluteMeanError = regressionEvaluator.evaluate(testData);
        System.out.println("Mean absolute error:" +absoluteMeanError);
    }

    public Dataset<Row> readAndTransformDataSet(SparkSession spark, String fileName){

        HashMap<String, String> options = new HashMap<>();
        options.put("delimiter", ";");
        options.put("inferSchema", "true");
        options.put("header", "true");

        // read the csv into memory
        Dataset<Row> rowDataset = spark.read().options(options).csv(fileName);

        //data cleansing
        Dataset<Row> cleanDataSet = rowDataset.dropDuplicates();

        //collect feature columns (Independent Data)
        Dataset<Row> featureColumns = cleanDataSet.select("fixed acidity", "volatile acidity", "citric acid", "residual sugar",
                "chlorides", "free sulfur dioxide", "total sulfur dioxide", "density", "pH", "sulphates", "alcohol");

        //Aggregate feature column into single
        VectorAssembler vectorAssembler = new VectorAssembler().setInputCols(featureColumns.columns()).setOutputCol("features");

        // transform dataset in vector type
        Dataset<Row> finalDataSet = vectorAssembler.transform(cleanDataSet).select("features", "quality").cache();

        return finalDataSet;
    }

}

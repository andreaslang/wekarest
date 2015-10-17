package wekarest.service

import wekarest.model.ClassificationResult
import weka.classifiers.Classifier
import weka.classifiers.Evaluation
import weka.core.Instances

abstract class ClassificationService {

    abstract ClassificationResult classify(Instances dataSet);

    ClassificationResult classify(Classifier classifier, Instances trainingSet, Instances testSet) {
        classifier.buildClassifier(trainingSet)
        def evaluation = new Evaluation(trainingSet);
        evaluation.evaluateModel(classifier, testSet);
        return new ClassificationResult(
                summary: evaluation.toSummaryString("\nResults\n======\n", false),
                details: evaluation.toClassDetailsString()
        )
    }

    static List splitIntoTrainingAndTestSet(Instances dataSet, double splitRatio = 0.75) {
        def totalSize = dataSet.numInstances()
        int trainingSize = totalSize * splitRatio
        int testSize = totalSize - trainingSize
        def trainingSet = new Instances(dataSet, 0, trainingSize)
        def testSet = new Instances(dataSet, trainingSize, testSize)
        return [trainingSet, testSet]
    }

}
